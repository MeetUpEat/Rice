package com.bestapp.zipbab.data.di

import com.bestapp.zipbab.data.repository.AppSettingRepository
import com.bestapp.zipbab.data.repository.AppSettingRepositoryImpl
import com.bestapp.zipbab.data.repository.CategoryRepository
import com.bestapp.zipbab.data.repository.CategoryRepositoryImpl
import com.bestapp.zipbab.data.repository.MeetingRepository
import com.bestapp.zipbab.data.repository.MeetingRepositoryImpl
import com.bestapp.zipbab.data.repository.NotificationRepository
import com.bestapp.zipbab.data.repository.NotificationRepositoryImpl
import com.bestapp.zipbab.data.repository.PostRepository
import com.bestapp.zipbab.data.repository.PostRepositoryImpl
import com.bestapp.zipbab.data.repository.ReportRepository
import com.bestapp.zipbab.data.repository.ReportRepositoryImpl
import com.bestapp.zipbab.data.repository.SearchLocationRepository
import com.bestapp.zipbab.data.repository.SearchLocationRepositoryImpl
import com.bestapp.zipbab.data.repository.StorageRepository
import com.bestapp.zipbab.data.repository.StorageRepositoryImpl
import com.bestapp.zipbab.data.repository.UserRepository
import com.bestapp.zipbab.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelScoped::class)
internal abstract class RepositoryModule {

    @Binds
    abstract fun bindAppSettingRepository(appSettingRepositoryImpl: AppSettingRepositoryImpl): AppSettingRepository

    @Binds
    abstract fun bindCategoryRepository(categoryRepositoryImpl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    abstract fun bindMeetingRepository(meetingRepositoryImpl: MeetingRepositoryImpl): MeetingRepository

    @Binds
    abstract fun bindSearchLocationRepository(searchLocationRepositoryImpl: SearchLocationRepositoryImpl): SearchLocationRepository

    @Binds
    abstract fun bindStorageRepository(storageRepositoryImpl: StorageRepositoryImpl): StorageRepository

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindPostRepository(postRepositoryImpl: PostRepositoryImpl): PostRepository

    @Binds
    abstract fun bindNotifyRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    abstract fun bindReportRepository(reportRepositoryImpl: ReportRepositoryImpl): ReportRepository
}