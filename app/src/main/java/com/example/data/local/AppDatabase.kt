package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        ReminderEntity::class,
        MemoryEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun reminderDao(): ReminderDao
    abstract fun memoryDao(): MemoryDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "brain_helper_db"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial real items so dashboard starts alive
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            seedInitialData(database)
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(db: AppDatabase) {
            // Seed initial sample tasks
            db.taskDao().insertTask(
                TaskEntity(
                    title = "Review quarterly product roadmap",
                    notes = "Check milestones and team updates",
                    priority = "HIGH",
                    dueDate = "Today",
                    dueTime = "10:30 AM",
                    isCompleted = true,
                    category = "Work"
                )
            )
            db.taskDao().insertTask(
                TaskEntity(
                    title = "আজকের প্রধান কাজগুলো সম্পন্ন করা",
                    notes = "বাংলা ডকুমেন্টস ও প্ল্যানিং সম্পূর্ণ করা",
                    priority = "HIGH",
                    dueDate = "Today",
                    dueTime = "02:00 PM",
                    isCompleted = true,
                    category = "Work"
                )
            )
            db.taskDao().insertTask(
                TaskEntity(
                    title = "Review Brain Helper obsidian design notes",
                    notes = "Ensure dark theme and contrast ratios are calibrated",
                    priority = "MEDIUM",
                    dueDate = "Today",
                    dueTime = "04:30 PM",
                    isCompleted = false,
                    category = "Design"
                )
            )
            db.taskDao().insertTask(
                TaskEntity(
                    title = "Evening mindfulness & daily reflection",
                    notes = "15 minutes of calm meditation",
                    priority = "LOW",
                    dueDate = "Today",
                    dueTime = "09:00 PM",
                    isCompleted = false,
                    category = "Health"
                )
            )

            // Seed initial reminders / alarms
            db.reminderDao().insertReminder(
                ReminderEntity(
                    title = "Morning Deep Focus Alarm",
                    time = "07:00 AM",
                    repeatDays = "Mon–Fri",
                    isEnabled = true,
                    isAlarm = true,
                    challengeType = "MATH"
                )
            )
            db.reminderDao().insertReminder(
                ReminderEntity(
                    title = "Hydration & posture check / পানি পান",
                    time = "02:30 PM",
                    repeatDays = "Every day",
                    isEnabled = true,
                    isAlarm = false,
                    challengeType = "GENTLE"
                )
            )

            // Seed initial memory note
            db.memoryDao().insertMemory(
                MemoryEntity(
                    title = "Brain Helper Philosophy",
                    content = "Think clearly. Live smarter. Focus on one intentional step at a time with calm clarity.",
                    category = "IDEAS",
                    isPinned = true
                )
            )

            // Seed initial greeting message
            db.chatDao().insertMessage(
                ChatMessageEntity(
                    role = "assistant",
                    text = "Welcome to Brain Helper! I am your personal assistant. You can speak to me in Bengali (বাংলা) or English to manage tasks, set reminders, scan documents, or think through problems.",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
