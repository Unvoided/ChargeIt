package com.unvoided.chargeit.data.firestore

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unvoided.chargeit.data.Review
import kotlinx.coroutines.tasks.await
import java.text.DecimalFormat

class StationsDbActions(
    private val db: FirebaseFirestore = Firebase.firestore,
    private val userUid: String = Firebase.auth.currentUser!!.uid
) {
    companion object {
        private const val STATIONS_COLLECTION = "stations"
        private const val REVIEWS = "reviews"
    }

    suspend fun addReview(stationId: String, reviewInput: Review) {
        db.collection(STATIONS_COLLECTION)
            .document(stationId)
            .set(mapOf(REVIEWS to FieldValue.arrayUnion(reviewInput)), SetOptions.merge())
            .await()
    }

    suspend fun removeReview(stationId: String, reviewInput: Review) {
        db.collection(STATIONS_COLLECTION)
            .document(stationId)
            .set(mapOf(REVIEWS to FieldValue.arrayRemove(reviewInput)), SetOptions.merge())
            .await()
    }

    suspend fun updateReview(stationId: String, oldReview: Review, newReview: Review) {
        removeReview(stationId, oldReview)
        addReview(stationId, newReview)
    }

    suspend fun getReviews(stationId: String): List<Review>? =
        db.collection(STATIONS_COLLECTION).document(stationId).get().await()
            .toObject(ReviewsResponse::class.java)?.reviews

    suspend fun getReviewsAvg(stationId: String): String? {
        val reviews = getReviews(stationId)
        if (reviews != null && reviews.isNotEmpty()) {
            return DecimalFormat("#.#").format(reviews.stream().mapToInt { it.rating }
                .average().asDouble)
        }
        return null
    }
}

data class ReviewsResponse(
    val reviews: List<Review>? = null
)


