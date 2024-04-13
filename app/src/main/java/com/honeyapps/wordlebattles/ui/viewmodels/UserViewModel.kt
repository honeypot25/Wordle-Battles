package com.honeyapps.wordlebattles.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import com.honeyapps.wordlebattles.data.models.SignedUserModel
import com.honeyapps.wordlebattles.network.MyHttpClient
import com.honeyapps.wordlebattles.utils.LocationUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.Request
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.File

class UserViewModel : ViewModel() {

    private val user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    private val _userState = MutableStateFlow(SignedUserModel())
    val userState = _userState.asStateFlow()

    private val _isContentLoading = MutableStateFlow(false)
    val isContentLoading = _isContentLoading.asStateFlow()

    init {
        fetchUser()
    }

    private fun fetchUser() {
        // user has been already fetched
//        if (_userState.value.uid.isNotEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            val userData: Map<String, Any>
            val usersRef = FirebaseFirestore.getInstance().collection("users")
            // get the Firestore user if it exists
            val userSnap = usersRef.document(user.uid).get().await()
            if (userSnap.exists()) {
                userData = userSnap.data!!
                // otherwise add it using default values
            } else {
                userData = mapOf(
                    "username" to user.displayName!!,
                    "email" to user.email!!,
                    "photoUrl" to user.photoUrl.toString(),
                    "flag" to "\uD83C\uDFF4\u200D☠\uFE0F",
//                    "coins" to 0,
                    // handleded only by Firestore:
                    "friends" to listOf<DocumentReference>(),
                    "friendRequests" to listOf<DocumentReference>(),
                    "challengeRequests" to listOf<Map<String, Any>>()
                )
                usersRef.document(user.uid).set(userData)
            }

            // in any case, update the userState through the populated userData
            _userState.update {
                it.copy(
                    uid = user.uid,
                    username = userData["username"].toString(),
                    email = userData["email"].toString(),
                    photoUrl = userData["photoUrl"].toString(),
                    flag = userData["flag"].toString(),
//                    coins = userData["coins"].toString().toInt()
                )
            }
        }
    }

    fun changeUsername(
        newUsername: String,
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val userRef = FirebaseFirestore.getInstance().collection("users").document(user.uid)
            userRef.update("username", newUsername)
                .addOnSuccessListener {
                    _userState.update {
                        it.copy(
                            username = newUsername
                        )
                    }
                    onSuccess()
                }
                .addOnFailureListener { onFail() }
        }
    }

    fun changePhoto(
        photoUri: Uri,
        isFromCamera: Boolean = false,
        onSuccess: () -> Unit,
        onFail: () -> Unit,
        ctx: Context
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isContentLoading.value = true
            val userRef = FirebaseFirestore.getInstance().collection("users").document(user.uid)
            val photoRef = FirebaseStorage.getInstance().reference.child("photos/${user.uid}.jpg")
            val metadata = storageMetadata {
                contentType = "image/jpg"
            }

            // always compress the photo for 25% quality
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(ctx.contentResolver, photoUri))
            } else {
                MediaStore.Images.Media.getBitmap(ctx.contentResolver, photoUri)
            }
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos)
            val compressedPhoto = baos.toByteArray()

            // upload task
            photoRef.putBytes(compressedPhoto, metadata)
                // on success, get a downloadable url of the photo
                .addOnSuccessListener {
                    photoRef.downloadUrl // getDownloadUrl()
                        // used to update the Firestore photoUrl field
                        .addOnSuccessListener { uri ->
                            val newPhotoUrl = uri.toString()
                            userRef.update("photoUrl", newPhotoUrl)
                                // and the photoUrl field of UserState
                                .addOnSuccessListener {
                                    _userState.update {
                                        it.copy(
                                            photoUrl = newPhotoUrl
                                        )
                                    }
                                    _isContentLoading.value = false
                                    onSuccess()
                                    // if a Camera photo, delete the cached file after updating
                                    if (isFromCamera) {
                                        File(ctx.externalCacheDir, photoUri.lastPathSegment.toString()).delete()
                                    }
                                }
                                .addOnFailureListener {
                                    Log.e("Firestore.update", "Error: ${it.message}")
                                    _isContentLoading.value = false
                                    onFail()
                                }
                        }
                        .addOnFailureListener {
                            Log.e("Storage.downloadUrl", "Error: ${it.message}")
                            _isContentLoading.value = false
                            onFail()
                        }
                }
                .addOnFailureListener {
                    Log.e("Storage.putFile","Error: ${it.message}")
                    _isContentLoading.value = false
                    onFail()
                }
        }
    }

    fun changeFlag(
        ctx: Context,
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isContentLoading.value = true
            val userRef = FirebaseFirestore.getInstance().collection("users").document(user.uid)
            val country = LocationUtil.getCountryName(ctx = ctx)

            // early exit if unknown country from Location
            if (country == null) {
                onFail()
                return@launch
            }

            val req = Request.Builder().url("https://emojihub.yurace.pro/api/all/category/flags").build()

            MyHttpClient.httpClient().newCall(req).execute().use { res ->
                if (res.isSuccessful) {
                    // create a name-to-unicode map for each entry
                    val resBodyStr = res.body?.string()!!
                    val flags = JSONArray(resBodyStr)
                    val nameToUnicodeMap = mutableMapOf<String, List<String>>()
                    for (i in 0 until flags.length()) {
                        val flag = flags.getJSONObject(i)
                        val name = flag.getString("name")
                        val uCodePoints = flag.getJSONArray("unicode")
                        nameToUnicodeMap[name] = uCodePoints.toList()
                    }
                    // get the flag codepoints for my country
                    // or default to pirate flag codepoints: listOf("U+1F3F4", "U+200D", "U+2620", "U+FE0F")
                    val uCodePoints: List<String>? = nameToUnicodeMap[country]

                    // early exit if unknown country from API
                    if (uCodePoints == null) {
                        onFail()
                        return@launch
                    }

                    // array of int(16) codepoints
                    val hCodePoints = uCodePoints.map { it.substring(2).toInt(16) }.toIntArray()
                    // store the new flag as emoji (interpreted unicode string)
                    val newFlag = String(hCodePoints, 0, hCodePoints.size)

                    userRef.update("flag", newFlag)
                        .addOnSuccessListener {
                            _userState.update {
                                it.copy(
                                    flag = newFlag
                                )
                            }
                            onSuccess()
                        }
                        .addOnFailureListener {
                            onFail()
                        }
                } else {
                    Log.e("changeFlag", "Failed to reach emojihub")
                }

                _isContentLoading.value = false
            }
        }
    }

    private fun JSONArray.toList(): List<String> {
        return List(length()) { get(it) as String }
    }
}