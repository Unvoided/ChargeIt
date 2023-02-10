package com.unvoided.chargeit.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unvoided.chargeit.data.Station
import com.unvoided.chargeit.retrofit.GetStationsInput
import com.unvoided.chargeit.retrofit.OpenChargeMap

class StationsViewModel : ViewModel() {
    private val openChargeMap = OpenChargeMap()
    private val _stations = MutableLiveData<List<Station>>()

    val stationsList: LiveData<List<Station>>
        get() = _stations

    fun fetchStations(
        params: GetStationsInput,
    ) {
        openChargeMap.getStations(params) { stations, _ ->
            _stations.value = stations
        }
    }
}