package com.bestapp.rice.model.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserActionArg(
    val userDocumentID: String = "",
    val uuid: String = "",
    val nickname: String,
    val id: String,
    val pw: String,
    val profileImage: String,
    val temperature: Double,
    val meetingCount: Int,
    val meetingReviews: List<String>,
    val postArgs: List<PostArg>,
    val placeLocationArg: PlaceLocationArg,
): Parcelable
