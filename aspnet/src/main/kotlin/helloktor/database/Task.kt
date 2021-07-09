package helloktor.database

import org.ktorm.database.Database
import org.ktorm.dsl.isNotNull
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.varchar

val Database.tasks get() = this.sequenceOf(Tasks)

object Tasks : Table<Task>("tasks") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }.isNotNull()
    val completed = boolean("completed").bindTo { it.completed }.isNotNull()
}

interface Task : Entity<Task> {
    companion object : Entity.Factory<Task>()

    var id: Int
    var name: String
    var completed: Boolean
}