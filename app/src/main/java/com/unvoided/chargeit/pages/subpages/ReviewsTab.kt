@file:OptIn(ExperimentalMaterial3Api::class)

package com.unvoided.chargeit.pages.subpages

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.Reviews
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.unvoided.chargeit.data.Review
import com.unvoided.chargeit.data.Station
import com.unvoided.chargeit.data.firestore.StationsDbActions
import com.unvoided.chargeit.data.viewmodels.StationsViewModel
import com.unvoided.chargeit.ui.components.ReviewDialog
import com.unvoided.chargeit.ui.theme.components.LoadingComponent
import com.unvoided.chargeit.ui.theme.components.ShowIfLoggedIn
import com.unvoided.chargeit.ui.theme.components.ShowIfNotEmpty
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsTab(
    navController: NavHostController,
    station: Station,
    stationsViewModel: StationsViewModel
) {
    ShowIfLoggedIn(navController) {
        val reviews by stationsViewModel.stationReviews.observeAsState()
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(key1 = station) {
            stationsViewModel.fetchStationReviews(station.id.toString())
        }

        LoadingComponent(isLoading = reviews == null, "Loading Reviews") {
            val openDialog = remember { mutableStateOf(false) }

            if (!userHasReview(reviews!!)) {
                ReviewDialog(dialogState = openDialog, message = "Write Review") { review, _ ->
                    coroutineScope.launch {
                        handleNewReview(station.id!!, stationsViewModel, review)
                    }
                }
            } else if (userHasReview(reviews!!)) {
                ReviewDialog(
                    dialogState = openDialog,
                    message = "Edit Review",
                    oldReview = reviews!!.first { it.userUid == Firebase.auth.uid }) { newReview, oldReview ->
                    coroutineScope.launch {
                        handleOldReview(station.id!!, stationsViewModel, newReview, oldReview!!)
                    }
                }
            }
            Scaffold(
                floatingActionButtonPosition = FabPosition.Center,
                floatingActionButton = {
                    FilledTonalButton(onClick = { openDialog.value = true }) {
                        Icon(Icons.Outlined.DriveFileRenameOutline, "Review")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(if (userHasReview(reviews!!)) "Edit Review" else "Write Review")
                    }
                },
                topBar = {
                    LazyColumn() {
                        if (userHasReview(reviews!!)) {
                            item { ReviewItem(review = reviews!!.first { it.userUid == Firebase.auth.uid }) }
                        }
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    ShowIfNotEmpty(
                        icon = Icons.Outlined.Reviews,
                        isEmpty = reviews!!.isEmpty(),
                        message = "No Reviews"
                    ) {
                        val lazyListState = rememberLazyListState()
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier.fillMaxSize()
                        ) {

                            reviews!!.filter { it.userUid != Firebase.auth.uid }.forEach {
                                item { ReviewItem(it) }
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun ReviewItem(review: Review) {
    ListItem(
        leadingContent = {
            if (review.userPictureUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(
                        review.userPictureUrl
                    ),
                    contentDescription = "Account",
                    Modifier
                        .clip(CircleShape)
                        .size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Account"
                )
            }
        },
        headlineText = {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                Text(review.userName, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(10.dp))
                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                    Text(review.rating.toString())
                    Icon(Icons.Filled.Star, "Rating", Modifier.size(10.dp))
                }
            }
        },
        supportingText = {
            Text(text = review.comment)
        }
    )
    Divider()
}

fun userHasReview(reviews: List<Review>): Boolean {
    return if (reviews.isNotEmpty()) {
        reviews.any { it.userUid == Firebase.auth.uid!! }
    } else {
        false
    }

}

suspend fun handleNewReview(stationId: Int, stationsViewModel: StationsViewModel, review: Review) {
    val stationsDbActions = StationsDbActions()

    stationsDbActions.addReview(stationId.toString(), review)
    stationsViewModel.fetchStationReviews(stationId.toString())
}

suspend fun handleOldReview(
    stationId: Int,
    stationsViewModel: StationsViewModel,
    newReview: Review,
    oldReview: Review
) {
    val stationsDbActions = StationsDbActions()

    stationsDbActions.removeReview(stationId.toString(), oldReview)
    stationsDbActions.addReview(stationId.toString(), newReview)
    stationsViewModel.fetchStationReviews(stationId.toString())
}
