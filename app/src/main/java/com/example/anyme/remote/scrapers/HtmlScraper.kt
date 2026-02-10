package com.example.anyme.remote.scrapers

import android.util.Log
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.NextEpisode
import com.example.anyme.utils.time.OffsetDateTime
import com.example.anyme.utils.RangeMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.TimeZone
import org.jsoup.select.Selector
import java.util.Calendar
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

open class HtmlScraper @Inject constructor(
   private val networkManager: JsoupHtmlCacher,
   private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

   private val seasonalUrl = "https://www.livechart.me/schedule"
   private val episodesTypeBaseUrl = "https://www.animefillerlist.com"

   @OptIn(ExperimentalCoroutinesApi::class)
   suspend fun scrapeEpisodesType(malAnime: MalAnime): RangeMap<MalAnime.EpisodesType> {

      val html = networkManager.getHtml("$episodesTypeBaseUrl/shows")

      val htmlToEpOffset = html.select("div.Group a[href]").filter { element ->
         element.text().contains(malAnime.title) || element.text()
            .contains(malAnime.alternativeTitles.en)
      }.asFlow().flatMapMerge(concurrency = 5) { element ->
         flow {
            try {
               val showPageRef = episodesTypeBaseUrl + element.attr("href")
               val showPageHtml = networkManager.getHtml(showPageRef)
               showPageHtml.select("tbody tr").firstOrNull { tr ->
                  (tr.select("td.Date").first()?.text() ?: "") == "${malAnime.startDate}"
               }?.select("td.Number")?.text()?.toIntOrNull()?.minus(1)?.let {
                  emit(showPageHtml to it)
               }
            } catch (e: Selector.SelectorParseException) {
               Log.e("$e", "${e.message}", e)
            }
         }.flowOn(dispatcher)
      }.first()


      val animeFillerListHtml = htmlToEpOffset.first
      val epOffset = htmlToEpOffset.second

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
      return episodesType
   }


   suspend fun scrapeNextEpInfos(malAnime: MalAnime): NextEpisode {
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

      val liveChart = networkManager.getHtml(link).select("article.anime")
      val article = liveChart.first { article ->
         article.select(".mal-icon").attr("href")
            .substringAfter("https://myanimelist.net/anime/")
            .toInt() == malAnime.id
      }
      val nextEp =
         article.select(".release-schedule-info").text().filter { it.isDigit() }.toInt()
      val releaseDate = article.select("time").attr("data-timestamp").toLong().seconds
      val offsetDateTime = OffsetDateTime.create(releaseDate, TimeZone.currentSystemDefault())
      return NextEpisode(nextEp, offsetDateTime)
   }

   suspend fun scrapeSeasonal(media: Media): NextEpisode {
      var result = NextEpisode()
      try {
         val html = networkManager.getHtml(seasonalUrl)
         val articles = html.getElementsByTag("article")

         articles.forEach { article ->
            val aMalTag = article.select("a.lc-anime-card--related-links--icon.mal").firstOrNull()

            val malIdAsString = aMalTag!!.attr("href").filter { it.isDigit() }

            val malId = malIdAsString.toInt()
            if (malId == media.id) {

               val releaseDate = article
                  .getElementsByTag("time")
                  .attr("data-timestamp")
                  .toLong().seconds

               val spanTag = article.getElementsByTag("span").firstOrNull()

               val episodeNumber = spanTag!!.text().filter { it.isDigit() }.toInt()

               val offsetDateTime =
                  OffsetDateTime.create(releaseDate, TimeZone.currentSystemDefault())
               result = NextEpisode(episodeNumber, offsetDateTime)
            }
         }
      } catch (e: Exception) {
         Log.e("$e", "${e.message}", e)
      }
      return result
   }


}