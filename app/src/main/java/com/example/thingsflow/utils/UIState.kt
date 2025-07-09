package com.example.thingsflow.utils

sealed class UIState<T> {
    data class Success<T>(val data: T?): UIState<T>()
    data class Failure<T>(val errorCode: Int, val message: String?): UIState<T>()
}