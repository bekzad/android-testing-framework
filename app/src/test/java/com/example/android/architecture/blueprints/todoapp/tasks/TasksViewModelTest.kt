package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeTasksRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TasksViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Swaps Dispatcher.Main for a TestCoroutineDispatcher
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
//    private val testDispatcher = TestCoroutineDispatcher()
//
//    @Before
//    fun setUpDispatcher() {
//        Dispatchers.setMain(testDispatcher)
//    }
//
//    @After
//    fun tearDownDispatcher() {
//        Dispatchers.resetMain()
//        testDispatcher.cleanupTestCoroutines()
//    }

    private lateinit var fakeTasksRepository: FakeTasksRepository
    private lateinit var tasksViewModel: TasksViewModel

    // This is used to set up the test codes in every function here
    @Before
    fun setupViewModel() {
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)

        fakeTasksRepository = FakeTasksRepository()
        fakeTasksRepository.addTasks(task1, task2, task3)

        // Given a fresh TaskViewModel
        tasksViewModel = TasksViewModel(fakeTasksRepository)
    }

    @Test
    fun addNewTask_setsNewTaskEvent() {

        // When adding a new task
        tasksViewModel.addNewTask()

        // Then the new task event is triggered
        // Using getOrAwaitValue because it is an observable liveData and we want to observe it
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()
        assertThat(value.getContentIfNotHandled(), not(nullValue()))

//        // Create an observer - no need to do anything
//        val observer = Observer<Event<Unit>>() {}
//        try {
//            // Observe the livedata forever
//            tasksViewModel.newTaskEvent.observeForever(observer)
//
//            // When adding a new task
//            tasksViewModel.addNewTask()
//
//            // Then the new task event is triggered
//            val valueEvent = tasksViewModel.newTaskEvent.value
//            assertThat(valueEvent?.getContentIfNotHandled(), (not(nullValue())))
//        } finally {
//            // remove the observer
//            tasksViewModel.newTaskEvent.removeObserver(observer)
//        }
    }

    @Test
    fun setFilterAllTasks_tasksAddViewVisible() {

        // When the filter type is ALL_TASKS
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        // Then the "Add task" action is visible
        // Using getOrAwaitValue because it is an observable liveData
        val value = tasksViewModel.tasksAddViewVisible.getOrAwaitValue()
        assertThat(value, `is`(true))

    }

    @Test
    fun completeTask_dataAndSnackbarUpdated() {
        // Create an active task and add it to the repository.
        val task = Task("Title", "Description")
        fakeTasksRepository.addTasks(task)

        // Mark the task as complete task.
        tasksViewModel.completeTask(task, true)

        // Verify the task is completed.
        assertThat(fakeTasksRepository.tasksServiceData[task.id]?.isCompleted, `is`(true))

        // Assert that the snackbar has been updated with the correct text.
        val snackbarText: Event<Int> =  tasksViewModel.snackbarText.getOrAwaitValue()
        assertThat(snackbarText.getContentIfNotHandled(), `is`(R.string.task_marked_complete))
    }

}