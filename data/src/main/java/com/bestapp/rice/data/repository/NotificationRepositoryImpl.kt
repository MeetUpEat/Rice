package com.bestapp.zipbab.data.repository

import com.bestapp.zipbab.data.di.NetworkProviderModule
import com.bestapp.zipbab.data.notification.DownloadToken
import com.bestapp.zipbab.data.notification.SendNotificationRequest
import com.bestapp.zipbab.data.notification.setup.KaKaoService
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    @NetworkProviderModule.KakaoNotificationRetrofit private val kaKaoService: KaKaoService
) : NotificationRepository {
    override suspend fun registerToken(
        uuid: String,
        deviceId: String,
        pushType: String,
        pushToken: String
    ) {
        kaKaoService.registerToken(uuid = uuid, deviceId = deviceId, pushType = pushType, pushToken = pushToken)
    }

    override suspend fun downloadToken(uuid: String) : DownloadToken {
        return kaKaoService.downloadToken(uuid = uuid)
    }

    override suspend fun deleteToken(uuid: String, deviceId: String, pushType: String) {
        kaKaoService.deleteToken(uuid = uuid, deviceId = deviceId, pushType = pushType)
    }

    override suspend fun sendNotification(sendInfo: SendNotificationRequest) {
        kaKaoService.sendNotification(sendInfo)
    }

}