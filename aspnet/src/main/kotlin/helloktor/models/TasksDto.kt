package helloktor.models

import helloktor.database.Task
import helloktor.database.Tasks
import kotlinx.serialization.Serializable
import org.ktorm.entity.EntitySequence
import org.ktorm.entity.map

@Serializable
data class TasksDto(
    val totalRecords: Int,
    val results: List<TaskDto>
)

val EntitySequence<Task, Tasks>.asDto get() = TasksDto(this.totalRecords, this.map { it.asDto })

@Serializable
data class TaskDto(
    val id: Int,
    val name: String,
    val completed: Boolean
)

val Task.asDto get() = TaskDto(this.id, this.name, this.completed)

@Serializable
data class TaskEditDto(
    val name: String,
    val completed: Boolean
) {
    fun isValid() = !name.isNullOrEmpty()
}