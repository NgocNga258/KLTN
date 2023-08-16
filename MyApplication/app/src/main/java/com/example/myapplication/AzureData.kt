package com.example.myapplication

// AzureData with temperature, humidity, led, timestamp
data class AzureData(
    val temperature: Double,
    val humidity: Double,
    val led1: Int,
    val led2: Int,
    val time: String,
    val date: String,
)
