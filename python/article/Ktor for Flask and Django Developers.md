# Ktor for Flask and Django Developers

Ktor is a brand new framework from the creators of IntelliJ that promises to make tooling an integral part of the web development experience. This means that a lot of the tooling is integrated closely with both the language (Kotlin) and framework (Ktor) features.

If you're new to Ktor, and have a Python web development background, this is the article for you. We're going to take a look at what Ktor has to offer, with a focus on getting set up the right way, and using the right set of tools to get productive. The main focus here is to understand what Ktor has to offer, understand the minimal level of plumming that is required and then go onto more advanced functionality. We will be building two applications through this series.

The first is a simple "Greeter Application" that should showcase some of the most basic features. In other words, we are going to get Ktor to spit out some of the output that we want, as soon as possible. We are going to incrementally go over how we can make things better, so if you follow along you will not only learn some language features, but also features of the framework.

We will then move on to building a simple Todo App API, that will use some of Ktor's slightly more advanced features. This means that we're going to be talking to a database as well as managing user authentication.

## Getting Set Up

- Project Creation
    - Focus on the minimal that is required
    - Explain things like gradle
- Running the first Application, explain what different parts of the application does
- Figure out auto-reload

## JSON In and JSON Out

- Create the first Greeter app, and make sure that you are explaining `mapOf` in a proper way.
- Explain how the DSL works to help you manage serialization
- Perhaps show how you can use json to set a permanent variable. Use POST to set the variable once, and then show this change in a GET method.

## Organising your Views

- Split out the views into different parts.

## The Todo API

- Introduce the Todo API
- Talk about Locations

## Adding Database

- Install Exposed in gradle

## Adding Authentication

- Needto find out about this more

## Conclusion

- Point out main differences, give some reasons as to "why" you would want to do one thing over the other.