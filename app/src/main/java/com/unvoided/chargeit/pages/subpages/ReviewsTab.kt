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
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Reviews
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.unvoided.chargeit.data.Review
import com.unvoided.chargeit.data.Station
import com.unvoided.chargeit.data.firestore.StationsDbActions
import com.unvoided.chargeit.data.viewmodels.StationsViewModel
import com.unvoided.chargeit.ui.components.ExpandableText
import com.unvoided.chargeit.ui.components.ReviewDialog
import com.unvoided.chargeit.ui.theme.components.LoadingComponent
import com.unvoided.chargeit.ui.theme.components.ShowIfNotEmpty
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsTab(
    navController: NavHostController,
    station: Station,
    stationsViewModel: StationsViewModel,
    snackbarHostState: SnackbarHostState
) {
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    Firebase.auth.addAuthStateListener {
        user = it.currentUser
    }
    val reviews by stationsViewModel.stationReviews.observeAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = station) {
        stationsViewModel.fetchStationReviews(station.id.toString())
    }

    LoadingComponent(isLoading = reviews == null, "Loading Reviews") {
        val openDialog = remember { mutableStateOf(false) }

        if (user != null && !userHasReview(reviews!!)) {
            ReviewDialog(dialogState = openDialog, message = "Write Review") { review, _ ->
                coroutineScope.launch {
                    handleNewReview(station.id!!, stationsViewModel, review, snackbarHostState)
                }
            }
        } else if (user != null && userHasReview(reviews!!)) {
            ReviewDialog(
                dialogState = openDialog,
                message = "Edit Review",
                oldReview = reviews!!.first { it.userUid == Firebase.auth.uid }) { newReview, oldReview ->
                coroutineScope.launch {
                    handleOldReview(
                        station.id!!,
                        stationsViewModel,
                        newReview,
                        oldReview!!,
                        snackbarHostState
                    )
                }
            }
        }
        Scaffold(
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                if (user != null) {
                    FilledTonalButton(onClick = { openDialog.value = true }) {
                        Icon(Icons.Outlined.DriveFileRenameOutline, "Review")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(if (userHasReview(reviews!!)) "Edit Review" else "Write Review")
                    }
                } else {
                    FilledTonalButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Outlined.PersonOutline, "Sign Up")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Sign Up to Review")
                    }
                }
            },
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
                        if (user != null && userHasReview(reviews!!)) {
                            item {
                                ReviewItem(
                                    review = reviews!!.first { it.userUid == Firebase.auth.uid },
                                    true
                                ) { review ->
                                    coroutineScope.launch {
                                        handleDeleteReview(
                                            station.id!!,
                                            stationsViewModel,
                                            review,
                                            snackbarHostState
                                        )
                                    }
                                }
                            }
                        }
                        reviews!!.filter { it.userUid != Firebase.auth.uid }.forEach {
                            item { ReviewItem(it, false) }
                        }
                    }
                }
            }
        }
    }


}

@Composable
fun ReviewItem(review: Review, isCurrentUser: Boolean, onDelete: (Review) -> Unit = {}) {
    ListItem(
        headlineText = {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
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
                Spacer(modifier = Modifier.size(10.dp))
                Text(review.userName!!, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(10.dp))
                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                    Text(review.rating.toString(), maxLines = 3, overflow = TextOverflow.Ellipsis)
                    Icon(Icons.Filled.Star, "Rating", Modifier.size(10.dp))
                }
                if (isCurrentUser) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                        IconButton(onClick = { onDelete(review) }) {
                            Icon(Icons.Outlined.DeleteForever, "Delete Review")
                        }
                    }
                }

            }
        },
        supportingText = {
            Box(
                Modifier
                    .wrapContentSize()
                    .padding(start = 35.dp)
            ) {
                ExpandableText(
                    text = review.comment,
                    minimizedMaxLines = 3
                )
            }

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

suspend fun handleNewReview(
    stationId: Int,
    stationsViewModel: StationsViewModel,
    review: Review,
    snackbarHostState: SnackbarHostState
) {
    val stationsDbActions = StationsDbActions()

    stationsDbActions.addReview(stationId.toString(), review)
    stationsViewModel.fetchStationReviews(stationId.toString())

    snackbarHostState.showSnackbar(
        "Review added successfully!",
        duration = SnackbarDuration.Short,
        withDismissAction = true
    )
}

suspend fun handleOldReview(
    stationId: Int,
    stationsViewModel: StationsViewModel,
    newReview: Review,
    oldReview: Review,
    snackbarHostState: SnackbarHostState
) {
    val stationsDbActions = StationsDbActions()

    stationsDbActions.removeReview(stationId.toString(), oldReview)
    stationsDbActions.addReview(stationId.toString(), newReview)
    stationsViewModel.fetchStationReviews(stationId.toString())

    snackbarHostState.showSnackbar(
        "Review edited successfully!",
        duration = SnackbarDuration.Short,
        withDismissAction = true
    )
}

suspend fun handleDeleteReview(
    stationId: Int,
    stationsViewModel: StationsViewModel,
    oldReview: Review,
    snackbarHostState: SnackbarHostState
) {
    val stationsDbActions = StationsDbActions()

    stationsDbActions.removeReview(stationId.toString(), oldReview)
    stationsViewModel.fetchStationReviews(stationId.toString())

    snackbarHostState.showSnackbar(
        "Review deleted successfully!",
        duration = SnackbarDuration.Short,
        withDismissAction = true
    )
}
