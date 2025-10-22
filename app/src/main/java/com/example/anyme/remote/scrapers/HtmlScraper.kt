package com.example.anyme.remote.scrapers

import android.util.Log
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.NextEpisode
import com.example.anyme.utils.time.OffsetDateTime
import com.example.anyme.utils.RangeMap
import kotlinx.datetime.TimeZone
import org.jsoup.Jsoup
import java.net.URL
import java.util.Calendar
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

open class HtmlScraper @Inject constructor(
   private val networkManager: JsoupHtmlCacher
) {

   private val seasonalUrl = URL("https://www.livechart.me/schedule")

   suspend fun scrapeEpisodesType(malAnime: MalAnime): MalAnime {
      val url = """
         https://www.google.com/search?q=
         ${malAnime.alternativeTitles.en.ifBlank { malAnime.title }.replace("\"", "")}
         &as_oq=&as_eq=&as_nlo=&as_nhi=&lr=&cr=&as_qdr=all&as_sitesearch=animefillerlist.com&as_occt=any&as_filetype=&tbs=
      """.replace("\n", "").replace(" ", "")

      val html = networkManager.getHtml(URL(url))

      val episodesList = html.select("a").filter { element ->
         element.attr("href").startsWith("https://www.animefillerlist.com") &&
                 !element.attr("href").startsWith("https://translate.google.com")
      }

      val link = episodesList.first().attr("href")
      val animeFillerListHtml = Jsoup.connect(link).timeout(10000).get()
      val epOffset = animeFillerListHtml
         .select("tbody tr")
         .first { tr ->
            (tr.select("td.Date").first()?.text() ?: "") == "${malAnime.startDate}"
         }.select("td.Number")
         .text()
         .toInt() - 1

      val mangaCanonList = animeFillerListHtml.select("div.manga_canon a").map {
         it to MalAnime.EpisodesType.MangaCanon
      }
      val mixedCanonList = animeFillerListHtml.select("div.mixed_canon\\/filler a").map {
         it to MalAnime.EpisodesType.MixedMangaCanon
      }
      val animeCanonList = animeFillerListHtml.select("div.anime_canon a").map {
         it to MalAnime.EpisodesType.AnimeCanon
      }
      val fillerList = animeFillerListHtml.select("div.filler a").map {
         it to MalAnime.EpisodesType.Filler
      }
      val episodesType = RangeMap(mutableMapOf<IntRange, MalAnime.EpisodesType>())
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


   suspend fun scrapeNextEpInfos(malAnime: MalAnime): MalAnime {
      var currentYear = Calendar.getInstance().get(Calendar.YEAR).toFloat()
      val currentSeason = when (Calendar.getInstance().get(Calendar.MONTH) / 3) {
         0 -> "winter"
         1 -> "spring"
         2 -> "summer"
         else -> "fall"
      }

      var link = "https://www.livechart.me/${currentSeason}-${currentYear.toInt()}/all"
      when (malAnime.season.season) {
         "winter" -> 0
         "spring" -> 1
         "summer" -> 2
         "fall" -> 3
         else -> null
      }?.let {
         currentYear += Calendar.getInstance().get(Calendar.MONTH) / 12F
         var animeYear = malAnime.season.year.toFloat()
         animeYear += it / 4F
         if (animeYear < currentYear)
            link =
               "https://www.livechart.me/${malAnime.season.season}-${malAnime.season.year}/all"
      }

      val liveChart = networkManager.getHtml(URL(link)).select("article.anime")
      val article = liveChart.first { article ->
         article.select(".mal-icon").attr("href")
            .substringAfter("https://myanimelist.net/anime/")
            .toInt() == malAnime.id
      }
      val nextEp =
         article.select(".release-schedule-info").text().filter { it.isDigit() }.toInt()
      val releaseDate = article.select("time").attr("data-timestamp").toLong().seconds
      val offsetDateTime = OffsetDateTime.create(releaseDate, TimeZone.currentSystemDefault())
      malAnime.nextEp = NextEpisode(nextEp, offsetDateTime)
      return malAnime
   }

   suspend fun scrapeSeasonal(malSeasonalAnimes: Map<Int, Media>): Map<Int, NextEpisode>{
      val html = networkManager.getHtml(seasonalUrl)
      val articles = html.getElementsByTag("article")
      val animeToTime = mutableMapOf<Int, NextEpisode>()

      articles.forEach { article ->
         try {
            val aMalTag = article.select("a.lc-anime-card--related-links--icon.mal").firstOrNull()

            val malIdAsString = aMalTag!!.attr("href").filter { it.isDigit() }

            val malId = Integer.parseInt(malIdAsString)
            val malAnime = malSeasonalAnimes[malId]
            if (malAnime == null) return@forEach

            val releaseDate =
               article.getElementsByTag("time").attr("data-timestamp").toLong().seconds

            val spanTag = article.getElementsByTag("span").firstOrNull()

            val episodeNumber = spanTag!!.text().filter { it.isDigit() }.toInt()

            val offsetDateTime = OffsetDateTime.create(releaseDate, TimeZone.currentSystemDefault())
            animeToTime[malId] = NextEpisode(episodeNumber, offsetDateTime)
         } catch (e: Exception){
            Log.e("$e", "${e.message}", e)
         }
      }
      return animeToTime
   }


}