package com.example.students.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.*


@Entity(tableName = "university")
data class Faculty(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "faculty_name") var name: String?,
)
