package com.bestapp.rice.model.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImagePostSubmitUi(
    val userDocumentID: String,
    val images: List<String>,
): Parcelable
