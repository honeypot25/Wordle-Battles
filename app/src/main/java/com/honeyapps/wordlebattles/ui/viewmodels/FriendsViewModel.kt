package com.honeyapps.wordlebattles.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.honeyapps.wordlebattles.data.models.UserModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FriendsViewModel : ViewModel() {

    private val _user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    // My friends
    private val _friendsList = MutableStateFlow(listOf<UserModel>())
    val friendsList = _friendsList.asStateFlow()

    // Requests
    private val _friendRequests = MutableStateFlow(listOf<UserModel>())
    val friendRequests = _friendRequests.asStateFlow()

    // Search
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()
    private val _resultUsersList = MutableStateFlow(listOf<UserModel>())
    val resultUsersList = _resultUsersList.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchFriends()
            fetchFriendRequests()
        }
    }

    fun refreshScreen() {
        // reset
        _isRefreshing.value = false
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            fetchFriendRequests()
            delay(500L)
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchFriends() {
        // get the list of friends document references from Firestore
        val friendsRefs = FirebaseFirestore.getInstance()
            .collection("users")
            .document(_user.uid)
            .get().await()
            .get("friends") as? List<DocumentReference> ?: emptyList()

        if (friendsRefs.isEmpty()) return

        // create a request task for each friend
        val fetchFriendTasks = friendsRefs.map { it.get() }
        // whenever we complete the get for all the friends
        Tasks.whenAllComplete(fetchFriendTasks)
            .addOnSuccessListener { taskList ->
                // get the data for each task result, and so friend document
                val friendsItemsList: List<UserModel> = taskList.mapNotNull { task ->
                    val taskSnap = task.result as? DocumentSnapshot
                    taskSnap?.let { friendDoc ->
                        // which is used to create a local UserModel for the fetched friend
                        friendDoc.data?.let {
                            UserModel(
                                uid = friendDoc.id,
                                username = it["username"].toString(),
                                photoUrl = it["photoUrl"].toString(),
                                flag = it["flag"].toString(),
                            )
                        }
                    }
                }
                // and then update the local friendsList (ordering by username, locally)
                _friendsList.value = friendsItemsList.sortedBy { it.username }
            }
    }

    fun fetchUser(userId: String): Deferred<UserModel> {
        return viewModelScope.async(Dispatchers.IO) {
            val userDoc = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get().await()

            userDoc.data?.let {
                UserModel(
                    uid = userDoc.id,
                    username = it["username"].toString(),
                    photoUrl = it["photoUrl"].toString(),
                    flag = it["flag"].toString(),
                )
            }!!
        }
    }

    fun removeFriend(
        friend: UserModel,
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // reflect the change in the user's document
            val usersColl = FirebaseFirestore.getInstance().collection("users")
            val userRef = usersColl.document(_user.uid)
            val friendRef = usersColl.document(friend.uid)
            userRef.update("friends", FieldValue.arrayRemove(friendRef))
                .addOnSuccessListener {
                    // reflect the change in the friend's document
                    friendRef.update("friends", FieldValue.arrayRemove(userRef))
                        .addOnSuccessListener {
                            _friendsList.value -= friend
                            onSuccess()
                        }
                        .addOnFailureListener { onFail() }
                }
                .addOnFailureListener { onFail() }
        }
    }


    private suspend fun fetchFriendRequests() {
        // get the list of friend requests document references from Firestore
        val friendsRefs = FirebaseFirestore.getInstance()
            .collection("users")
            .document(_user.uid)
            .get().await()
            .get("friendRequests") as? List<DocumentReference> ?: emptyList()

        if (friendsRefs.isEmpty()) return

        // create a request task for each friend
        val fetchFriendTasks = friendsRefs.map { it.get() }
        // whenever we complete the get for all the friends
        Tasks.whenAllComplete(fetchFriendTasks)
            .addOnSuccessListener { taskList ->
                // get the data for each task result, and so friend document
                val friendsItemsList: List<UserModel> = taskList.mapNotNull { task ->
                    val taskSnap = task.result as? DocumentSnapshot
                    taskSnap?.let { friendDoc ->
                        // which is used to create a local UserModel for the fetched friend
                        friendDoc.data?.let {
                            UserModel(
                                uid = friendDoc.id,
                                username = it["username"].toString(),
                                photoUrl = it["photoUrl"].toString(),
                                flag = it["flag"].toString(),
                            )
                        }
                    }
                }
                // and then update the local friendRequests (ordering by username, locally)
                _friendRequests.value = friendsItemsList.sortedBy { it.username }
            }
    }


    fun acceptFriendReq(
        friend: UserModel,
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // update the Firebase user's requests
            val usersColl = FirebaseFirestore.getInstance().collection("users")
            val userRef = usersColl.document(_user.uid)
            val friendRef = usersColl.document(friend.uid)
            userRef.update("friendRequests", FieldValue.arrayRemove(friendRef))
                .addOnSuccessListener {
                    _friendRequests.value -= friend
                    // update the Firebase user's friends
                    userRef.update("friends", FieldValue.arrayUnion(friendRef))
                        .addOnSuccessListener {
                            // update the Firebase friend's friends
                            friendRef.update("friends", FieldValue.arrayUnion(userRef))
                                .addOnSuccessListener {
                                    _friendsList.value += friend
                                    onSuccess()
                                }
                                .addOnFailureListener { onFail() }
                        }
                        .addOnFailureListener { onFail() }
                }
                .addOnFailureListener { onFail() }
        }
    }


    fun rejectFriendReq(
        friend: UserModel,
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // update the Firebase user's requests
            val usersRef = FirebaseFirestore.getInstance().collection("users")
            val friendRef = usersRef.document(friend.uid)
            val userRef = usersRef.document(_user.uid)
            userRef.update("friendRequests", FieldValue.arrayRemove(friendRef))
                .addOnSuccessListener {
                    _friendsList.value -= friend
                    onSuccess()
                }
                .addOnFailureListener { onFail() }
        }
    }


    fun onUserSearch(
        query: String,
        onSuccess: (Int) -> Unit,
        onFail: () -> Unit
    ) {
        // reset list and start searching
        _resultUsersList.value = listOf()
        _isSearching.value = true

        viewModelScope.launch(Dispatchers.IO) {
            // needed for the subsequent checks
//            fetchFriends()
//            fetchFriendRequests()

            val usersRef = FirebaseFirestore.getInstance().collection("users")
            usersRef
                .whereEqualTo("username", query)
                .whereNotEqualTo(FieldPath.documentId(), _user.uid) // skip the current user
                .orderBy("username", Query.Direction.ASCENDING)
                // make it case insensitive
//                .startAt(query.lowercase())
//                .endAt(query.lowercase() + "\uf8ff")
                .get()
                .addOnSuccessListener { querySnap ->
                    if (querySnap.documents.isEmpty()) {
                        _isSearching.value = false
                        onSuccess(0)
                        return@addOnSuccessListener
                    }

                    // foundUsers list
                    val foundUsers: List<UserModel> =
                        querySnap.documents.mapNotNull { foundUserDoc ->
                            val foundUserId = foundUserDoc.id
//                        viewModelScope.launch(Dispatchers.IO) {
//                            var canReceiveFriendReq = canReceiveFriendReq(
//                                usersColl,
//                                userRef,
//                                foundUserId
//                            ).await()

                            // build the user
                            foundUserDoc.data?.let { userData ->
                                UserModel(
                                    uid = foundUserId,
                                    username = userData["username"].toString(),
                                    photoUrl = userData["photoUrl"].toString(),
                                    flag = userData["flag"].toString(),
//                                canReceiveFriendReq = canReceiveFriendReq
                                )
                            }
                        }

                    // update the local _resultUsersList
                    _resultUsersList.value = foundUsers
                    _isSearching.value = false
                    onSuccess(foundUsers.size)
                }
                .addOnFailureListener {
                    _isSearching.value = false
                    onFail()
                }
        }
    }

