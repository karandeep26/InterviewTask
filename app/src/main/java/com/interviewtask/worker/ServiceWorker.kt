package com.interviewtask.worker

import android.os.Handler
import android.os.Looper
import androidx.annotation.WorkerThread
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ServiceWorker(private val tag: String) {
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

    fun <T> addTask(task: Task<T>) {
        executor.execute {
            val result = task.onExecuteTask()
            Handler(Looper.getMainLooper()).post {
                task.onTaskComplete(result)
            }
        }
    }

    fun shutDownTasks() {
        executor.shutdownNow()
    }
}
