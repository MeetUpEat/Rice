package com.bestapp.zipbab.model

import com.bestapp.zipbab.data.model.remote.FilterResponse
import com.bestapp.zipbab.data.model.remote.MeetingResponse
import com.bestapp.zipbab.data.model.remote.NotificationTypeResponse
import com.bestapp.zipbab.data.model.remote.PlaceLocation
import com.bestapp.zipbab.data.model.remote.PostResponse
import com.bestapp.zipbab.data.model.remote.Review
import com.bestapp.zipbab.data.model.remote.TermInfoResponse
import com.bestapp.zipbab.data.model.remote.UserResponse
import com.bestapp.zipbab.model.args.FilterUi
import com.bestapp.zipbab.model.args.ImageUi
import com.bestapp.zipbab.model.args.MeetingUi
import com.bestapp.zipbab.model.args.PlaceLocationUi
import com.bestapp.zipbab.model.args.PostUi
import com.bestapp.zipbab.model.args.ProfileEditUi
import com.bestapp.zipbab.model.args.SelectImageUi
import com.bestapp.zipbab.model.args.UserActionUi
import com.bestapp.zipbab.ui.profile.ProfileUiState
import com.bestapp.zipbab.ui.profileedit.ProfileEditUiState
import com.bestapp.zipbab.ui.profileimageselect.GalleryImageInfo
import com.bestapp.zipbab.ui.profilepostimageselect.model.PostGalleryUiState
import com.bestapp.zipbab.ui.profilepostimageselect.model.SelectedImageUiState

// Data -> UiState

fun FilterResponse.Cost.toUiState() = FilterUiState.CostUiState(
    name = name,
    icon = icon,
    type = type,
)

fun FilterResponse.Food.toUiState() = FilterUiState.FoodUiState(
    icon = icon,
    name = name,
)

fun MeetingResponse.toUiState() = MeetingUiState(
    meetingDocumentID = meetingDocumentID,
    title = title,
    titleImage = titleImage,
    placeLocationUiState = placeLocation.toUiState(),
    time = time,
    recruits = recruits,
    description = description,
    mainMenu = mainMenu,
    costValueByPerson = costValueByPerson,
    costTypeByPerson = costTypeByPerson,
    hostUserDocumentID = hostUserDocumentID,
    hostTemperature = hostTemperature,
    members = members,
    pendingMembers = pendingMembers,
    attendanceCheck = attendanceCheck,
    activation = activation
)

fun MeetingResponse.toUi() = MeetingUi(
    meetingDocumentID = meetingDocumentID,
    title = title,
    titleImage = titleImage,
    placeLocationUi = PlaceLocationUi(
        locationAddress = placeLocation.locationAddress,
        locationLat = placeLocation.locationLat,
        locationLong = placeLocation.locationLong,
    ),
    time = time,
    recruits = recruits,
    description = description,
    mainMenu = mainMenu,
    costValueByPerson = costValueByPerson,
    costTypeByPerson = costTypeByPerson,
    hostUserDocumentID = hostUserDocumentID,
    hostTemperature = hostTemperature,
    members = members,
    pendingMembers = pendingMembers,
    attendanceCheck = attendanceCheck,
    activation = activation,
)

fun PlaceLocation.toUiState() = PlaceLocationUiState(
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLong = locationLong,
)

fun PostResponse.toUiState() = PostUiState(
    postDocumentID = postDocumentID,
    images = images,
)

fun Review.toUiState() = ReviewUiState(
    id = id,
    votingPoint = votingPoint,
)

fun TermInfoResponse.toUiState() = TermInfoState(
    id = id,
    content = content,
    date = date,
)

fun UserResponse.toUiState() = UserUiState(
    userDocumentID = userDocumentID,
    uuid = uuid,
    nickname = nickname,
    id = id,
    pw = pw,
    profileImage = profileImage,
    temperature = temperature,
    meetingCount = meetingCount,
    notificationUiState = notificationList.map { it.toUiState() },
    meetingReviews = meetingReviews,
    postDocumentIds = posts,
    placeLocationUiState = placeLocation.toUiState(),
)

// UiState -> Data

fun NotificationTypeResponse.toUiState() = when (this) {
    is NotificationTypeResponse.MainResponseNotification -> {
        NotificationUiState.MainNotification(dec = dec, uploadDate = uploadDate)
    }

    is NotificationTypeResponse.UserResponseNotification -> {
        NotificationUiState.UserNotification(dec = dec, uploadDate = uploadDate)
    }
}

fun PlaceLocationUiState.toData() = PlaceLocation(
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLong = locationLong
)

fun PostUiState.toData() = PostResponse(
    postDocumentID = postDocumentID,
    images = images,
)


// UiState -> ActionArgs

fun UserUiState.toUi() = UserActionUi(
    userDocumentID = userDocumentID,
    uuid = uuid,
    nickname = nickname,
    id = id,
    pw = pw,
    profileImage = profileImage,
    temperature = temperature,
    meetingCount = meetingCount,
    meetingReviews = meetingReviews,
    postDocumentIds = postDocumentIds,
    placeLocationUi = placeLocationUiState.toUi(),
)

fun PlaceLocationUiState.toUi() = PlaceLocationUi(
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLong = locationLong,
)

fun PostUiState.toUi() = PostUi(
    postDocumentID = postDocumentID,
    images = images,
)

fun FilterUiState.FoodUiState.toUi() = FilterUi.FoodUi(
    icon = icon,
    name = name,
)

fun FilterUiState.CostUiState.toUi() = FilterUi.CostUi(
    icon = icon,
    name = name,
    type = type,
)

fun ProfileUiState.toProfileEditUi() = ProfileEditUi(
    userDocumentID = userDocumentID,
    nickname = nickname,
    profileImage = profileImage,
)

fun GalleryImageInfo.toUi() = ImageUi(
    uri = uri,
    name = name,
)

fun SelectedImageUiState.toUi() = SelectImageUi(
    uri = uri,
)

// UiState -> UiState
fun GalleryImageInfo.toPostGalleryState() = PostGalleryUiState(
    uri = uri,
    name = name,
)

fun PostGalleryUiState.toSelectUiState() = SelectedImageUiState(
    uri = uri,
    name = name,
    order = order,
)

fun SelectedImageUiState.toGalleryUiState() = PostGalleryUiState(
    uri = uri,
    name = name,
    order = order,
)

// Ui -> UiState

fun ProfileEditUi.toUiState() = ProfileEditUiState(
    userDocumentID = userDocumentID,
    nickname = nickname,
    profileImage = profileImage,
)