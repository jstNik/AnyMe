package com.example.anyme.repositories

import com.example.anyme.domain.mal_dl.MalAnimeDL
import com.example.anyme.domain.mal_dl.NextEpisode
import com.example.anyme.utils.RangeMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.jsoup.Jsoup
import java.util.Calendar
import kotlin.time.Duration.Companion.seconds

open class EpisodeInfoScraper(
   private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

   suspend fun scrapeEpisodesType(malAnime: MalAnimeDL): MalAnimeDL{
         val episodesList = Jsoup
            .connect(
               "https://www.google.com/search?q=${
                  malAnime.alternativeTitles.en.ifBlank { malAnime.title }.replace("\"", "")
               }&as_oq=&as_eq=&as_nlo=&as_nhi=&lr=&cr=&as_qdr=all&as_sitesearch=animefillerlist.com&as_occt=any&as_filetype=&tbs="
            )
            .timeout(10000)
            .get()
            .select("a")
            .filter { element ->
               element.attr("href").startsWith("https://www.animefillerlist.com") &&
                  !element.attr("href").startsWith("https://translate.google.com")
            }

         val link = episodesList.first().attr("href")
         val animeFillerListHtml = Jsoup.connect(link).timeout(10000).get()
         val epOffset = animeFillerListHtml
            .select("tbody tr")
            .first { tr ->
               tr.select("td.Date").first()?.text() == malAnime.startDate
            }.select("td.Number")
            .text()
            .toInt() - 1

         val mangaCanonList = animeFillerListHtml.select("div.manga_canon a").map {
            it to MalAnimeDL.EpisodesType.MangaCanon
         }
         val mixedCanonList = animeFillerListHtml.select("div.mixed_canon\\/filler a").map {
            it to MalAnimeDL.EpisodesType.MixedMangaCanon
         }
         val animeCanonList = animeFillerListHtml.select("div.anime_canon a").map {
            it to MalAnimeDL.EpisodesType.AnimeCanon
         }
         val fillerList = animeFillerListHtml.select("div.filler a").map {
            it to MalAnimeDL.EpisodesType.Filler
         }
         val episodesType = RangeMap(mutableMapOf<IntRange, MalAnimeDL.EpisodesType>())
         (mangaCanonList + mixedCanonList + animeCanonList + fillerList).forEach forEach@{
            val firstInt: Int
            val secondInt: Int
            val string = it.first.text()
            if (string.contains("-")) {
               firstInt = string.substringBefore("-").toInt() - epOffset
               secondInt = string.substringAfter("-").toInt() - epOffset
            } else {
               firstInt = string.toInt() - epOffset
               secondInt = firstInt
            }
            if (malAnime.numEpisodes != 0) {
               if (firstInt > malAnime.numEpisodes || secondInt < 1)
                  return@forEach
               else {
                  val range = IntRange(
                     if (firstInt < 1) 1 else firstInt,
                     if (secondInt > malAnime.numEpisodes)
                        malAnime.numEpisodes else secondInt
                  )
                  episodesType[range] = it.second
               }
            } else {
               val range = IntRange(
                  if (firstInt < 1) 1 else firstInt,
                  secondInt
               )

               episodesType[range] = it.second
            }
         }
         malAnime.episodesType = episodesType
         return malAnime
      }


   suspend fun scrapeNextEpInfos(malAnime: MalAnimeDL): MalAnimeDL {
         var currentYear = Calendar.getInstance().get(Calendar.YEAR).toFloat()
         val currentSeason = when (Calendar.getInstance().get(Calendar.MONTH) / 3) {
            0 -> "winter"
            1 -> "spring"
            2 -> "summer"
            else -> "fall"
         }

         var link = "https://www.livechart.me/${currentSeason}-${currentYear.toInt()}/all"
         when (malAnime.startSeason.season) {
            "winter" -> 0
            "spring" -> 1
            "summer" -> 2
            "fall" -> 3
            else -> null
         }?.let {
            currentYear += Calendar.getInstance().get(Calendar.MONTH) / 12F
            var animeYear = malAnime.startSeason.year.toFloat()
            animeYear += it / 4F
            if (animeYear < currentYear)
               link =
                  "https://www.livechart.me/${malAnime.startSeason.season}-${malAnime.startSeason.year}/all"
         }

         val liveChart = Jsoup
            .connect(link)
            .timeout(10000)
            .get()
            .select("article.anime")
         val article = liveChart.first { article ->
            article.select(".mal-icon").attr("href")
               .substringAfter("https://myanimelist.net/anime/")
               .toInt() == malAnime.id
         }
         val nextEp =
            article.select(".release-schedule-info").text().filter { it.isDigit() }.toInt()
         val nextEpIn = article.select("time").attr("data-timestamp").toLong().seconds
         malAnime.nextEp = NextEpisode(nextEp, nextEpIn)
         return malAnime
      }

}
