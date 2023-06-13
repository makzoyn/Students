package com.example.students.api

import com.example.students.data.Faculty
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ServerAPI {
    @GET(".")
    suspend fun getUniversity(): Response<University>

    @POST(".")
    suspend fun postUniversity(@Body university: University): Response<University>
}
