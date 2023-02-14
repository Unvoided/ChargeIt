package com.unvoided.chargeit.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unvoided.chargeit.data.Station
import com.unvoided.chargeit.data.firestore.Users
import com.unvoided.chargeit.retrofit.GetStationsInput
import com.unvoided.chargeit.retrofit.OpenChargeMap

class StationsViewModel : ViewModel() {
    private val openChargeMap = OpenChargeMap()
    private val _stations = MutableLiveData<List<Station>>()
    private val _station = MutableLiveData<Station>()
    private val _favoriteStations = MutableLiveData<List<Station>>()

    val stationsList: LiveData<List<Station>>
        get() = _stations

    val station: LiveData<Station>
        get() = _station

    val favoriteStations: MutableLiveData<List<Station>>
        get() = _favoriteStations

    fun fetchStations(
        params: GetStationsInput,
    ) {
        openChargeMap.getStations(params) { stations, _ ->
            _stations.value = stations
        }
    }

    fun fetchStationById(id: Int) {
        var fStation: Station? = null
        _stations.value?.let { stations ->
            fStation = stations.firstOrNull { it.id == id }
        }
        if (fStation == null) {
            openChargeMap.getStationById(id.toString()) { stations, _ ->
                _station.value = stations!!.first()
            }
        } else {
            _station.value = fStation
        }
    }

    suspend fun fetchStationsById() {
        val favoriteStations = Users().getFavorites()

        if (favoriteStations.isNotEmpty()) {
            openChargeMap.getStationById(favoriteStations.joinToString(",")) { stations, _ ->
                _favoriteStations.value = stations
            }
        } else {
            _favoriteStations.value = emptyList()
        }

    }
}