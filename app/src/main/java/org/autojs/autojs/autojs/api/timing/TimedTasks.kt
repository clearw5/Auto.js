package org.autojs.autojs.autojs.api.timing

import com.stardust.autojs.execution.ExecutionConfig
import org.autojs.autojs.timing.TimedTask
import org.autojs.autojs.timing.TimedTaskManager
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime

object TimedTasks {

    fun daily(path: String, hour: Int, minute: Int) {
        TimedTaskManager.getInstance().addTask(TimedTask.dailyTask(LocalTime(hour, minute), path, ExecutionConfig()))
    }

    fun disposable(path: String, millis: Long) {
        TimedTaskManager.getInstance().addTask(TimedTask.disposableTask(LocalDateTime(millis), path, ExecutionConfig()))
    }

    fun weekly(path: String, millis: Long) {
        //TimedTaskManager.getInstance().addTask(TimedTask.weeklyTask(LocalDateTime(millis), path, ExecutionConfig()))
    }

}