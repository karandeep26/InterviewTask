package com.interviewtask.worker

interface Task<T> {
    fun onExecuteTask(): T
    fun onTaskComplete(result: T)
}