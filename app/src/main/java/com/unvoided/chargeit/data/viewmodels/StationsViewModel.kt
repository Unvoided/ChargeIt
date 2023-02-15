package com.unvoided.chargeit.data.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unvoided.chargeit.data.Review
import com.unvoided.chargeit.data.Station
import com.unvoided.chargeit.data.firestore.StationsDbActions
import com.unvoided.chargeit.data.firestore.UsersDbActions
import com.unvoided.chargeit.retrofit.GetStationsInput
import com.unvoided.chargeit.retrofit.OpenChargeMap
import java.time.LocalDate

class StationsViewModel : ViewModel() {
    private val openChargeMap = OpenChargeMap()
    private val _stations = MutableLiveData<List<Station>>()
    private val _station = MutableLiveData<Station>()
    private val _stationReviews = MutableLiveData<List<Review>>()
    private val _favoriteStations = MutableLiveData<List<Station>>()
    private val _stationsHistory = MutableLiveData<Map<LocalDate, List<Station>>>()

    val stationsList: LiveData<List<Station>>
        get() = _stations

    val station: LiveData<Station>
        get() = _station

    val stationReviews: LiveData<List<Review>>
        get() = _stationReviews

    val favoriteStations: MutableLiveData<List<Station>>
        get() = _favoriteStations

    val stationsHistory: MutableLiveData<Map<LocalDate, List<Station>>>
        get() = _stationsHistory

    fun fetchStations(
        params: GetStationsInput,
    ) {
        openChargeMap.getStations(params) { stations, _ ->
            _stations.value = stations
        }
    }

    fun clearPrevReviews() {
        _stationReviews.value = null
    }

    suspend fun fetchStationReviews(id: String) {
        val reviews = StationsDbActions().getReviews(id)
        clearPrevReviews()
        if (reviews != null && reviews.isNotEmpty()) {
            _stationReviews.value = reviews
        } else {
            _stationReviews.value = emptyList()
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

    suspend fun fetchFavoriteStations() {
        val favoriteStat = UsersDbActions().getFavorites()

        if (favoriteStat != null && favoriteStat.isNotEmpty()) {
            openChargeMap.getStationById(favoriteStat.joinToString(",")) { stations, _ ->
                _favoriteStations.value = stations
            }
        } else {
            _favoriteStations.value = emptyList()
        }
    }

    suspend fun fetchStationsHistory() {
        val statHistory = UsersDbActions().getHistory()

        if (statHistory != null && statHistory.isNotEmpty()) {
            val ids = ArrayList<Int>()
            statHistory.keys.forEach { date ->
                if (statHistory[date] != null && statHistory[date]!!.isNotEmpty()) {
                    statHistory[date]?.forEach { id ->
                        if (!ids.any { it == id }) {
                            ids.add(id)
                        }
                    }
                }
            }
            Log.d("SVM", ids.joinToString(","))
            openChargeMap.getStationById(ids.joinToString(",")) { stations, _ ->
                if (stations != null) {
                    val fStations = HashMap<LocalDate, List<Station>>()

                    statHistory.keys.forEach { date ->
                        val filtered = stations.filter { station ->
                            statHistory[date]?.any { it == station.id }
                                ?: false
                        }

                        if (filtered.isNotEmpty()) {
                            fStations[LocalDate.parse(date)] = filtered
                        }
                    }
                    Log.d("SVM", fStations.toString())
                    _stationsHistory.value = fStations
                }
            }
        } else {
            _stationsHistory.value = HashMap()
        }
    }
}