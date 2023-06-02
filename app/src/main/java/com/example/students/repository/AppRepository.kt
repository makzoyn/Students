package com.example.students.repository

import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.students.StudentsApplication
import com.example.students.api.ServerAPI
import com.example.students.data.Faculty
import com.example.students.data.Group
import com.example.students.data.Student
import com.example.students.database.UniversityDatabase
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit

class AppRepository private constructor() {
    var university: MutableLiveData<List<Faculty>> = MutableLiveData()
    var faculty: MutableLiveData<List<Group>> = MutableLiveData()
    var group: MutableLiveData<List<Student>> = MutableLiveData()

    companion object{
        private var INSTANCE: AppRepository? = null

        fun newInstance(){
            if (INSTANCE == null){
                INSTANCE = AppRepository()
                INSTANCE!!.getAPI()
            }
        }
        fun get(): AppRepository{
            return INSTANCE?:
            throw IllegalAccessException("Репозиторий не инициализирован")
        }


    }

    private var myServerAPI : ServerAPI? = null

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun getAPI(){
        val url = "http://10.0.2.2:8080/"
        Retrofit.Builder()
            .baseUrl("http://${url}")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().apply {
                myServerAPI = create(ServerAPI::class.java)
            }
    }
    suspend fun saveUniversityOnServer() {
        val universityData = getLocalUniversity()

        val job = CoroutineScope(Dispatchers.IO).launch {
            myServerAPI!!.postUniversity(universityData)
        }
        job.join()
    }

    suspend fun getLocalUniversity(): List<Faculty> {
        val faculties = universityDao.loadUniversity()
        val universityData = ArrayList<Faculty>()

        for (faculty in faculties) {
            val groups = universityDao.loadFacultyGroup(faculty.id!!)
            val groupList = ArrayList<Group>()

            for (group in groups) {
                val students = universityDao.loadGroupStudents(group.id!!)
                val studentList = ArrayList<Student>()

                for (student in students) {
                    val studentData = Student(
                        student.id,
                        student.firstName,
                        student.lastName,
                        student.middleName,
                        student.phone,
                        student.birthDate,
                        student.groupID
                    )
                    studentList.add(studentData)
                }

                group.students = studentList
                groupList.add(group)
            }

            faculty.groups = groupList
            universityData.add(faculty)
        }

        return universityData
    }


    fun getServerFaculty(){
        if (myServerAPI != null) {
            CoroutineScope(Dispatchers.Main).launch {
                fetchFaculty()
            }
        }
    }

    private suspend fun fetchFaculty() {
        if (myServerAPI != null) {
            val job = CoroutineScope(Dispatchers.IO).launch {
                val r = myServerAPI!!.getUniversity()
                if (r.isSuccessful){
                    val job = CoroutineScope(Dispatchers.IO).launch {
                        universityDao.deleteAllFaculty()
                    }
                    job.join()
                    val facultyList = r.body()
                    if (facultyList != null) {
                        for (f in facultyList) {
                            universityDao.insertNewFaculty(f)
                        }
                    }
                }
            }
            job.join()
            loadFaculty()
        }
    }


    val db = Room.databaseBuilder(
        StudentsApplication.applicationContext(),
        UniversityDatabase::class.java, "uniDB.db"
    ).build()

    val universityDao = db.getDao()

    suspend fun newFaculty (name: String){
        val faculty =Faculty(id=null,name=name)
        withContext(Dispatchers.IO){
            universityDao.insertNewFaculty(faculty)
            university.postValue(universityDao.loadUniversity())
        }
    }

    suspend fun deleteFaculty(faculty: Faculty) {
        withContext(Dispatchers.IO) {
            universityDao.deleteFaculty(faculty)
            university.postValue(universityDao.loadUniversity())
        }
    }

    suspend fun loadFaculty (){
        withContext(Dispatchers.IO){
            university.postValue(universityDao.loadUniversity())
        }
    }
    suspend fun getFacultyGroups (facultyID: Long){
        withContext(Dispatchers.IO){
            faculty.postValue(universityDao.loadFacultyGroup(facultyID))
        }
    }
    suspend fun getfaculty(facultyID: Long): Faculty?{
        var f : Faculty?=null
        val job= CoroutineScope(Dispatchers.IO).launch {
            f=universityDao.getFaculty(facultyID)
        }
        job.join()
        return f
    }

    suspend fun getGroupStudents(groupID: Long) /*:List<Student> */{
        withContext(Dispatchers.IO){
            group.postValue(universityDao.loadGroupStudents(groupID))
        }

    }

    suspend fun newGroup(facultyID: Long, name: String) {
        val group = Group(id=null,name=name,facultyID=facultyID)
        withContext(Dispatchers.IO) {
            universityDao.insertNewGroup(group)
            getFacultyGroups(facultyID)
        }
    }

    suspend fun newStudent(student: Student, groupID: Long) {
        withContext(Dispatchers.IO) {
            universityDao.insertNewStudent(student)
            getGroupStudents(student.groupID!!)
        }
    }

    suspend fun getGroup(groupID: Long): Group? {
        var f : Group?=null
        val job= CoroutineScope(Dispatchers.IO).launch {
            f=universityDao.getGroup(groupID)
        }
        job.join()
        return f
    }

    suspend fun editStudent(student: Student) {
        withContext(Dispatchers.IO) {
            universityDao.updateStudent(student)
            getGroupStudents(student.groupID!!)
        }
    }

    suspend fun deleteStudent(student: Student) {
        withContext(Dispatchers.IO) {
            universityDao.deleteStudent(student)
            getGroupStudents(student.groupID!!)
        }
    }

    suspend fun editFaculty(s: String, faculty: Faculty) {
        withContext(Dispatchers.IO) {
            faculty.name = s
            universityDao.updateFaculty(faculty)
            university.postValue(universityDao.loadUniversity())
        }
    }

    suspend fun editGroup(facultyID: Long, s: String, group: Group) {
        withContext(Dispatchers.IO) {
            group.name = s
            universityDao.updateGroup(group)
            university.postValue(universityDao.loadUniversity())
            getFacultyGroups(facultyID)
        }
    }

    suspend fun deleteGroup(facultyID: Long, group: Group) {
        withContext(Dispatchers.IO) {
            universityDao.deleteGroup(group)
            getFacultyGroups(facultyID)
        }
    }




}