package com.bestapp.rice.data.repository

import com.bestapp.rice.data.model.remote.kakaomap.SearchLocation
import com.bestapp.rice.data.network.SearchLocationService

class SearchLocationRepositoryImpl(
    private val searchLocationService: SearchLocationService
): SearchLocationRepository {
    override suspend fun convertLocation(
        query: String,
        analyzeType: String,
        page: Int,
        size: Int,
    ): SearchLocation {
        return searchLocationService.convertLocation(
            query,
            analyzeType,
            page,
            size,
        )
    }
}