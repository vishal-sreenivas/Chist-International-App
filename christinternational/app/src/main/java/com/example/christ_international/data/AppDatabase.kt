package com.example.christ_international.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Student::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "christ_international_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                prepopulateDatabase(database.studentDao())
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulateDatabase(studentDao: StudentDao) {
            val students = listOf(
                Student(
                    regNumber = "2434527",
                    name = "GAYATRI BALKRISHNA MENON",
                    email = "gayatri.menon@bscpsyh.christuniversity.in",
                    className = "1BScPSY A BGR",
                    program = "Bachelor of Computer Applications",
                    level = "Under Graduate Degree",
                    campus = "BANGALORE BANNERGHATTA ROAD CAMPUS",
                    nationality = "United Kingdom",
                    category = "OCI"
                ),
                Student(
                    regNumber = "2434544",
                    name = "NISHITA HITEN BHATIA",
                    email = "nishita.bhatia@bscpsyh.christuniversity.in",
                    className = "1BScPSY A BGR",
                    program = "Bachelor of Computer Applications",
                    level = "Under Graduate Degree",
                    campus = "BANGALORE BANNERGHATTA ROAD CAMPUS",
                    nationality = "United Kingdom",
                    category = "OCI"
                ),
                // Add all other students here...
                Student(
                    regNumber = "2448557",
                    name = "VIVEK GEORGE STEPHEN",
                    email = "vivek.stephen@msam.christuniversity.in",
                    className = "1MSAIM",
                    program = "BSc (Computer Science, Statistics) - Bangalore Central Campus",
                    level = "Post Graduate Degree",
                    campus = "BANGALORE CENTRAL CAMPUS",
                    nationality = "United States of America",
                    category = "International Students"
                )
            )
            studentDao.insertAll(students)
        }
    }
}
