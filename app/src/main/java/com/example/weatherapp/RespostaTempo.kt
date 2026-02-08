package com.example.weatherapp

data class RespostaTempo(
    val main: Main,
    val nome: String,

)

data class Main(
    val temp: Double,
    val unit: String,
    val lang: String

)
