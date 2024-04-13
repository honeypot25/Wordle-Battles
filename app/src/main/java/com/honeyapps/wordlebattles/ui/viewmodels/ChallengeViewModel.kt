package com.honeyapps.wordlebattles.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.honeyapps.wordlebattles.data.models.ChallengeModel
import com.honeyapps.wordlebattles.data.models.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChallengeViewModel : ViewModel() {

    private val _user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    private val _challengeRequests = MutableStateFlow(listOf<ChallengeModel>())
    val challengeRequests = _challengeRequests.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        fetchChallengeRequests()
    }

    fun refreshScreen() {
        // reset
        _isRefreshing.value = false
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchChallengeRequests()
            delay(500L)
            _isRefreshing.value = false
        }
    }

    private fun fetchChallengeRequests() {
        viewModelScope.launch(Dispatchers.IO) {
            // get the list of challenge requests document references from Firestore
            val requestsRefs = FirebaseFirestore.getInstance()
                .collection("users")
                .document(_user.uid)
                .get().await()
                .get("challengeRequests") as? List<Map<String, Any>> ?: emptyList()

            if (requestsRefs.isEmpty()) return@launch

            val requests: List<ChallengeModel> = requestsRefs.map { docData ->
                val friendRef = docData["friend"] as DocumentReference
                val friend = friendRef.get().await().data?.let{
                    UserModel(
                        uid = friendRef.id,
                        username = it["username"].toString(),
                        photoUrl = it["photoUrl"].toString(),
                        flag = it["flag"].toString(),
                    )
                }!!

                ChallengeModel(
                    id = docData["id"].toString(),
                    timestamp = docData["timestamp"]!! as Timestamp,
                    friend = friend
                )
            }

            _challengeRequests.value = requests.sortedByDescending { it.timestamp }
        }
    }

    fun sendChallengeRequest(
        challengeId: String,
        friendId: String,
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ) {
        /* TODO:
            * Implement FCM method to receive challenge requests:
            * - It should modify the _challengeRequests value so that the "Challenge requests" section can get automatic recompositions
        */
        val usersRef = FirebaseFirestore.getInstance().collection("users")
        val friendRef = usersRef.document(friendId)
        val request = mapOf(
            "id" to challengeId,
            "timestamp" to Timestamp.now(), // can't use FieldValue.serverTimestamp()
            "friend" to usersRef.document(_user.uid)
        )
        // update the Firebase friend's request, creating a match
        friendRef.update("challengeRequests", FieldValue.arrayUnion(request))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFail() }
    }


    fun handleChallengeRequest(
        challengeId: String,
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val usersRef = FirebaseFirestore.getInstance().collection("users")
            val userRef = usersRef.document(_user.uid)
            val request = _challengeRequests.value.find { it.id == challengeId }?.run {
                val friendRef = usersRef.document(this.friend.uid)
                mapOf(
                    "id" to this.id,
                    "timestamp" to this.timestamp!!,
                    "friend" to friendRef,
                )
            } ?: mapOf()

            // update the Firebase user's requests
            userRef.update("challengeRequests", FieldValue.arrayRemove(request))
                .addOnSuccessListener {
                    // copy the list excluding the challenge to remove
                    _challengeRequests.value = _challengeRequests.value.filterNot { it.id == challengeId }
                    onSuccess()
                }
                .addOnFailureListener { onFail() }
        }
    }
}

