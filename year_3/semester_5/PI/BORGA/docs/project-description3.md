<div style="text-align: center">
   Instituto Superior de Engenharia de Lisboa
   <div style="font-size: 80%">
   Bachelor in Computer Science and Computer Engineering
   <br>Bachelor in Informatics, Networks and Telecommunications Engineering
   </div>
   Internet Programming/Introduction to Web Programing

   Winter Semester of 2021/2022 – <strong> 3rd practical assignment - BORGA - Part3 </strong>
</div>

---

# Delivery

### **Due date for this assignment: 29/01/2022-23h59**.
Deploy may be delivered up to 3 days before final discussion.

### **Delivery method**

The development of the BORGA application has 3 development cycles, phased in three parts. For each one, the deadline for delivering the solution will be defined, and it will be a non-negotiable requirement.

This document includes the requirements of **part 3 of the BORGA application**. The assignment should be delivered in the same Git repository used in part 1, by creating a tag called `BORGA-P3`. If you need to make some changes to the delivery, just create another tag named `BORGA-P3.1`, `BORGA-P3.2`, etc.

The main goals of this part are adding authentication to the web application with user interface (web site), and support the PUT and DELETE operations not implemented in the previous phase, using JavaScript on the client side.
Another goal of this phase is to deploy the BORGA application to [Heroku](https://www.heroku.com/) cloud platform, so that the application is worldwide available.

## Functional requirements

1. Add user registration and authentication functionality to the BORGA application. All game group management features must be accessible only to authenticated users. Groups are private to each user and can only be manipulated by their owner. The authentication must be implemented with the [`Passport`](https://www.passportjs.org/) module.

2. All functionalities available in the Web API developed in BORGA part 1, must be available also through the user interface available at the web site, namely those that are available with the PUT and DELETE HTTP methods.  

Again, as it was a requirement for part 2, when using the web application, in no situation the (human) user will have to know and/or introduce any id for groups or games. The only situations in which it is allowed to write the name of a game is to carry out searches in order to obtain a list of results. The only situation in which it is allowed to manually enter the name of a group is when creating or editing that group.
  
## Non-functional requirements

1. With this part of the work, a report must be delivered to the group's repository wiki, describing the implementation of the full work developed during the semester. Thus, the intermediate states that the implementation went through in each of the phases should not be included. The report must include:
    * Description of the application structure, in both components (server and client).
    * Data storage design (i.e. in ElasticSarch), namely: index or indices; document properties; relations between documents, if exit; etc.
    * Description of the mapping between the ElastictSearch’s Borga documents and the web application objects model.
    * Server API documentation.
    * Instructions for **<u>all</u>** the previous steps that are necessary to run the application and the respective tests.
      * These steps must include the actions necessary for the automatic introduction of test data, so that it is possible to run the application with data.
      * Instructions must have all the information needed to run the application on any machine, namely the teacher's. The instructions to make the application run should be executed in a maximum of 5 min.

2. The instructions to deploy the application in Heroku will be published soon, in a specific page for this purpose.