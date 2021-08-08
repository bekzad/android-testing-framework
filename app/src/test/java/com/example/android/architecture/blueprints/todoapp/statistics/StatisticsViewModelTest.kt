package com.example.android.architecture.blueprints.todoapp.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.source.FakeTasksRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class StatisticsViewModelTest {

    // Executes each task synchronously using Architecture Components, since we are testing them
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Swaps Dispatcher.Main for a TestCoroutineDispatcher
    // TestCoroutineDispatcher executes coroutines deterministically an immediately
    // It doesn't execute asynchronously
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeTasksRepository: FakeTasksRepository
    private lateinit var statisticsViewModel: StatisticsViewModel

    @Before
    fun setupViewModel() {
        // Set up repository
        fakeTasksRepository = FakeTasksRepository()

        // Given a fresh StatisticsViewModel
        statisticsViewModel = StatisticsViewModel(fakeTasksRepository)
    }

    @Test
    fun loadTasks_loading() {
        // Because TestCoroutineDispatcher executes this immediately it will finish completely until
        // it goes to the next line
        // PauseDispatcher pauses before executing the coroutine
        mainCoroutineRule.pauseDispatcher()
        // Load the task in the view model
        statisticsViewModel.refresh()

        // Then progress indicator is shown
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(true))
        // Resumes the paused coroutine
        mainCoroutineRule.resumeDispatcher()

        // This won't continue until the refresh function finishes
        // Then progress indicator is hidden
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadStatisticsWhenTasksAreUnavailable_callErrorToDisplay() = mainCoroutineRule.runBlockingTest {

        fakeTasksRepository.setReturnError(true)

        // When repository returns an error
        fakeTasksRepository.getTasks(true)
        statisticsViewModel.refresh()

        assertThat(statisticsViewModel.empty.getOrAwaitValue(), `is`(true))
        assertThat(statisticsViewModel.error.getOrAwaitValue(), `is`(true))

    }



}