package app.what.investtravel.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import app.what.investtravel.data.local.entity.RouteEntity
import app.what.investtravel.data.local.entity.RoutePointEntity

@Dao
interface PointsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(teacher:List<RoutePointEntity>): Long
}
