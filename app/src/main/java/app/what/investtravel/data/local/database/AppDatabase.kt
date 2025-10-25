package app.what.investtravel.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.what.investtravel.data.local.entity.RouteEntity
import app.what.investtravel.data.local.entity.RoutePointEntity
import app.what.investtravel.data.local.entity.RouteWithPoints

@Database(
    entities = [RouteEntity::class, RoutePointEntity::class, RouteWithPoints::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){

abstract fun routesDao(): RoutesDAO
abstract fun routePointsDao(): PointsDao
}

