package com.bestapp.zipbab.data.model.remote.kakaomap

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @param address 지명 주소지 정보
 * @param roadAddress 도로명 주소지 정보
 * @param addressName 전체 지번 주소 또는 전체 도로명 주소, 입력에 따라 결정됨
 * @param addressType REGION(지명), ROAD(도로명), REGION_ADDR(지번 주소), ROAD_ADDR(도로명 주소)
 * @param x 경도 127,xxxxxxxxxxx
 * @param y 위도 37.xxxxxxxxxxxxx
 */
@JsonClass(generateAdapter = true)
data class Document(
    @field:Json(name = "address") val address: Address,
    @Json(name = "x") val longitude: String,
    @Json(name = "y") val latitude: String,
    @Json(name = "address_name") val addressName: String,
    @Json(name = "address_type") val addressType: String,
    // TODO: (동일한 조건에서) Postman에서는 null이 아니지만,
    //       API 호출 시 null로 반환되는 이슈가 있어 nullable 처리함
    @field:Json(name = "road_address") val roadAddress: RoadAddress?,
)