package com.unvoided.chargeit.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StationEntity::class, ConnectionEntity::class], version = 2)
abstract class StationDatabase : RoomDatabase() {

    abstract fun getStationDao(): StationDao

    companion object {
        private var INSTANCE: StationDatabase? = null

        fun getDatabase(context: Context): StationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    StationDatabase::class.java,
                    "stations"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}
