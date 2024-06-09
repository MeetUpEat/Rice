package com.bestapp.rice.data.repository

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.bestapp.rice.data.FirestorDB.FirestoreDB
import com.bestapp.rice.data.doneSuccessful
import com.bestapp.rice.data.model.remote.Post
import com.bestapp.rice.data.model.remote.Review
import com.bestapp.rice.data.model.remote.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val firestoreDB : FirestoreDB,
    private val storageRepository: StorageRepository
) : UserRepository {

    override suspend fun getUser(userDocumentId: String): User {
        val users = firestoreDB.getUsersDB()
            .whereEqualTo("userDocumentID", userDocumentId)
            .get()
            .await()

        for (document in users) {
            return document.toObject<User>()
        }

        return FAKE_USER
    }

    override suspend fun login(id: String, pw: String): Boolean {
        val users = firestoreDB.getUsersDB()
            .whereEqualTo("id", id)
            .get()
            .await()

        for (document in users) {
            val user = document.toObject<User>()
            return user.pw == pw
        }

        return false
    }

    override suspend fun signUpUser(user: User): Pair<String, Boolean> {
        val userDocumentRef = firestoreDB.getUsersDB()
            .add(user)
            .await()
        val userDocumentId = userDocumentRef.id

        return Pair(userDocumentId,
            firestoreDB.getUsersDB().document(userDocumentId)
            .update("userDocumentID", userDocumentId)
            .doneSuccessful()
        )
    }

    override suspend fun signOutUser(userDocumentId: String): Boolean {
        return firestoreDB.getUsersDB().document(userDocumentId)
            .delete()
            .doneSuccessful()
    }

    override suspend fun updateUserNickname(userDocumentId: String, nickname: String): Boolean {
        return firestoreDB.getUsersDB().document(userDocumentId)
            .update("nickname", nickname)
            .doneSuccessful()
    }

    override suspend fun updateUserTemperature(reviews: List<Review>): Boolean {
        val isSuccessfuls = Array<Boolean>(reviews.size) { false }

        reviews.forEachIndexed { i, review ->
            if (review.votingPoint == 0.0) {
                isSuccessfuls[i] = true
                return@forEachIndexed
            }

            isSuccessfuls[i] = firestoreDB.getUsersDB().document(review.id)
                .update("temperature", FieldValue.increment(review.votingPoint))
                .doneSuccessful()
        }

        return isSuccessfuls.all { it == true }
    }

    override suspend fun updateUserMeetingCount(userDocumentID: String): Boolean {
        return firestoreDB.getUsersDB().document(userDocumentID)
            .update("meetingCount", FieldValue.increment(1))
            .doneSuccessful()
    }

    override suspend fun updateUserProfileImage(userDocumentID: String, profileImageUri: String?): Boolean {
        val uri = if (profileImageUri.isNullOrBlank()) {
            USER_DEFAULT_IMAGE_URI
        } else {
            storageRepository.uploadImage(
                Uri.parse(profileImageUri)
            )
        }

        return firestoreDB.getUsersDB().document(userDocumentID)
            .update("profileImage", uri)
            .doneSuccessful()
    }

    override suspend fun convertImages(userDocumentID: String, images: List<Bitmap>): List<String> {
        val storageUris = Array<String>(images.size) { "" }

        images.forEachIndexed { i, bitmap ->
            storageUris[i] = storageRepository.uploadImage(bitmap)
        }

        return storageUris.toList()
    }

    override suspend fun addPost(userDocumentID: String, post: Post): Boolean {
        return firestoreDB.getUsersDB().document(userDocumentID)
            .update("posts", FieldValue.arrayUnion(post))
            .doneSuccessful()
    }

    companion object {
//        private val storageRepositoryImpl = StorageRepositoryImpl()

        private val FAKE_USER = User()
        private val USER_DEFAULT_IMAGE_URI = "https://firebasestorage.googleapis.com/v0/b/food-879fc.appspot.com/o/images%2F1717515927323.jpg?alt=media&token=026118a6-50ff-4add-a371-5d7f7feda46c"
    }
}