package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.BrainAiService
import com.example.data.local.AppDatabase
import com.example.data.local.BrainRepository
import com.example.data.local.TaskEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: BrainRepository
    private lateinit var aiService: BrainAiService

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = BrainRepository(db)
        aiService = BrainAiService()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Brain Helper", appName)
    }

    @Test
    fun `insert and retrieve task in repository`() = runBlocking {
        val task = TaskEntity(
            title = "Plan my day",
            notes = "Finish deep work",
            priority = "HIGH",
            dueDate = "Today",
            dueTime = "10:00 AM",
            category = "Work"
        )
        val id = repository.insertTask(task)
        assertTrue(id > 0)

        val tasks = repository.allTasks.first()
        assertEquals(1, tasks.size)
        assertEquals("Plan my day", tasks[0].title)
        assertEquals("HIGH", tasks[0].priority)
    }

    @Test
    fun `ai service parses action proposals in english and bengali`() = runBlocking {
        val enResult = aiService.processQuery("Add task: Review design system")
        assertNotNull(enResult.proposedAction)
        assertEquals("ADD_TASK:Review design system", enResult.proposedAction)

        val bnResult = aiService.processQuery("কাজ: বাংলা ডকুমেন্ট পর্যালোচনা")
        assertNotNull(bnResult.proposedAction)
        assertEquals("ADD_TASK:বাংলা ডকুমেন্ট পর্যালোচনা", bnResult.proposedAction)
    }
}
