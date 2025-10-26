package app.what.investtravel.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.what.investtravel.data.local.entity.RouteEntity
import app.what.investtravel.data.local.entity.RoutePointEntity

@Dao
interface PointsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(teacher:List<RoutePointEntity>)
    
    @Query("DELETE FROM route_points WHERE routeId = :routeId")
    suspend fun deleteByRouteId(routeId: Int)

    @Query("UPDATE route_points SET checked = :checked WHERE localId = :pointId")
    suspend fun updateChecked(pointId: Int, checked: Boolean)
}
