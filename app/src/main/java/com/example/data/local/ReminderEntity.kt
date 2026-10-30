package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val time: String, // e.g. "07:30 AM" or "14:00"
    val repeatDays: String = "Every day", // "Every day", "Mon–Fri", "Once"
    val isEnabled: Boolean = true,
    val isAlarm: Boolean = true, // true = Smart Alarm, false = Reminder
    val challengeType: String = "GENTLE", // "GENTLE", "MATH", "SHAKE"
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE isAlarm = 1 ORDER BY id DESC")
    fun getAllAlarms(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isAlarm = 0 ORDER BY id DESC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders ORDER BY id DESC")
    fun getAllItems(): Flow<List<ReminderEntity>>

    @Query("SELECT COUNT(*) FROM reminders WHERE isEnabled = 1")
    fun getActiveRemindersCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM reminders")
    suspend fun clearAll()
}
