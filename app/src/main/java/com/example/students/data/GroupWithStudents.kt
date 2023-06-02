package com.example.students.data

import androidx.room.Embedded
import androidx.room.Relation

data class GroupWithStudents(
    @Embedded val group: Group,
    @Relation(
        parentColumn = "id",
        entityColumn = "group_id"
    )
    val students: List<Student>
)