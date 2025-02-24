package com.example.import_task_automation

data class Deadline(var dates: String="", var assignment: String="")
data class Deadlines(var deadlines: MutableList<Deadline> = mutableListOf(), var count:Int = 0)