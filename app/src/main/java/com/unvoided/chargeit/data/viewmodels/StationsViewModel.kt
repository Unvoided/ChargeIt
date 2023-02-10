package com.unvoided.chargeit.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.unvoided.chargeit.data.room.*
import com.unvoided.chargeit.retrofit.GetStationsInput
import com.unvoided.chargeit.retrofit.OpenChargeMap
import kotlinx.coroutines.launch

public class StationsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val openChargeMap = OpenChargeMap()
    private val stationRepository: StationRepository

    init {
        val stationDatabase = StationDatabase.getDatabase(application)
        val stationDao = stationDatabase.getStationDao()
        stationRepository = StationRepository(stationDao)
    }

    val stationsList: LiveData<List<StationWithConnections>> =
        stationRepository.getAllEvStations()

    fun fetchStations(
        params: GetStationsInput,
    ) {
        openChargeMap.getStations(params) { stations, _ ->
            viewModelScope.launch {
                stations?.mapIndexed { i, station ->
                    StationEntity(
                        stationId = station.id ?: i,
                        phonePrimaryContact = station.operatorInfo?.phonePrimaryContact,
                        contactEmail = station.operatorInfo?.contactEmail,
                        operatorInfo = station.operatorInfo?.title,
                        isOperational = station.statusType?.isOperational ?: true,
                        usageCost = station.usageCost,
                        addressInfo = station.addressInfo?.title,
                        town = station.addressInfo?.town,
                        latitude = station.addressInfo?.latitude,
                        longitude = station.addressInfo?.longitude,
                        distance = station.addressInfo?.distance,
                        distanceUnit = station.addressInfo?.distanceUnit,
                        numberOfPoints = station.numberOfPoints
                    )
                }?.let { staList ->
                    stationRepository.addStations(staList)
                }
            }

            viewModelScope.launch {
                stations?.forEachIndexed { i, station ->
                    station.connections?.mapIndexed { k, conn ->
                        ConnectionEntity(
                            connectionId = conn.id ?: k,
                            stationId = station.id ?: i,
                            isOperational = conn.statusType?.isOperational,
                            formalName = conn.connectionType?.formalName,
                            amps = conn.amps,
                            voltage = conn.voltage,
                            powerKw = conn.powerKw,
                            quantity = conn.quantity
                        )
                    }?.let { connList ->
                        stationRepository.addConnections(connList)
                    }
                }
            }
        }
    }
}