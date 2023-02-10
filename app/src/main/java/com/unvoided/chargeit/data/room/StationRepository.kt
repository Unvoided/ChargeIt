package com.unvoided.chargeit.data.room

import androidx.lifecycle.LiveData

class StationRepository(private val stationDao: StationDao) {
    suspend fun addStations(evStations: List<StationEntity>) {
        return stationDao.addStations(evStations)
    }

    suspend fun addConnections(connections: List<ConnectionEntity>) {
        return stationDao.addConnections(connections)
    }

    fun getAllEvStations(): LiveData<List<StationWithConnections>> {
        return stationDao.getAllEvStations()
    }

    suspend fun updateEvStation(evStation: StationEntity) {
        return stationDao.updateEvStation(evStation)
    }
}