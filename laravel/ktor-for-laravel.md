# Ktor for Laravel Developer

Welcome, artisan. When you first jump into a technical stack that you are not familiar with, it must be uncomfortable. Fortunately, web development is not that different between programing language and framework. If you prefer a lightweight, easy-to-learn framework in [Kotlin](https://kotlinlang.org/) ecosystem, then Ktor will be a perfect match for you.

## Meet Ktor Framework

[Ktor](https://ktor.io/) (pronounced Kay-tor) is a framework built from the ground up using Kotlin and coroutines. It aim to leverage to the maximum extent some of the language features that Kotlin offers, such as DSLs and coroutines. Compare with other ecosystems, it’s a framework like Slim in PHP, Express.js in JavaScript, Sinatra in Ruby. Ktor is developed by JetBrains and used internally. It’s an open source project using Apache 2.0 license.

In this article, we will guide you through the steps to build an RESTful API with H2 database using Ktor. We will use terminologies in [Laravel](https://laravel.com/) to make you get started quickly.

## Create a Ktor project

To have a development environment for Ktor is quite simply, the only thing you need is [IntelliJ IDEA](https://www.jetbrains.com/idea/). Install JetBrains [Toolbox App](https://www.jetbrains.com/toolbox-app/) and install IntelliJ IDEA first. After IntelliJ IDEA installed, don’t forget to [install JDK](https://www.jetbrains.com/help/idea/sdk.html) (like PHP interpreter) and [Ktor plugin](https://plugins.jetbrains.com/plugin/16008-ktor) for later use.

After the Ktor plugin installed properly, we can now create a new Ktor project using IntelliJ IDEA. Click the Ktor tab in the New Project dialog and fill out the project information. In next step, search and add “**Routing**”, “**ContentNegotiation**” and “**Jackson**” features then hit finish. IntelliJ IDEA will create a brand new Ktor project using [Gradle](https://www.jetbrains.com/help/idea/gradle.html) for you automatically. This process is equal when we use [Composer](https://getcomposer.org/) command to create a new project, add dependency in PHP but in a GUI way.

(gif)

Take a look of the Project tool window in the left hand side. It contains a folder called `src`, that’s the place we write Kotlin code. Open up the `Application.kt` file, as you can see, there is a `main` function inside which is the entry point of Ktor application. Hit the green play button in the gutter, IntelliJ IDEA will trigger Gradle task to compile and run the application. After the application start up. Open browser at `http://localhost:8080/`, you will see `HELLO WORLD!`. This process is similar we type `artisan serve` in the terminal to run application in Laravel.

(gif)

## Routing

You must be familiar with the notion of routing in Laravel. Route is like the lobby of an application, it determine an HTTP request could pass by path and method. In Ktor, you could define your routing using DSL syntax inside `Application.module()` function in `Application.kt`. This syntax describes how it response to an HTTP request.

We got a hello world sample when creating project. Inside the `routing { }`, we have a `get()` function that accept `"/"` root URL string. The beautiful thing of DSL is, it made route definition in a structured and easy understanding way. As you can expect, we could use `post()`, `put()`, `patch()`, `delete()` methods to accept certain HTTP method. Compare to Laravel, the syntax is equal to `Route::get()`, `Route::post()`, etc.

## The application call

When we deal with HTTP, it typically contains two parts: request and response. In Ktor, it put those in an application call object. Inside our routing DSL, you will receive a `call` object inside the function. We could access the request details by `call.request` and setup our response by `call.respond` related methods. We can see how Ktor response a plain text response in the sample code, just pass a string and setup content type by using builtin classes.

```
call.respondText(
    text = "HELLO WORLD!",
    contentType = ContentType.Text.Plain
)
```

We are going to build a RESTful API, right? How about JSON response?
