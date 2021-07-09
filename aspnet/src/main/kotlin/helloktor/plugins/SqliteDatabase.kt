package helloktor.plugins

import org.ktorm.database.Database
import org.ktorm.logging.Slf4jLoggerAdapter
import org.ktorm.support.sqlite.SQLiteDialect
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// SqliteDatabase modified from
// https://github.com/tomconder/ktorm-sqlite/
class SqliteDatabase {

    companion object Factory {
        private val logger: Logger by lazy { LoggerFactory.getLogger("Sqlite") }
        private const val sqlScript = "setup.sql"

        fun connect(): Database {
            return Database.connect(
                url = "jdbc:sqlite:sample.db",
                dialect = SQLiteDialect(),
                logger = Slf4jLoggerAdapter(logger)
            )
        }

        fun initialize() {
            val db = connect()

            db.useConnection { connection ->
                connection.createStatement().use { statement ->
                    SqliteDatabase::class.java.classLoader
                        ?.getResourceAsStream(sqlScript)
                        ?.bufferedReader()
                        ?.use { reader ->
                            for (sql in reader.readText().split(';')) {
                                if (sql.any { it.isLetterOrDigit() }) {
                                    statement.executeUpdate(sql)
                                }
                            }
                        }
                }
            }

        }
    }

}