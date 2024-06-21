package com.art3mvp.newsclient.data.model

import com.google.gson.annotations.SerializedName

data class UserResponseDto(
    @SerializedName("response") val userContainer: List<UserContainerDto>
)