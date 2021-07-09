package helloktor.plugins

import helloktor.database.Task
import helloktor.database.tasks
import helloktor.models.TaskEditDto
import helloktor.models.asDto
import helloktor.plugins.validation.validate
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.dsl.eq
import org.ktorm.entity.*

fun Application.configureRouting() {

    routing {
        get("/") {
            val db = SqliteDatabase.connect()
            val results = db
                .tasks
                .sortedByDescending { it.completed }

            call.respond(results.asDto)
        }

        post("/") {
            val request = call.receive<TaskEditDto>()

            val validated = request.validate()

            if (validated.isValid) {
                val db = SqliteDatabase.connect()
                val task = Task {
                    name = request.name
                    completed = request.completed
                }

                db.tasks.add(task)
                call.respond(HttpStatusCode.Created, task.asDto)

            } else {
                call.respond(HttpStatusCode.BadRequest, validated)
            }
        }

        put("/{id}") {
            val param = call.parameters["id"]
            val request = call.receive<TaskEditDto>()
            val db = SqliteDatabase.connect()

            val validated = request.validate()

            if (validated.isValid) {
                param?.let {
                    val id = it.toInt()
                    val task = db.tasks.find { t -> t.id eq id }

                    if (task != null) {
                        task.name = request.name
                        task.completed = request.completed
                        task.flushChanges()

                        call.respond(HttpStatusCode.OK, task.asDto)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, validated)
            }
        }

        delete("/{id}") {
            val param = call.parameters["id"]

            param?.let {
                val id = it.toInt()
                val db = SqliteDatabase.connect()
                db.tasks.removeIf { t -> t.id eq id }
            }

            call.respond(HttpStatusCode.Accepted)
        }
    }
}
