package com.unvoided.chargeit.data.room

import androidx.lifecycle.LiveData

class StationRepository(private val evStationDAO: StationDao) {
    suspend fun addStations(evStations: List<StationEntity>) {
        return evStationDAO.addStations(evStations)
    }

    suspend fun addConnections(connections: List<ConnectionEntity>) {
        return evStationDAO.addConnections(connections)
    }

    fun getAllEvStations(): LiveData<List<StationWithConnections>> {
        return evStationDAO.getAllEvStations()
    }

    suspend fun updateEvStation(evStation: StationEntity) {
        return evStationDAO.updateEvStation(evStation)
    }
}