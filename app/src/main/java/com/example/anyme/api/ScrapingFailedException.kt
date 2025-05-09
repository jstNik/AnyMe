package com.example.anyme.api

class ScrapingFailedException(
    override val message: String?
): Exception(message)