package com.example.anyme.remote.scrapers

class ScrapingFailedException(
    override val message: String?
): Exception(message)