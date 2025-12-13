package com.example.anyme.remote.scrapers

import android.util.Log
import com.example.anyme.utils.MutexConcurrentHashMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

class JsoupHtmlCacher @Inject constructor(
   private val client: OkHttpClient,
   private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

   private val cacheHtml: ConcurrentMap<String, Document> = ConcurrentHashMap()
   private val mutexMap: MutexConcurrentHashMap = MutexConcurrentHashMap()

   suspend fun getHtml(url: String): Document {
      val mutex = mutexMap[url]
      try {
         mutex.acquire()
         if (url in cacheHtml) {
            return cacheHtml[url]!!
         }
         val request = Request.Builder().url(url).build()
         val response = withContext(dispatcher) {
            client.newCall(request).execute()
         }
         if (!response.isSuccessful || response.body == null)
            throw HttpStatusException(response.message, response.code, url)
         val link = URL(url)
         val html = Jsoup.parse(response.body!!.string(), "${link.protocol}://${link.host}/")
         cacheHtml[url] = html
         return html
      } finally {
         mutex.release()
      }
   }

}