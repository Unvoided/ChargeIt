package com.unvoided.chargeit.data.firestore

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class Users(
    private val db: FirebaseFirestore = Firebase.firestore,
    private val userUid: String = Firebase.auth.currentUser!!.uid
) {

    companion object {
        private const val USER_COLLECTION = "users"
        private const val FAVORITE_STATIONS = "favorites"
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun getFavorites(): List<Int> =
        db.collection(USER_COLLECTION)
            .document(userUid)
            .get().await().get(FAVORITE_STATIONS) as ArrayList<Int>


    suspend fun isFavorite(stationId: Int): Boolean {
        return getFavorites().any { it == stationId }
    }

    suspend fun addFavorite(stationId: Int) {
        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(mapOf("favorites" to FieldValue.arrayUnion(stationId)), SetOptions.merge())
            .await()
    }

    suspend fun removeFavorite(stationId: Int) {
        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(mapOf("favorites" to FieldValue.arrayRemove(stationId)), SetOptions.merge())
            .await()
    }

}