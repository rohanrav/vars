package com.example.import_task_automation

import com.example.models.Task
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.td
import it.skrape.selects.html5.tr
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Scrape(website_url: String, date_index: Int, assignment_index: Int): Deadlines {
    val deadlines = skrape(HttpFetcher) {
        request {
            url = website_url
        }

        extractIt<Deadlines> {
            htmlDocument {
                val deadlines: MutableList<Deadline> = mutableListOf()
                var count = 0

                val table = findFirst("table") {
                    tr {
                        findAll{this}
                    }
                }

                table
                    .drop(1)
                    .map {
                        var dates = ""
                        var assignment = ""

                        it.td {
                            findAll() {
                                try {
                                    dates = this[date_index].text
                                    val pattern1 = Regex("(\\w{3,4}) (\\d+)-(\\d+)")
                                    val pattern2 = Regex("(\\d{4}-\\d{2}-\\d{2} \\d{1,2}:\\d{2}[apm]+)")
                                    val pattern3 = Regex("\\b(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Sept|Oct|Nov|Dec" +
                                            "|Jan.|Feb.|Mar.|Apr.|May.|Jun.|Jul.|Aug.|Sep.|Sept.|Oct.|Nov.|Dec.)\\s*\\d+\\b")

                                    val matchResult1 = pattern1.find(dates)
                                    if (matchResult1 != null) {
                                        val month = matchResult1.groupValues[1]
                                        val lastNumber = matchResult1.groupValues[3]
                                        dates = "${month.substring(0, 3)} $lastNumber"
                                    }
                                    val matchResult2 = pattern2.find(dates)
                                    if (matchResult2 != null) {
                                        val formattedDate = SimpleDateFormat("MMM d", Locale.US).format(
                                            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(matchResult2.groupValues[1])
                                        )
                                        dates = formattedDate
                                    }
                                    val matchResult3 = pattern3.find(dates)
                                    if (matchResult3 != null) {
                                        dates = matchResult3.value
                                    }
                                } catch (_: Exception) {}
                            }
                        }

                        it.td {
                            findAll() {
                                try {
                                    assignment = this[assignment_index].text
                                } catch (_: Exception) {}
                            }
                        }

                        if (assignment != "") {
                            val assignmentArray = assignment.split(",")
                            for (individualAssignment in assignmentArray) {
                                val deadline = Deadline(dates, individualAssignment.trim())
                                deadlines.add(deadline)
                                count += 1
                            }
                        }
                    }

                it.deadlines = deadlines
                it.count = count
            }
        }
    }
    return deadlines
}
fun DeadlineToTask(deadline: Deadline, courseName: String): Task {
    return Task(
        taskName = deadline.assignment,
        dueDate = dateFromString(deadline.dates),
        course = courseName,
        weight = 0.0
    )
}

fun dateFromString(dateString: String): Date {
    val format = SimpleDateFormat("MMM dd yyyy", Locale.US)
    val formattedDateString = dateString.replace(Regex("Sept"), "Sep") + " 2023"
    return format.parse(formattedDateString) ?: throw ParseException("Invalid date format", 0)
}

