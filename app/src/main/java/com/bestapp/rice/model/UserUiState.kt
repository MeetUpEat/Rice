package com.bestapp.rice.model

import com.bestapp.rice.data.model.remote.NotificationType

/**
 * Empty -> default parameter로 대체하기
 * Pacelable을 위한 별도의 클래스 만들기
 * sealed interface로 loading
 */
data class UserUiState(
    val userDocumentID: String = "",
    val uuid: String = "",
    val nickname: String = "",
    val id: String = "",
    val pw: String = "",
    val profileImage: String = "",
    val temperature: Double = 0.0,
    val meetingCount: Int = 0,
    val notificationList: List<NotificationType> = listOf(),
    val meetingReviews: List<String> = listOf(),
    val postUiStates: List<PostUiState> = listOf(),
    val placeLocationUiState: PlaceLocationUiState = PlaceLocationUiState(),
)  {
    val isLoggedIn: Boolean
        get() = userDocumentID.isNotBlank()
}