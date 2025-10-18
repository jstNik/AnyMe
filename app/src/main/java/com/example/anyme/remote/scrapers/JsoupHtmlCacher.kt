package com.example.anyme.remote.scrapers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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

class JsoupHtmlCacher @Inject constructor(
   private val client: OkHttpClient,
   private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

   private val cacheHtml: ConcurrentMap<URL, Document> = ConcurrentHashMap()

   suspend fun getHtml(url: URL): Document {
      if(url in cacheHtml)
         return cacheHtml[url]!!
      val request = Request.Builder().url(url).build()
      val response = withContext(dispatcher) {
         client.newCall(request).execute()
      }
      if(!response.isSuccessful || response.body == null)
         throw HttpStatusException(response.message, response.code, url.toString())
      val html = Jsoup.parse(response.body!!.string(), "${url.protocol}://${url.host}/")
      cacheHtml[url] = html
      return html
   }

}