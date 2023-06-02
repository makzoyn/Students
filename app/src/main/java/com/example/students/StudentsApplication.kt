package com.example.students

import android.app.Application
import android.content.Context
import com.example.students.repository.AppRepository

class StudentsApplication : Application() {
    override  fun onCreate(){
        super.onCreate()
        AppRepository.newInstance()
    }
    init{
        instance = this
    }
    companion object{
        private var instance: StudentsApplication? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}