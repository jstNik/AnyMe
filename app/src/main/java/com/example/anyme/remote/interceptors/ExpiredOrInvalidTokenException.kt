package com.example.anyme.remote.interceptors

class ExpiredOrInvalidTokenException(
   message: String?
): Exception(message) {
}