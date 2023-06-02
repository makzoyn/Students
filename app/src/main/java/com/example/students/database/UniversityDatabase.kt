package com.example.students.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.students.dao.UniversityDAO
import com.example.students.data.Faculty
import com.example.students.data.Group
import com.example.students.data.Student

@Database(
    version = 1,
    entities = [
        Faculty::class,
        Group::class,
        Student::class
    ]
)
abstract class UniversityDatabase : RoomDatabase(){
    abstract fun getDao(): UniversityDAO
}