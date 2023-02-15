package com.unvoided.chargeit.data

import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

data class Review(
    val userUid: String = Firebase.auth.currentUser!!.uid,
    val userName: String = Firebase.auth.currentUser!!.displayName!!,
    val userPictureUrl: String? = Firebase.auth.currentUser!!.photoUrl?.toString(),
    val timestamp: Timestamp = Timestamp.now(),
    val rating: Int,
    val comment: String
)