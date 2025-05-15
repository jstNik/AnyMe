package com.example.anyme.api

class ExpiredOrInvalidTokenException(
   message: String?
): Exception(message) {
}