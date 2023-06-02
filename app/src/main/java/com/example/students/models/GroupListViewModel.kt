package com.example.students.models


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.students.data.Faculty
import com.example.students.data.Group
import com.example.students.data.Student
import com.example.students.repository.AppRepository
import kotlinx.coroutines.launch

class GroupListViewModel: ViewModel() {
    var group: MutableLiveData<List<Student>> = MutableLiveData()
    private var groupID: Long =-1

    init {
        AppRepository.get().group.observeForever{
            group.postValue(it)
        }
    }
    //метод для изменения идентификатора факультета
    fun setGroupID(groupID : Long){
        this.groupID = groupID
        loadStudents()
    }

    fun loadStudents() {
        viewModelScope.launch {
            AppRepository.get().getGroupStudents(groupID)
        }
    }

    suspend fun getGroup() : Group?{
        var f : Group?=null
        val job = viewModelScope.launch {
            f = AppRepository.get().getGroup(groupID)
        }
        job.join()
        return f
    }

    suspend fun deleteStudent(student: Student) = AppRepository.get().deleteStudent(student)
}