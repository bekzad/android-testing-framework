package com.example.android.architecture.blueprints.todoapp.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.runBlocking
import java.lang.Error
import java.lang.Exception

class FakeTasksRepository : TasksRepository {

    private var shouldReturnError = false
    var tasksServiceData: LinkedHashMap<String, Task> = LinkedHashMap()
    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        if (shouldReturnError) {
            return Result.Error(Exception("Some error has happened"))
        }
        return Result.Success(tasksServiceData.values.toList())
    }

    override suspend fun refreshTasks() {
//        CoroutineScope(Dispatchers.Main).launch {
        observableTasks.postValue(getTasks())
//        }
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        runBlocking { refreshTasks() }
        return observableTasks
    }

    override suspend fun refreshTask(taskId: String) {
        refreshTasks()
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        runBlocking { refreshTasks() }
        return observableTasks.map { tasks ->
            when (tasks) {
                is Result.Loading -> Result.Loading
                is Result.Error -> Result.Error(tasks.exception)
                is Result.Success -> Result.Success(tasks.data.firstOrNull { it.id == taskId }
                    ?: return@map Result.Error(Exception("Not found")))
            }
        }
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        if (shouldReturnError) {
            return Result.Error(Exception("Some error has happened"))
        }
        val task = tasksServiceData[taskId]
        return task?.let { Result.Success(it) } ?: Result.Error(Exception("Not Found"))
    }

    override suspend fun saveTask(task: Task) {
        tasksServiceData[task.id] = task
        refreshTasks()
    }

    override suspend fun completeTask(task: Task) {
        completeTask(task.id)
    }

    override suspend fun completeTask(taskId: String) {
        tasksServiceData[taskId]?.isCompleted = true
        refreshTasks()
    }

    override suspend fun activateTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearCompletedTasks() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllTasks() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskId: String) {
        TODO("Not yet implemented")
    }

    fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            tasksServiceData[task.id] = task
        }
        runBlocking { refreshTasks() }
    }
}