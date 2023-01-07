<div style="text-align: center">
   Instituto Superior de Engenharia de Lisboa
   <div style="font-size: 80%">
   Bachelor in Computer Science and Computer Engineering
   <br>Bachelor in Informatics, Networks and Telecommunications Engineering
   </div>
   Internet Programming/Introduction to Web Programing

   Winter Semester of 2021/2022 – **2nd practical assignment - BORGA - Part1 **
</div>

---

# Delivery

### **Due date for this assignment: 08/12/2021-23h59**.

### **Delivery method**

Except for classes LEIC52D and LEIC51N, the delivery method is done through Github Classroom, using [this invitation link](https://classroom.github.com/a/nYJbDZ7o). Following this link, login with your github user and select your student id to make the correspondence between them and create a new group or join an existing one. If you create a new repository, the repository name should be: `<LEIC|LEIRT><COURSE><CLASS>-<GRUOUP_NUMBER>`. For example, group 2 for class LI51D from IP course, should name the repository: `LEICIP51D-G02`.  
The assignment should be delivered in this repository, by creating a tag called `BORGA-P1`. If you need to make some changes to the delivery, just create another tag named `BORGA-P1.1`, `BORGA-P1.1`, etc.


# Introduction

The practical component evaluation for this course will be carried out based on the BORGA (BOaRd Games Application) application, to be developed throughout the semester. This application provides access, through a web interface (hypermedia), to some of the features provided by the [Board Game Atlas] website (https://www.boardgameatlas.com/), making use of its Web API for this purpose:
[https://www.boardgameatlas.com/api/docs](https://www.boardgameatlas.com/api/docs). To enable access to this API, each student should use the BoardGames Atlas API token obtained for 1st assignment. This token will be included in all HTTP requests.
The description of the steps necessary to create this token is [here](https://www.boardgameatlas.com/api/docs/apps). Note that the API is free to use and as such is expected to comply with the [Rules of Good Usage](https://www.boardgameatlas.com/api/docs/ratelimits).

The development will be carried out incrementally, necessarily involving several code refactoring cycles and, therefore, it is essential that you make use of good programming practices in general, in order to reduce the effort associated with each cycle.

The development of the BORGA application has 3 development cycles, phased in three parts (Part 1, Part 2 and Part 3). For each one, the deadline for delivering the solution will be defined, and it will be a non-negotiable requirement.

For each BORGA functionality, the corresponding HTTP *endpoint* must be defined. The description of the application API (i.e all application endpoints) must appear on the repository in an [OpenAPI](https://oai.github.io/Documentation/specification.html) file, named `docs/borga-api-spec.json` (or .yml). The repository must also contain the Postman collection export of the with requests that validate the API, in a file named  `docs/borga-api-test.json`.

Summary of the artifacts to be submitted upon delivery:

* OpenAPI/Swagger file with API documentation - `docs/borga-api-spec.json`
* Postman API validation project for BORGA application - `docs/borga-api-test.json`
* Node.js BORGA application files

# Functional Requirements

Develop a web application that provides a web API that follows the REST principles, with responses in JSON format and that supports the following features:

* Get the list of the most popular games
* Search games by name
* Manage favorite game groups
  * Create group providing its name and description
  * Edit group by changing its name and description
  * List all groups
  * Delete a group
  * Get the details of a group, with its name, description and names of the included games.
  * Add a game to a group
  * Remove a game from a group
* Create new user
  
For all group operations, a user token must be sent in the [Authorization header](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Authorization) using a Bearer Token. This token is generated at user creation, and consists of a UUID string, obtained from the `crypto.randomUUID()` method.

# Non-Functional Requirements

The application must be developed with Node.js technology. To handle/receive HTTP requests, the [express](https://expressjs.com/) module must be used. To make HTTP requests, the [node-fetch](https://www.npmjs.com/package/node-fetch) module must be used.

The Board Games Atlas API is used to obtain data (query) about the games.

The data that are specific to the application, which can be created, altered and deleted, namely the entire group management, must be stored in memory.

Any of the base modules of Node.js can be used. In addition to these, in this 1st part of the work, only the following modules can be used:

* express - Handling HTTP requests
* node-fetch - Making HTTP requests
* debug – Debug messages
* jest - Unit tests
  
Any other module you intend to use must be previously discussed and authorized by the corresponding teacher.

All PUT and POST requests must send their data in the request body (_body_) and never inthe _query string_. The body should be handled by [builtin Express JSON middleware](https://expressjs.com/en/4x/api.html#express.json).

The server application must consist of <u>at least</u> 5 Node modules:

* <code>borga-server.js</code> - file that constitutes the entry point to the server application
* <code>borga-web-api.js</code> - implementation of the HTTP routes that make up the REST API of the web application
* <code>borga-services.js</code> - implementation of the logic of each of the application's functionalities
* <code>board-games-data.js</code> - access to the Board Games Atlas API.
* <code>borga-data-mem.js</code> - access to borga data (groups and users), in this version stored in memory .

The dependencies between these modules are as follows:

<pre>
borga-server.js -> borga-web-api.js -> borga-services.js -> board-games-data.js
                                                               -> borga-data-mem.js
</pre>

The server application development methodology must be as follows and in this order:

1. Design and document API routes (HTTP request type + URL+example response content) using OpenAPI/Swagger format.
2. Create a collection in Postman (ex. BORGA) to test API routes
3. Implement the server application input module: <code>borga-server.js</code>. For this module it is not necessary to create unit tests, since it must not implement any logic other than receiving some arguments from the command line (configuration), registering routes and starting the web server. This module can be built as each route in borga-web-api.js is implemented.
4. In the <code>borga-web-api.js</code> module implement the API routes one by one.
   * For each route implemented, use Postman tests to verify the correct functioning of that route.
   * Only move on to implementing the next route when the previous one is fully implemented and tested.
   * For each route, create a request in the Postman collection that validates it.
   * In this phase of the implementation of the module <code>borga-web-api.js</code> **use local data (*mock* of <code>borga-service.js</code>)**, that is, testing must be performed without access to the Board Game Atlas API or ElasticSearch.
  
5. Implement application services in borga-services.js module.
   * Follow an approach similar to the one used in `borga-web-api.js` in the development of the features of this module and respective unit tests.
   * `borga-services.js` module unit tests must be run without access to the Board Games Atlas API (`board-games-data.js`).
6. Implement data access modules:
   * <code>`board-games-data.js`</code> - access to the Board Games Atlas API.
   * <code>`borga-data-mem.js`</code> - access groups data.