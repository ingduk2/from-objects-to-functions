package com.zettai.ui

import com.zettai.domain.*
import com.zettai.fp.unlessNullOrEmpty
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class HtmlPage(val raw: String)

fun renderPage(todoList: ToDoList): HtmlPage =
    HtmlPage(
        """
        <!DOCTYPE html>
        <html>
        <head>
            <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
            <title>Zettai - a ToDoList application</title>
        </head>
        <body>
        <div id="container">
        <div class="row justify-content-md-center"> 
        <div class="col-md-center">
            <h1>Zettai</h1>
            <h2>ToDo List ${todoList.listName.name}</h2>
            <table class="table table-hover">
                <thead>
                    <tr>
                      <th>Name</th>
                      <th>Due Date</th>
                      <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                ${todoList.renderItems()}
                </tbody>
            </table>
            </div>
        </div>
        </div>
        </body>
        </html>
    """.trimIndent()
    )

fun renderListsPage(user: User, lists: List<ListName>) = HtmlPage(
    """
        <!DOCTYPE html>
        <html>
        <head>
            <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
            <title>Zettai - a ToDoList application</title>
        </head>
        <body>
        <div id="container">
        <div class="row justify-content-md-center"> 
        <div class="col-md-center">
            <h1>Zettai</h1>
            <h2>User ${user.name}</h2>
            <table class="table table-hover">
                <thead>
                    <tr>
                      <th>Name</th>
                      <th>State</th>
                      <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                ${lists.render(user)}
                </tbody>
            </table>
            <hr>
            <h5>Create new to-do list</h5>
            <form action="/todo/${user.name}" method="post">
              <label for="listname">List name:</label>
              <input type="text" name="listname">
              <input type="submit" value="Submit">
            </form>
            </div>
        </div>
        </div>
        </body>
        </html>
    """.trimIndent()
)

fun renderListPage(user: User, lists: List<ListName>): HtmlPage = HtmlPage(
    """
        <!DOCTYPE html>
        <html>
        <head>
            <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
            <title>Zettai - a ToDoList application</title>
        </head>
        <body>
        <div id="container">
        <div class="row justify-content-md-center"> 
        <div class="col-md-center">
            <h1>Zettai</h1>
            <h2>User ${user.name}</h2>
            <table class="table table-hover">
                <thead>
                    <tr>
                      <th>Name</th>
                      <th>State</th>
                      <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                ${lists.render(user)}
                </tbody>
            </table>
            <hr>
            <h5>Create new to-do list</h5>
            <form action="/todo/${user.name}" method="post">
              <label for="listname">List name:</label>
              <input type="text" name="listname">
              <input type="submit" value="Submit">
            </form>
            </div>
        </div>
        </div>
        </body>
        </html>
    """.trimIndent()
)

private fun List<ListName>.render(user: User): String =
    joinToString(separator = "") { renderListName(user, it) }

private fun renderListName(user: User, listName: ListName): String = """<tr>
              <td><a href="${user.name}/${listName.name}">${listName.name}</a></td>
              <td>open</td>
              <td>[archive] [rename] [freeze]</td>
            </tr>""".trimIndent()

private fun ToDoList.renderItems() =
    items.map(::renderItem).joinToString("")

private fun renderItem(it: ToDoItem): String =
    """
        <tr>
              <td>${it.description}</td>
              <td>${it.dueDate?.toIsoString().orEmpty()}</td>
              <td>${it.status}</td>
            </tr>
    """.trimIndent()

fun LocalDate.toIsoString(): String = format(DateTimeFormatter.ISO_LOCAL_DATE)

fun String?.toIsoLocalDate(): LocalDate? =
    unlessNullOrEmpty {
        LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)
    }

fun String.toStatus(): ToDoStatus = ToDoStatus.valueOf(this)