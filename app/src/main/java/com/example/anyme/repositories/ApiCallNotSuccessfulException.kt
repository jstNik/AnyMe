package com.example.anyme.repositories;

public class ApiCallNotSuccessfulException(
        override val message: String?,
): Exception(message) {
}
