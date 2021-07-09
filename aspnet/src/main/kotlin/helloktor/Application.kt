package helloktor

import helloktor.plugins.SqliteDatabase
import helloktor.plugins.configureRouting
import helloktor.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {

    // initialize the sqlite database
    // with database schema
    // comment out after first initialization
    SqliteDatabase.initialize()

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
    }.start(wait = true)
}
