package com.cr4sh.nhlauncher.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ToolEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun toolDao(): ToolsDao

    private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    // Populate the database with default items here
                    populateDatabase(database.toolDao())
                }
            }
        }

        private suspend fun populateDatabase(toolDao: ToolsDao) {
            // Insert default items here
            toolDao.insertTool(ToolEntity(system = 1, category = "1", favourite = 0, name = "p0f", descriptionEN = "Identify remote systems passively", descriptionPL = "Pasywna identyfikacja systemów zdalnych", cmd = "p0f -h", icon = "kali_p0f", usage = 0))
            toolDao.insertTool(ToolEntity(system = 1, category = "13", favourite = 0, name = "DNSTwist", descriptionEN = "Permutation Engine for detecting homograph Phishing Attacks", descriptionPL = "Silnik permutacji do wykrywania ataków phishingowych na podstawie homografów", cmd = "dnstwist -h", icon = "antiphishing", usage = 0))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nhlauncherdb"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
