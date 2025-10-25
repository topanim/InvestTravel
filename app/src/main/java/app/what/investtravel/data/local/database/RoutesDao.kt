package app.what.investtravel.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import app.what.investtravel.data.local.entity.RouteEntity
import app.what.investtravel.data.local.entity.RouteWithPoints


@Dao
interface RoutesDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(teacher: RouteEntity)

    @Transaction
    @Query("SELECT * FROM routes WHERE routes.id = :id")
    suspend fun selectRouteById(id: Int): RouteWithPoints
}

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insert(teacher: List<TeacherDBO>)

//    @Update
//    suspend fun update(teacher: TeacherDBO)
//
//    @Query("SELECT * FROM teachers WHERE teachers.institutionId = :institutionId")
//    suspend fun selectByInstitution(institutionId: String): List<TeacherDBO>
//
//    @Query("SELECT * FROM teachers WHERE teachers.id = :id")
//    suspend fun selectById(id: Long): TeacherDBO
//
//    @Query("SELECT * FROM teachers WHERE teachers.institutionId = :institutionId AND teachers.teacherId = :id")
//    suspend fun selectByTeacherId(institutionId: String, id: String): TeacherDBO
//
//    @Query("SELECT teachers.id FROM teachers WHERE teachers.institutionId = :institutionId AND teachers.teacherId = :id")
//    suspend fun selectIdByTeacherId(institutionId: String, id: String): Long?
//
//    @Query("SELECT * FROM teachers WHERE teachers.name = :name")
//    suspend fun selectByName(name: String): TeacherDBO
//
//    @Delete
//    suspend fun delete(teacher: TeacherDBO)
//}