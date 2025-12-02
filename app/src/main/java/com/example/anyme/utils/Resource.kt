package com.example.anyme.utils

class Resource<T> private constructor(
   val status: Status,
   val data: T? = null,
   val error: Exception? = null
) {


   init{
      when(status){
         Status.Success -> checkNotNull(data)
         Status.Loading -> check(data == null && error == null)
         Status.Failure -> check(data == null && error != null)
      }
   }

   enum class Status{
      Success, Loading, Failure
   }

   companion object {
      fun <T> success(data: T) = Resource(Status.Success, data)
      fun <T> loading() = Resource<T>(Status.Loading)
      fun <T> failure(e: Exception) = Resource<T>(Status.Failure, error = e)
   }

}


sealed class Result<out T>{
   data class Success<out T>(val data: T): Result<T>()
   data object Loading: Result<Nothing>()
   data class Error<E: Exception>(val error: E): Result<Nothing>()
}