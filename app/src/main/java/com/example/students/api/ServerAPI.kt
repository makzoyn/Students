package com.example.students.api

import com.example.students.data.Faculty
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ServerAPI {
    @GET("/students/fetch")
    suspend fun getUniversity(): Response<List<Faculty>>

    @POST("/students/create")
    suspend fun postUniversity(@Body university: List<Faculty>): Response<List<Faculty>>
}
