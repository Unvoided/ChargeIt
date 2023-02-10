package com.unvoided.chargeit.data.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface StationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStations(evStations: List<StationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addConnections(connections: List<ConnectionEntity>)

    @Transaction
    @Query("SELECT * FROM stations")
    fun getAllEvStations(): LiveData<List<StationWithConnections>>

    @Update
    suspend fun updateEvStation(evStation: StationEntity)
}