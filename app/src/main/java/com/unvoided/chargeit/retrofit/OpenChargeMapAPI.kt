package com.unvoided.chargeit.retrofit

import com.unvoided.chargeit.data.Station
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


private const val OPEN_CHARGE_MAP_API_KEY = "6ae510bc-4d2a-4039-ab4b-1cb7f43fde67"

interface OpenChargeMapAPI {
    @GET("/poi?output=json&key=${OPEN_CHARGE_MAP_API_KEY}?distanceunit=km")
    fun getStations(
        @Query("maxresults") maxResults: Number = 10,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): Call<List<Station>>
}