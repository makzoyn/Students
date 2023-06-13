package com.example.students.api

data class FacultyWithGroups(
    val id: Long,
    val name: String,
    val groups: List<GroupsWithStudents>
)
