package com.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        get("/") {
            call.respondRedirect("/tasks", false)
        }
    }

    // tasks routes
    tasksModule()
}

fun Application.tasksModule() {

    // in-memory tasks
    val tasks = mutableListOf(
        Task(1, "pick up groceries"),
        Task(2, "walk the dog"),
        Task(3, "cook dinner")
    )

    routing {
        route("/tasks") {
            // tasks#index
            get {
                call.respond(Index(tasks))
            }
            // tasks#create
            post {
                val request = call.receive<Edit>()

                if (request.text.isNullOrEmpty()) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    val id = tasks.maxOf { it.id } + 1
                    val task = Task(id, request.text, request.complete)
                    tasks.add(task)
                    call.respond(HttpStatusCode.Created, task)
                }
            }
            // tasks#get
            get("{id}") {
                val id = call.parameters["id"]?.toInt()
                val task = tasks.find { it.id == id }

                if (task == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(task)
                }
            }
            // tasks#update
            put("{id}") {
                val id = call.parameters["id"]?.toInt()
                val input = call.receive<Edit>()

                if (input.text.isNullOrEmpty()) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    val task = tasks.find { it.id == id }
                    if (task == null) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        val index = tasks.indexOf(task)
                        val result = Task(task.id, input.text, input.complete)
                        tasks[index] = result
                        call.respond(result)
                    }
                }
            }
            // tasks#delete
            delete("{id}") {
                val id = call.parameters["id"]?.toInt()
                tasks.removeIf { it.id == id }

                // success no content
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }

}

data class Task(val id:Int, val text: String, val complete: Boolean = false, val link: String = "/tasks/${id}")
data class Edit(val text: String, val complete: Boolean = false)
data class Index(val results: List<Task>)


