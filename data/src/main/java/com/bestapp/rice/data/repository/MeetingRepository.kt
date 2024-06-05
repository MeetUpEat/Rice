package com.bestapp.rice.data.repository

import com.bestapp.rice.data.model.remote.Meeting

interface MeetingRepository {
    suspend fun getMeeting(meetingDocumentID: String): List<Meeting>
    suspend fun getMeetingByUserDocumentID(userDocumentID: String): List<Meeting>

    suspend fun getSearch(meetingDocumentID: String): List<Meeting>

    suspend fun getFoodMeeting(mainMenu: String): List<Meeting>

    suspend fun getCostMeeting(costType: Int): List<Meeting>

    suspend fun createMeeting(meeting: Meeting): Boolean

    suspend fun updateAttendanceCheckMeeting(meetingDocumentID: String, userDocumentId: String): Boolean

    suspend fun endMeeting(meetingDocumentID: String): Boolean

    // 참여 대기중인 멤버리스트에 신청자 추가하기
    suspend fun addPendingMember(meetingDocumentID: String, userDocumentId: String): Boolean

    // 참여 대기중인 멤버를 참여자 리스트로 옮겨주기
    suspend fun approveMember(meetingDocumentID: String, userDocumentId: String): Boolean

    // pendingmembers 리스트에서 해당 멤버를 제거하기
    suspend fun rejectMember(meetingDocumentID: String, userDocumentId: String): Boolean
}