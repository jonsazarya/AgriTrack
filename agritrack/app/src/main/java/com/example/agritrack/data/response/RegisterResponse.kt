package com.example.agritrack.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("error")
	val message: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)
