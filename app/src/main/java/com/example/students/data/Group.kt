package com.example.students.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "faculty",
    indices = [Index("group_name")],
    foreignKeys = [
        ForeignKey(
            entity = Faculty::class,
            parentColumns = ["id"],
            childColumns = ["faculty_id"],
            onDelete = ForeignKey.CASCADE)
    ]
)
data class Group(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "group_name") var name: String?,
    @ColumnInfo(name = "faculty_id") val facultyID: Long?,
)