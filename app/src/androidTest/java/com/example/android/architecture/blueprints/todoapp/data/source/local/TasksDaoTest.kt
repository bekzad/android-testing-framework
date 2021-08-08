package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TasksDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ToDoDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertTaskAndGetById() = runBlockingTest {
        // GIVEN - Insert a task.
        val task = Task("Title", "Description",
            false, "id1")

        database.taskDao().insertTask(task)

        // WHEN - Get the task by id from the database.
        val returnTask = database.taskDao().getTaskById(task.id)

        // THEN - The loaded data contains the expected values.
        assertThat<Task>(returnTask as Task, notNullValue())
        assertThat(returnTask.id, `is`(task.id))
        assertThat(returnTask.title, `is`(task.title))
        assertThat(returnTask.description, `is`(task.description))
        assertThat(returnTask.isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun updateTaskAndGetById() = runBlockingTest {
        // 1. Insert a task into the DAO.
        val task = Task("Title", "Description",
            false, "id1")

        database.taskDao().insertTask(task)

        // 2. Update the task by creating a new task with the same
        // ID but different attributes.
        val task2 = Task("Title2", "Description2",
            false, "id1")
        database.taskDao().updateTask(task2)


        // 3. Check that when you get the task by its ID, it has the updated values.
        val returnTask = database.taskDao().getTaskById(task.id)
        assertThat<Task>(returnTask as Task, notNullValue())
        assertThat(returnTask.id, `is`(task2.id))
        assertThat(returnTask.title, `is`(task2.title))
        assertThat(returnTask.description, `is`(task2.description))
        assertThat(returnTask.isCompleted, `is`(task2.isCompleted))
    }


}