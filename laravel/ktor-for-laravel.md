# Ktor for Laravel Developer

Welcome, artisan. When you first jump into a technical stack that you are not familiar with, it must be uncomfortable. Fortunately, web development is not that different between programing language and framework. If you prefer a lightweight, easy-to-learn framework in [Kotlin](https://kotlinlang.org/) ecosystem, then Ktor will be a perfect match for you.

## Meet Ktor Framework

[Ktor](https://ktor.io/) (pronounced Kay-tor) is a framework built from the ground up using Kotlin and coroutines. It aim to leverage to the maximum extent some of the language features that Kotlin offers, such as DSLs and coroutines. Compare with other ecosystems, it’s a framework like Slim in PHP, Express.js in JavaScript, Sinatra in Ruby. Ktor is developed by JetBrains and used internally. It’s an open source project using Apache 2.0 license.

In this article, we will guide you through the steps to build an RESTful API with SQLite database using Ktor. We will use terminologies in [Laravel](https://laravel.com/) to make you get started quickly.

## Create a Ktor project

To have a development environment for Ktor is quite simply, the only thing you need is [IntelliJ IDEA](https://www.jetbrains.com/idea/). Install JetBrains [Toolbox App](https://www.jetbrains.com/toolbox-app/) and install IntelliJ IDEA first. After IntelliJ IDEA installed, don’t forget to [install JDK](https://www.jetbrains.com/help/idea/sdk.html) (like PHP interpreter) and [Ktor plugin](https://plugins.jetbrains.com/plugin/16008-ktor) for later use.

After the Ktor plugin installed properly, we can now create a new Ktor project using IntelliJ IDEA. Click the Ktor tab in the New Project dialog and fill out the project information. In next step, search and add “`Routing`”, “`ContentNegotiation`” and “`kotlinx.serialization`” features then hit finish. IntelliJ IDEA will create a brand new Ktor project using [Gradle](https://www.jetbrains.com/help/idea/gradle.html) for you automatically. This process is equal when we use [Composer](https://getcomposer.org/) command to create a new project, add dependency in PHP but in a GUI way.

(TODO: gif - crate a new project)

Take a look of the Project tool window in the left hand side. It contains a folder called `src`, that’s the place we write Kotlin code. Open up the `Application.kt` file, as you can see, there is a `main` function inside which is the entry point of Ktor application. Hit the green play button in the gutter, IntelliJ IDEA will trigger Gradle task to compile and run the application. After the application start up. Open browser at `http://localhost:8080/`, you will see `HELLO WORLD!`. This process is similar we type `artisan serve` in the terminal to run application in Laravel.

(TODO: gif - run application)

## Routing

You must be familiar with the notion of routing in Laravel. Route is like the lobby of an application, it determine an HTTP request could pass by path and method. In Ktor, you could define your routing using DSL syntax inside `Application.module()` function in `Application.kt`. This syntax describes how it response to an HTTP request.

We got a hello world sample when creating project. Inside the `routing { }`, we have a `get()` function that accept `"/"` root URL string. The beautiful thing of DSL is, it made route definition in a structured and easy understanding way. As you can expect, we could use `post()`, `put()`, `patch()`, `delete()` methods to accept certain HTTP method. Compare to Laravel, the syntax is equal to `Route::get()`, `Route::post()`, etc.

### The application call

When we deal with HTTP, it typically contains two parts: request and response. In Ktor, it put those in an application call object. Inside our routing DSL, you will receive a `call` object inside the function. We could access the request details by `call.request` and setup our response by `call.respond` related methods. We can see how Ktor response a plain text response in the sample code, just pass a string and setup content type by using builtin classes.

```
call.respondText(
    text = "HELLO WORLD!",
    contentType = ContentType.Text.Plain
)
```

We are going to build a RESTful API, right? How about JSON response?

### Features

Ktor structure itself by using interceptor pattern. Similar the concept of middleware, it means each HTTP request will pass through every interceptor and produce HTTP response. We called these interceptors as Features. Think the features is the abilities that application have. Therefore, when we need our application to handle JSON, we will need to “**install**” a feature called “`ContentNegotiation`”, and a JSON serialization library called “`kotlinx.serialization`”. Ktor provide `install()` function to include a feature. Inside the function, we could customize the feature by passing closure.

```
install(ContentNegotiation) {
    json()
}
```

When we respond a JSON in Laravel, we use arrays to structure our data. In Ktor, we use a similar structure called `Map`. `Map` is a key-value pair and we have `mapOf()` function in Kotlin to help us declare such structure. When we pass a map to Ktor, it will automatically serialize it for us. We could see the sample code in the second routing.

```
get("/json/kotlinx-serialization") {
    call.respond(mapOf("hello" to "world"))
}
```

Open browser at `http://localhost:8080/json/kotlinx-serialization`, you will see the JSON string `{ "hello": "world" }`.

## Integrate with database using Exposed ORM

We are going to build a TODO RESTful API. In order to store all the `Task` information, we need an ORM like Eloquent. Ktor designed to be a slim, light-weight framework, so there is no ORM builtin. Fortunately, there is an ORM framework called Exposed that develop by JetBrains!

### Add dependencies

Before using Exposed, we need to decide which database we are going to use. In this article, we will use SQLite database to store data in a file. Just like we manage our dependencies using `composer.json`. In Ktor, we defined our dependencies using Gradle's  
`build.gradle.kts` file. Open it and add `exposed-core`, `exposed-dao`, `exposed-jdbc` for Exposed, also `sqlite-jdbc` for SQLite driver. Our `dependencies` sections will look like this:

```
val exposed_version: String by project
val sqlite_version: String by project

// ...

dependencies {
    // ...
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.xerial:sqlite-jdbc:$sqlite_version")
    // ...
}
```

### Schema and Entity

In order to interact with database, we need an object to reflect the db schema. Let's declare a schema object like this:

```
object Tasks : IntIdTable("tasks") {
    val title = varchar("title", 255)
    val completed = bool("completed").default(false)
}
```

When using Exposed in DAO flavor, each database table has a corresponding "Entity". Exposed entities allow you to insert, update, and delete records from the table as well. Just like we use "Model" in Eloquent, it simplifies the operation to interact with database. Let's declare an entity class like this:

```
class Task(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Task>(Tasks)

    var title by Tasks.title
    var completed by Tasks.completed
}
```

### Create SQLite database file

We will store our `Task` data in a SQLite database. Open Terminal tool window in IntelliJ IDEA and type this command to create a new SQLite database file in database folder.

```
$ mkdir database
$ touch database/database.sqlite
```

### Connect to database

Before we retrieve data from SQLite, we need to connect to it first. Use the `Database` class to connect database by providing `url` and passing the `driver` to it.

```
Database.connect(
    url = "jdbc:sqlite:./database/database.sqlite",
    driver = "org.sqlite.JDBC"
)

TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
```

### Create tasks table

To make sure our `tasks` table exist before we select data from it, use `SchemaUtil` to create the table when application run. Put these code right after `Database.connect()`.

```
transaction {
    SchemaUtils.create(Tasks)
}
```

## CRUD operations

In order to exchange data between our client and server, we generally define a [DTO](https://en.wikipedia.org/wiki/Data_transfer_object) to represent the data format using data class. Just like we use POPO (Plain Old PHP Object) in PHP, data class is a simple class that carry data in Kotlin. In our sample, the `TaskDto` only need a nullable `id`, the `title` of a task, a boolean `completed` filed to store status. 

Also, we need to put `@Serializable` annotation before data class definition. The kotlinx.serialization gradle plugin will generate a serializer class automatically for us. Your `TaskDto` will look like this now:

```
@Serializable
data class TaskDto(val id: Int?, val title: String, val completed: Boolean = false)
```

### Retrieve a Task list

We can touch the database now. Create a new HTTP GET route. Inside this route, use `Task` entity to retrieve `all()` data from the table. We could sort the data by using `orderBy()` then `map()` the result in to our `TaskDTO` and return a `List<TaskDto>` as record set. When using Exposed, don't forget all the operation need to be place inside a `transcation()`.

```
get("/api/tasks") {
    val tasks = transaction {
        Task.all()
            .orderBy(Tasks.id to SortOrder.DESC)
            .map {
                TaskDto(it.id.value, it.title, it.completed)
            }
    }

    call.respond(mapOf("data" to tasks))
}
```

After we received the task list, put the record set into a Map with a `data` key. The application call will serialize it using kotlinx.serialization library.

### Create a new Task

Before we create a new task, we need to `receive()` a `TaskDto` object from client side. We instlled `ContentNegotiation` feature in our application, so it will deserialize a JSON string into a `TaskDto` automatically for us. To insert a data, simply use `new()` method on `Task` entity by passing column data in the closure. 

```
post("/api/tasks") {
    val taskDto = call.receive<TaskDto>()
    transaction {
        Task.new {
            title = taskDto.title
        }
    }
    call.respond(HttpStatusCode.Created)
}
```

We return HTTP 201 when task created. As you can wee, we could return any kind of `HttpStatusCode` provide by Ktor in a more semantic way.

### Get an existing Task

If you want to retrieve a single task, we could put the unique id on path. Put your path parameter inside curly braces with name. Then retrieve it using array access on `call.parameters`. Convert it to integer, search id in database using `findById()`. Make sure you map the record to DTO before return to client side.

```
get("/api/tasks/{id}") {
    val id = call.parameters["id"]?.toIntOrNull()

    if (id == null) {
        call.respond(HttpStatusCode.BadRequest)
    }

    val task = transaction {
        Task.findById(id!!)?.let {
            TaskDto(it.id.value, it.title, it.completed)
        }
    }

    if (task == null) {
        call.respond(HttpStatusCode.NotFound)
    }

    call.respond(task!!)
}
```

### Update existing Tasks

To update an existing task is similar to create a task. Receive `TaskDto` and find the corresponding entry by its id. Update the data using setter directly on entity. Exposed will update the changes at the end of the transaction.

```
patch("/api/tasks") {
    val dto = call.receive<TaskDto>()

    if (dto.id == null) {
        call.respond(HttpStatusCode.BadRequest)
    }

    transaction {
        val task = Task.findById(dto.id!!)
        task?.title = dto.title
        task?.completed = dto.completed
    }

    call.respond(HttpStatusCode.NoContent)
}
```

You can leverage the safe call syntax `?` to avoid null pointer exception in Kotlin. 

### Delete a Task

To delete a task is straight forward. Just call `delete()` method on the entity. Other part is pretty much the same. 

```
delete("/api/tasks") {
    val dto = call.receive<TaskDto>()

    if (dto.id == null) {
        call.respond(HttpStatusCode.BadRequest)
    }

    transaction {
        val task = Task.findById(dto.id!!)
        task?.delete()
    }

    call.respond(HttpStatusCode.NoContent)
}
```

## Retrospective

In the article, we've walked through an API development process using Ktor framework from the perspective of a developer familiar with Laravel. First we create a new Ktor project using Ktor plugin in IntelliJ IDEA. After that, we use `routing` DSL syntax to define the application route, add `ContentNegotiation` feature to grant the ability to handle JSON serialization/deserialization. Then we add Exposed and SQLite driver as dependencies to connect database and execute CRUD operations. Just simple 4 steps, we can build a RESTful in ease.

As mentioned, there are many similarities between two development ecosystems. We use Gradle to manage dependencies and build project just like we did with Composer. We use `routing` DSL instead `Route` to define application route. We use a `Map` of `TaskDto` to represent data structure just like we use `array` in Laravel. `Exposed` is pretty much the same with `Eloquent`. We can quickly get the notion by using metaphor above. All the source code could be found [in this repository](https://github.com/shengyou/ktor-for-laravel-sample). I truly hope this article could help you get started quickly and enjoy your journey of Ktor development.
