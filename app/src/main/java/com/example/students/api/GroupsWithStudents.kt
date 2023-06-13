package com.example.students.api

import com.example.students.data.Student

data class GroupsWithStudents (
    val facultyId: Long,
    val id: Long,
    val name: String,
    val students: List<Student>
    )