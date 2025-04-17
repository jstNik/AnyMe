package com.example.anyme.repositories

class ScrapingFailedException(
    override val message: String?
): Exception(message)