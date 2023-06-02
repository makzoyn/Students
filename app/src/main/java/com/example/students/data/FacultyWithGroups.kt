package com.example.students.data

import androidx.room.Embedded
import androidx.room.Relation

data class FacultyWithGroups(
    @Embedded val faculty: Faculty,
    @Relation(
        parentColumn = "id",
        entityColumn = "faculty_id"
    )
    val groups: List<Group>
)