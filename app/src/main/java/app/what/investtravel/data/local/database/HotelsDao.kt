package app.what.investtravel.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.what.investtravel.data.local.entity.HotelEntity

@Dao
interface HotelsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(teacher: List<HotelEntity>)

    @Query("SELECT * FROM hotels")
    suspend fun selectAll(): List<HotelEntity>
}