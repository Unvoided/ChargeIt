package com.unvoided.chargeit.retrofit

import android.util.Log
import com.unvoided.chargeit.data.Station
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OpenChargeMap {
    companion object {
        private val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openchargemap.io/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        private val service: OpenChargeMapAPI =
            retrofit.create(OpenChargeMapAPI::class.java)
    }

    fun getStations(
        input: GetStationsInput,
        callback: (data: List<Station>?, error: Throwable?) -> Unit
    ) {
        service.getStations(
            latitude = input.latitude,
            longitude = input.longitude,
            maxResults = input.maxResults
        ).enqueue(object : Callback<List<Station>> {
            override fun onResponse(
                call: Call<List<Station>>,
                response: Response<List<Station>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, Throwable("NetworkError"))
                }
                // TODO Remove log
                Log.d("Retrofit#isSuccessful", response.isSuccessful.toString())
            }

            override fun onFailure(call: Call<List<Station>>, t: Throwable) {
                // TODO Remove log
                Log.d("Retrofit#onFailure", t.message, t)
                callback(null, t)
            }
        })
    }

    fun getStationById(
        id: Int,
        callback: (data: List<Station>?, error: Throwable?) -> Unit
    ) {
        service.getStationById(id).enqueue(object : Callback<List<Station>> {
            override fun onResponse(
                call: Call<List<Station>>,
                response: Response<List<Station>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, Throwable("NetworkError"))
                }
            }

            override fun onFailure(call: Call<List<Station>>, t: Throwable) {
                callback(null, t)
            }
        })
    }
}

data class GetStationsInput(
    val maxResults: Int = 10,
    val latitude: Double,
    val longitude: Double
)

data class GetStationByIdInput(
    val latitude: Double,
    val longitude: Double,
    val id: Int,
)