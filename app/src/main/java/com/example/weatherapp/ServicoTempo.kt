package com.example.weatherapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ServicoTempo {
    @GET("weather")
    fun getTempoCidade(
        @Query("q") cidade: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt"
    ): Call<RespostaTempo>
}
