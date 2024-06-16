package com.bestapp.zipbab.model.args

import android.os.Parcelable
import com.bestapp.zipbab.data.model.remote.Meeting
import com.bestapp.zipbab.data.model.remote.PlaceLocation
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeetingUi(
    val meetingDocumentID: String,
    val title: String,
    val titleImage: String,
    val placeLocationUi: PlaceLocationUi,
    val time: String,
    val recruits: Int,
    val description: String,
    val mainMenu: String,
    val costValueByPerson: Int,
    val costTypeByPerson: Int,
    val hostUserDocumentID: String,
    val hostTemperature: Double,
    val members: List<String>,
    val pendingMembers: List<String>,
    val attendanceCheck: List<String>,
    val activation: Boolean,
) : Parcelable

fun Meeting.toUi() = MeetingUi(
    meetingDocumentID = meetingDocumentID,
    title = title,
    titleImage = titleImage,
    placeLocationUi = placeLocation.toUi(),
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

fun PlaceLocation.toUi() = PlaceLocationUi(
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLong = locationLong
)