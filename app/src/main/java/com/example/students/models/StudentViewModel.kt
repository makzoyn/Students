package com.example.students.models

import androidx.lifecycle.ViewModel
import com.example.students.data.Student
import com.example.students.repository.AppRepository

class StudentViewModel : ViewModel() {
    suspend fun newStudent(student: Student, groupID: Long) = AppRepository.get().newStudent(student, groupID)
    suspend fun editStudent(student: Student) = AppRepository.get().editStudent(student)
}