//    private fun canReceiveFriendReq(
//        usersColl: CollectionReference,
//        userRef: DocumentReference,
//        foundUserId: String,
//    ): Deferred<Boolean> {
//        // N.B: the skip 2 & 3 are enclosed in skip 1 this one since it's async
//        usersColl
//            .whereEqualTo("uid", foundUserId)
//            .whereArrayContains("friendRequests", userRef)
//            .get()
//            .addOnSuccessListener { friendRequestsSnap ->
//                // skip 1: if it has a pending friend request from the current user
//                if (friendRequestsSnap.documents.isNotEmpty() ||
//                    // skip 2: if it is already a friend,
//                    _friendsList.value.any { it.uid == foundUserId } ||
//                    // skip 3: if the current user has a pending friend request from it
//                    _friendRequests.value.any { it.uid == foundUserId }
//                ) {
//                    false
//                }
//            }
//    }

    fun sendFriendReq(
        friendId: String,
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        /* TODO:
            * Implement FCM method to receive friend requests
            * - It should modify the _friendRequests value so that the "Requests" tab can get automatic recompositions
        */
        viewModelScope.launch(Dispatchers.IO) {
            // update the Firebase friend's friendRequests
            val usersRef = FirebaseFirestore.getInstance().collection("users")
            val friendRef = usersRef.document(friendId)
            val userRef = usersRef.document(_user.uid)
            friendRef.update("friendRequests", FieldValue.arrayUnion(userRef))
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFail() }
        }
    }
}

/* resultUsersList is initially a Flow:
* It is triggered if either one of its 2 dependency userQuery or _resultUsersList changes */
//    @OptIn(FlowPreview::class)
//    val resultUsersList = userQuery
//        .debounce(1000L) // delay of 1s to continue
//        .onEach { _isSearching.update { true } } // initially it's loading
//        .combine(_resultUsersList) { query, users ->
//            if (query.isBlank()) {
//                listOf() // set resultUsersList as empty list if no query
//            } else {
//                users.filter {
//                    it.matchesSearchQuery(query)
//                }
//            }
//        }
//        .onEach { _isSearching.update { false } } // then it's not loading anymore
//        // now convert resultUsersList to a StateFlow, to cache its latest value as usual
//        .stateIn(
//            viewModelScope, // ctx
//            SharingStarted.WhileSubscribed(5000), // to execute the combine block for 5s more, after subscriber disappears
//            _resultUsersList.value // initial value
//        )