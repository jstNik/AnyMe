package com.example.anyme.data.repositories

import okhttp3.Response


class ApiCallNotSuccessfulException(
   val response: Response
) : Exception() {

   override val message: String = response.message
   val code: Int = response.code
   val body: String? = response.body.toString()

}
