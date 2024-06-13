package com.bestapp.rice.ui.meetupmap

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.MeetingRepository
import com.bestapp.rice.data.repository.UserRepository
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.label.Label
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetUpMapViewModel @Inject constructor(
    private val appSettingRepository: AppSettingRepository,
    private val userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
) : ViewModel() {

    private val _meetUpMapUiState = MutableStateFlow<MeetUpMapUiState>(MeetUpMapUiState())
    val meetUpMapUiState: SharedFlow<MeetUpMapUiState> = _meetUpMapUiState.asStateFlow()

    private val _userNickname = MutableSharedFlow<String>()
    val userNickname: SharedFlow<String> = _userNickname

    private val _isLocationStarted = MutableSharedFlow<Boolean>()
    val isLocationStarted: SharedFlow<Boolean> get() = _isLocationStarted

    private val _userLocation = MutableSharedFlow<LatLng>()
    val userLocation: SharedFlow<LatLng> get() = _userLocation

    private val _meetingLabels = MutableStateFlow<List<Label>>(emptyList())
    val meetingLabels: StateFlow<List<Label>> get() = _meetingLabels

    suspend fun isUpdateUserLabel(latLng: LatLng) {
        _userLocation.emit(latLng)
        getMeetings(latLng)
    }

    suspend fun locationCollectStarted(state: Boolean) {
        _isLocationStarted.emit(state)
    }

    fun setMeetingLabels(labels: List<Label>) {
        _meetingLabels.value = labels
    }

    fun getUserNickname() {
        viewModelScope.launch {
            // TODO: 개발 편의성을 위함 / 최종 연동 시, 제거
            val userDocumentedID = getUser().ifEmpty {
                "0UserByPythonYlp7Vdv"
            }

            val user = userRepository.getUser(userDocumentedID)
            _userNickname.emit(user.nickname)
        }
    }

    private suspend fun getUser() = appSettingRepository.userPreferencesFlow.first()

    private fun getMeetings(latLngUser: LatLng) {
        viewModelScope.launch {
            // TODO: 미팅 생성 시, 위치 값을 넣고 있지 않아서 NumberFormatException 오류 발생함.
            //  완벽히 연동되기 전까지 안정성을 위해 filter 추가 사용
            val meetings = meetingRepository.getMeetings().filter {
                it.placeLocation.locationLat.isNotEmpty() && it.placeLocation.locationLong.isNotEmpty()
            }

            val meetUpMapUiState = MeetUpMapUiState(
                meetUpMapMeetingUis = meetings.map {
                    val latlng = LatLng.from(
                        it.placeLocation.locationLat.toDouble(),
                        it.placeLocation.locationLong.toDouble()
                    )

                    val distance = haversine(latLngUser, latlng)
                    it.toUi(distance)
                }
                    .filter { it.distanceByUser <= DISTANCE_FILTER }
                    .sortedBy { it.distanceByUser }
            )

            Log.e("내 근처 20km 이내 모임 리스트", "${meetUpMapUiState.meetUpMapMeetingUis}")

            _meetUpMapUiState.value = meetUpMapUiState
        }
    }

    companion object {
        val DEFAULT_MEETING_DOCUMENT_ID = "O84eyapKdqIgbjitZZIr"
        val DEFAULT_USER_DOCUMENT_ID = "yUKL3rt0geiVdQJMOeoF"

        // 사용자 위치 기반 탐색 가능한 거리 -> TODO : ui에서 filter를 통해 바꿀 수 있도록 해야함
        val DISTANCE_FILTER = 30.0
    }
}