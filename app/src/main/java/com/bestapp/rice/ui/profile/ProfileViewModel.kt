package com.bestapp.rice.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.AppSettingRepository
import com.bestapp.rice.data.repository.PostRepository
import com.bestapp.rice.data.repository.ReportRepository
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.PostUiState
import com.bestapp.rice.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appSettingRepository: AppSettingRepository,
    private val postRepository: PostRepository,
    private val reportRepository: ReportRepository,
) : ViewModel() {

    private val _profileUiState = MutableStateFlow(ProfileUiState())
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    private val _reportState = MutableStateFlow<ReportState>(ReportState.Default)
    val reportState: StateFlow<ReportState> = _reportState.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Default)
    val deleteState: StateFlow<DeleteState> = _deleteState.asStateFlow()

    private var pendingPostForDeletion: PostUiState = PostUiState()

    fun loadUserInfo(userDocumentID: String) {
        viewModelScope.launch {
            runCatching {
                val userUiState = userRepository.getUser(userDocumentID).toUiState()
                appSettingRepository.userPreferencesFlow.collect { selfDocumentId ->

                    _profileUiState.emit(
                        ProfileUiState(
                            userDocumentID = userUiState.userDocumentID,
                            nickname = userUiState.nickname,
                            profileImage = userUiState.profileImage,
                            temperature = userUiState.temperature,
                            meetingCount = userUiState.meetingCount,
                            postUiStates = postRepository.getPosts(userUiState.userDocumentID)
                                .map {
                                    it.toUiState()
                                }.reversed(),
                            isSelfProfile = userUiState.userDocumentID == selfDocumentId,
                            isProfileClicked = false,
                        )
                    )
                }
            }.onFailure {
                throw it
            }
        }
    }

    fun onProfileImageClicked() {
        if (_profileUiState.value.profileImage.isBlank()) {
            return
        }
        viewModelScope.launch {
            _profileUiState.emit(_profileUiState.value.copy(isProfileClicked = true))
        }
    }

    fun closeLargeProfile() {
        viewModelScope.launch {
            _profileUiState.emit(_profileUiState.value.copy(isProfileClicked = false))
        }
    }

    fun reportPost() {
        when (val state = _reportState.value) {
            is ReportState.PendingPost -> {
                viewModelScope.launch {
                    _reportState.emit(
                        ReportState.PendingPost(
                            state.userDocumentID,
                            state.postDocumentID,
                            state.isSelfProfile,
                        )
                    )
                    runCatching {
                        reportRepository.reportPost(state.userDocumentID, state.postDocumentID)
                        _reportState.emit(ReportState.Complete)
                    }.onFailure {
                        _reportState.emit(ReportState.Fail)
                    }
                }
            }

            else -> Unit
        }
    }

    fun resetReportState() {
        viewModelScope.launch {
            _reportState.emit(ReportState.Default)
        }
    }

    fun onPostClick(postUiState: PostUiState) {
        pendingPostForDeletion = postUiState

        viewModelScope.launch {
            _reportState.emit(
                ReportState.PendingPost(
                    _profileUiState.value.userDocumentID,
                    postUiState.postDocumentID,
                    _profileUiState.value.isSelfProfile,
                )
            )
        }
    }

    fun reportUser() {
        when (_reportState.value) {
            is ReportState.Default -> {
                val userDocumentID = _profileUiState.value.userDocumentID
                if (userDocumentID.isBlank()) {
                    return
                }
                viewModelScope.launch {
                    _reportState.emit(
                        ReportState.ProgressProfile(
                            userDocumentID
                        )
                    )
                    runCatching {
                        reportRepository.reportUser(_profileUiState.value.userDocumentID)
                        _reportState.emit(ReportState.Complete)
                    }.onFailure {
                        _reportState.emit(ReportState.Fail)
                    }
                }
            }

            else -> Unit
        }
    }

    fun onPostLongClick(postUiState: PostUiState) {
        pendingPostForDeletion = postUiState
        onDeletePost()
    }

    fun onDeletePost() {
        // 본인 프로필이 아니거나, 삭제가 진행 중인 경우
        if (_profileUiState.value.isSelfProfile.not() || _deleteState.value is DeleteState.Progress) {
            return
        }
        viewModelScope.launch {
            _deleteState.emit(DeleteState.Pending)
        }
    }

    fun deletePost() {
        viewModelScope.launch {
            _deleteState.emit(DeleteState.Progress)

            // 게시물 삭제하기
            runCatching {
                val isSuccess = postRepository.deletePost(
                    _profileUiState.value.userDocumentID,
                    pendingPostForDeletion.postDocumentID
                )
                if (isSuccess) {
                    _deleteState.emit(DeleteState.Complete)
                    _profileUiState.emit(_profileUiState.value.copy(postUiStates = _profileUiState.value.postUiStates.filter {
                        it.postDocumentID != pendingPostForDeletion.postDocumentID
                    }))
                    pendingPostForDeletion = PostUiState()
                } else {
                    _deleteState.emit(DeleteState.Fail)
                }
            }.onFailure {
                _deleteState.emit(DeleteState.Fail)
            }
        }
    }

    fun resetDeleteState() {
        viewModelScope.launch {
            _deleteState.emit(DeleteState.Default)
        }
    }
}