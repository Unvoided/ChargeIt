@file:Suppress("UNCHECKED_CAST")

package com.unvoided.chargeit.data.firestore

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class UsersDbActions(
    private val db: FirebaseFirestore = Firebase.firestore,
    private val userUid: String = Firebase.auth.currentUser!!.uid
) {

    companion object {
        private const val USER_COLLECTION = "users"
        private const val FAVORITE_STATIONS = "favorites"
        private const val STATIONS_HISTORY = "history"
    }

    suspend fun getFavorites() =
        db.collection(USER_COLLECTION).document(userUid).get().await()
            .get(FAVORITE_STATIONS) as ArrayList<Int>?


    suspend fun isFavorite(stationId: Int): Boolean {
        return getFavorites()?.any { it == stationId } ?: false
    }

    suspend fun addFavorite(stationId: Int) {
        db.collection(USER_COLLECTION).document(userUid)
            .set(mapOf("favorites" to FieldValue.arrayUnion(stationId)), SetOptions.merge()).await()
    }

    suspend fun removeFavorite(stationId: Int) {
        db.collection(USER_COLLECTION).document(userUid)
            .set(mapOf("favorites" to FieldValue.arrayRemove(stationId)), SetOptions.merge())
            .await()
    }

    suspend fun isStationInCurrentDayHistory(currentDate: LocalDate, stationId: Int): Boolean {
        return getHistory()?.get(currentDate.toString())?.any { it == stationId } ?: false
    }

    suspend fun getHistory() =
        db.collection(USER_COLLECTION).document(userUid).get().await()
            .get(STATIONS_HISTORY) as Map<String, List<Int>>?


    suspend fun addToHistory(currentDate: LocalDate, stationId: Int) {
        db.collection(USER_COLLECTION).document(userUid).set(
            mapOf(
                STATIONS_HISTORY to mapOf(
                    currentDate.toString() to FieldValue.arrayUnion(
                        stationId
                    )
                )
            ),
            SetOptions.merge()
        ).await()
    }

    suspend fun removeFromHistory(currentDate: LocalDate, stationId: Int) {
        db.collection(USER_COLLECTION).document(userUid).set(
            mapOf(
                STATIONS_HISTORY to mapOf(
                    currentDate.toString() to FieldValue.arrayRemove(
                        stationId
                    )
                )
            ),
            SetOptions.merge()
        ).await()
    }

}