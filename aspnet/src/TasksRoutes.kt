import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

data class Task(val id:Int, val text: String, val complete: Boolean = false, val link: String = "/tasks/${id}")
data class Edit(val text: String, val complete: Boolean = false)
data class Index(val results: List<Task>)

// in-memory tasks
val tasks = mutableListOf(
    Task(1, "pick up groceries"),
    Task(2, "walk the dog"),
    Task(3, "cook dinner")
)

fun Application.tasksRoutes() {
    routing {
        route("/") {
            get {
                call.respond(Index(tasks))
            }
        }
        route("/tasks") {
            // tasks#index
            // tasks#create
            post {
                val request = call.receive<Edit>()

                if (request.text.isEmpty()) {
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

                if (input.text.isEmpty()) {
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

