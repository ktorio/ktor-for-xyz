# Ktor for Laravel Developer

Welcome, artisan. When you first jump into a technical stack that you are not familiar with, it must be uncomfortable. Fortunately, web development is not that different between programing language and framework. If you prefer a lightweight, easy-to-learn framework in [Kotlin](https://kotlinlang.org/) ecosystem, then Ktor will be a perfect match for you.

## Meet Ktor Framework

[Ktor](https://ktor.io/) (pronounced Kay-tor) is a framework built from the ground up using Kotlin and coroutines. It aim to leverage to the maximum extent some of the language features that Kotlin offers, such as DSLs and coroutines. Compare with other ecosystems, it’s a framework like Slim in PHP, Express.js in JavaScript, Sinatra in Ruby. Ktor is developed by JetBrains and used internally. It’s an open source project using Apache 2.0 license.

In this article, we will guide you through the steps to build an RESTful API with H2 database using Ktor. We will use terminologies in [Laravel](https://laravel.com/) to make you get started quickly.

## Create a Ktor project

To have a development environment for Ktor is quite simply, the only thing you need is [IntelliJ IDEA](https://www.jetbrains.com/idea/). Install JetBrains [Toolbox App](https://www.jetbrains.com/toolbox-app/) and install IntelliJ IDEA first. After IntelliJ IDEA installed, don’t forget to [install JDK](https://www.jetbrains.com/help/idea/sdk.html) (like PHP interpreter) and [Ktor plugin](https://plugins.jetbrains.com/plugin/16008-ktor) for later use.

After the Ktor plugin installed properly, we can now create a new Ktor project using IntelliJ IDEA. Click the Ktor tab in the New Project dialog and fill out the project information. In next step, search and add “`Routing`”, “`ContentNegotiation`” and “`Jackson`” features then hit finish. IntelliJ IDEA will create a brand new Ktor project using [Gradle](https://www.jetbrains.com/help/idea/gradle.html) for you automatically. This process is equal when we use [Composer](https://getcomposer.org/) command to create a new project, add dependency in PHP but in a GUI way.

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

Ktor structure itself by using interceptor pattern. Similar the concept of middleware, it means each HTTP request will pass through every interceptor and produce HTTP response. We called these interceptors as Features. Think the features is the abilities that application have. Therefore, when we need our application to handle JSON, we will need to “**install**” a feature called “`ContentNegotiation`”, and a JSON serialization library called “`Jackson`”. Ktor provide `install()` function to include a feature. Inside the function, we could customize the feature by passing closure.

```
install(ContentNegotiation) {
    jackson {
        enable(SerializationFeature.INDENT_OUTPUT)
    }
}
```

When we respond a JSON in Laravel, we use arrays to structure our data. In Ktor, we use a similar structure called `Map`. `Map` is a key-value pair and we have `mapOf()` function in Kotlin to help us declare such structure. When we pass a map to Ktor, it will automatically serialize it for us. We could see the sample code in the second routing.

```
get("/json/jackson") {
    call.respond(mapOf("hello" to "world"))
}
```

Open browser at `http://localhost:8080/json/jackson`, you will see the JSON string `{ "hello": "world" }`.

## Integrate with database using Exposed ORM

We are going to build a TODO RESTful API. In order to store all the `Task` information, we need an ORM like Eloquent. Ktor designed to be a slim, light-weight framework, there is no ORM builtin. Fortunately, there is an ORM framework called Exposed that develop by JetBrains!

### Add dependencies

Before using Exposed, we need to decide which database we are going to use. In this article, we will use [H2](https://www.h2database.com/html/main.html) in-memory database to provide a similar experience with SQLite. Just like we manage our dependencies using `composer.json`. In Ktor, we defined our dependencies using Gradle's  
`build.gradle.kts` file. Open it and add `exposed-core`, `exposed-dao`, `exposed-jdbc` for Exposed also `h2` for H2 driver. Our `dependencies` sections will look like this:

```
dependencies {
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("com.h2database:h2:$h2_version")
}
```

### Schema and Entity

In order to interact with database, we need an object to reflect the db schema. Let's declare a schema object like this:

```
object Tasks : IntIdTable() {
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

### Connect to database

Everytime we run our application, we need to connect to database for later use. Use the `Database` class to connect H2 database by providing `url` and passing the `driver` to it.

```
Database.connect(
    url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
    driver = "org.h2.Driver"
)
```
