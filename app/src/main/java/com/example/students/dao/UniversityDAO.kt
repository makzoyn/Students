package com.example.students.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.students.api.FacultyWithGroups
import com.example.students.data.Faculty
import com.example.students.data.Group
import com.example.students.data.Student


@Dao
interface UniversityDAO {




    @Insert(entity = Faculty::class,/*onConflict = OnConflictStrategy.REPLACE*/)
    fun insertNewFaculty(faculty: Faculty)

    @Query("DELETE FROM university WHERE id = :facultyID")
    fun deleteFacultyByID(facultyID: Long)

    @Delete(entity = Faculty::class)
    fun deleteFaculty (faculty: Faculty)

    @Query("SELECT id, faculty_name FROM university order by faculty_name")
    fun loadUniversity(): List<Faculty>

    @Update(entity= Faculty::class)
    fun updateFaculty(faculty: Faculty)

    @Query("SELECT id, faculty_name FROM university where id=:id")
    fun getFaculty(id: Long): Faculty

    @Query("SELECT * FROM faculty where faculty_id=:facultyID order by group_name")
    fun loadFacultyGroup(facultyID: Long): List<Group>

    @Insert(entity = Group::class,/*onConflict = OnConflictStrategy.REPLACE*/)
    fun insertNewGroup(group: Group)

    @Delete(entity = Group::class)
    fun deleteGroup(group: Group)

    @Query("SELECT * FROM faculty order by group_name")
    fun loadGroup(): List<Group>

    @Update(entity = Group::class)
    fun updateGroup(group: Group)

    @Query("SELECT id, group_name FROM faculty where id=:id")
    fun getGroup(id: Long): Group?

    @Insert(entity = Student::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertNewStudent(student: Student)

    @Delete(entity = Student::class)
    fun deleteStudent(student: Student)

    @Query("SELECT * FROM student order by last_name")
    fun loadStudent(): List<Student>

    @Update(entity = Student::class)
    fun updateStudent(student: Student)

    @Query("SELECT * FROM student where id=:id")
    fun getStudent(id: Long): Student?

    @Query("SELECT * FROM student where group_id=:groupID order by last_name")
    fun loadGroupStudents(groupID : Long): List<Student>

    @Query("DELETE FROM university")
    fun deleteAllFaculty()

//    @Transaction
//    @Query("SELECT id, faculty_name, group.id AS group_id, group.name AS group_name, student.id AS student_id, student.first_name, student.last_name, student.middle_name, student.phone, student.birth_date, student.group_id AS student_group_id FROM university " +
//            "JOIN faculty ON faculty.id = university.faculty_id " +
//            "JOIN `group` ON `group`.faculty_id = faculty.id " +
//            "JOIN student ON student.group_id = `group`.id")
//    suspend fun getFacultiesWithGroups(): List<FacultyWithGroups>

//    @Transaction
//    @Query("SELECT * FROM faculty where faculty_id=:facultyID")
//    suspend fun getGroupsWithStudentsByFacultyId(facultyId: Long): List<GroupWithStudents>

}