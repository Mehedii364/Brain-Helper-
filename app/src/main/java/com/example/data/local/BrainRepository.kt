package com.example.data.local

import kotlinx.coroutines.flow.Flow

class BrainRepository(private val db: AppDatabase) {

    // Tasks
    val allTasks: Flow<List<TaskEntity>> = db.taskDao().getAllTasks()
    val todayTasks: Flow<List<TaskEntity>> = db.taskDao().getTodayTasks()
    val totalTasksCount: Flow<Int> = db.taskDao().getTotalTasksCount()
    val completedTasksCount: Flow<Int> = db.taskDao().getCompletedTasksCount()

    suspend fun insertTask(task: TaskEntity): Long = db.taskDao().insertTask(task)
    suspend fun updateTask(task: TaskEntity) = db.taskDao().updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = db.taskDao().deleteTask(task)
    suspend fun deleteTaskById(id: Int) = db.taskDao().deleteById(id)

    // Reminders & Alarms
    val allAlarms: Flow<List<ReminderEntity>> = db.reminderDao().getAllAlarms()
    val allReminders: Flow<List<ReminderEntity>> = db.reminderDao().getAllReminders()
    val allReminderItems: Flow<List<ReminderEntity>> = db.reminderDao().getAllItems()
    val activeRemindersCount: Flow<Int> = db.reminderDao().getActiveRemindersCount()

    suspend fun insertReminder(reminder: ReminderEntity): Long = db.reminderDao().insertReminder(reminder)
    suspend fun updateReminder(reminder: ReminderEntity) = db.reminderDao().updateReminder(reminder)
    suspend fun deleteReminder(reminder: ReminderEntity) = db.reminderDao().deleteReminder(reminder)
    suspend fun deleteReminderById(id: Int) = db.reminderDao().deleteById(id)

    // Memories
    val allMemories: Flow<List<MemoryEntity>> = db.memoryDao().getAllMemories()
    fun getMemoriesByCategory(category: String): Flow<List<MemoryEntity>> = db.memoryDao().getMemoriesByCategory(category)
    fun searchMemories(query: String): Flow<List<MemoryEntity>> = db.memoryDao().searchMemories(query)

    suspend fun insertMemory(memory: MemoryEntity): Long = db.memoryDao().insertMemory(memory)
    suspend fun updateMemory(memory: MemoryEntity) = db.memoryDao().updateMemory(memory)
    suspend fun deleteMemory(memory: MemoryEntity) = db.memoryDao().deleteMemory(memory)
    suspend fun deleteMemoryById(id: Int) = db.memoryDao().deleteById(id)

    // Chat
    val allChatMessages: Flow<List<ChatMessageEntity>> = db.chatDao().getAllMessages()
    suspend fun insertChatMessage(message: ChatMessageEntity): Long = db.chatDao().insertMessage(message)
    suspend fun updateChatMessage(message: ChatMessageEntity) = db.chatDao().updateMessage(message)
    suspend fun clearChatHistory() = db.chatDao().clearHistory()

    suspend fun clearAllData() {
        db.taskDao().clearAll()
        db.reminderDao().clearAll()
        db.memoryDao().clearAll()
        db.chatDao().clearHistory()
    }
}
