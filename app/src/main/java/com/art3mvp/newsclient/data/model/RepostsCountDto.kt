package com.art3mvp.newsclient.data.model

import com.google.gson.annotations.SerializedName

data class RepostsCountDto(
    @SerializedName("count")
    val count: Long
)
