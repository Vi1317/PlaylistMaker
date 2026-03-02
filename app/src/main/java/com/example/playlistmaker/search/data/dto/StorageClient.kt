package com.example.playlistmaker.search.data.dto

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): T?
}