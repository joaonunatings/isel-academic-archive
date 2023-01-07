<div style="text-align: center">
   Instituto Superior de Engenharia de Lisboa
   <div style="font-size: 80%">
   Bachelor in Computer Science and Computer Engineering
   <br>Bachelor in Informatics, Networks and Telecommunications Engineering
   </div>
   Internet Programming/Introduction to Web Programing

   Winter Semester of 2021/2022 – <strong> 3rd practical assignment - BORGA - Part2 </strong>
</div>

---

# Delivery

### **Due date for this assignment: 10/01/2022-23h59**.

### **Delivery method**

The development of the BORGA application has 3 development cycles, phased in three parts. For each one, the deadline for delivering the solution will be defined, and it will be a non-negotiable requirement.

This document includes the requirements of **part 2 of the BORGA application**. The assignment should be delivered in the same Git repository used in part 1, by creating a tag called `BORGA-P2`. If you need to make some changes to the delivery, just create another tag named `BORGA-P2.1`, `BORGA-P2.2`, etc.

The main goals of this part are adding a Web user interface to the BORGA application developed in part 1, storing data in a database instead of memory, and incorporating new technologies and techniques covered in lectures. 

## Functional requirements

1. Add the following to BORGA Web API:
    1. Support more than one group with the same name, irrespective of its owner user.  
    2. Create a new resource that returns the game details, which must include at least:  id, name, description, url (at Board Game Atlas), image_url, mechanics names and category names..

1. Create a web interface for presentation in a _web browser_, for all the functionalities provided by the Web API, except for user creation and any route with HTTP method PUT or DELETE. This web interface is server-side rendered, using the following technologies: HTML, CSS, and Handlebars. You may use Bootstrap for the base style of the user interface.

When using the web application, in no situation the (human) user will have to know and/or introduce any id for groups or games. The only situations in which it is allowed to write the name of a game is to carry out searches in order to obtain a list of results. The only situation in which it is allowed to manually enter the name of a group is when creating or editing that group.

## Non-functional requirements

1. The web HTML and CSS interface should be implemented in a new file called `borga-web-site.js` that should be at the same level as `borga-web-api.js`   
2. Create a new module that replaces `borga-data-mem.js` so that the data is stored in an ElasticSearch database. This change should not imply any additional change in the remaining modules of the application besides module loading in `borga-server`. The interaction with ElasticSearch must be done through its HTTP API, without using any specific node module, besides `node-fetch`.
3. Create integration tests for the API with [supertest](https://www.npmjs.com/package/supertest) module.
4. In addition to the previous requirements, this part of work should be used to improve the code quality as well as the quality and quantity of tests, whether unit or integration.


The server application modules dependencies should be as follows:

![Module dependencies1](http://www.plantuml.com/plantuml/png/LP11RiGW34NtFeLx0AvGZTeZT5bruO15K0AEZ6UgKTMx5uZIoImGd__yZU5IZTIkUOKNcpapreNDptUeIoeRAMLy8xz4bMi9xxAAeYcLsX0NErLvoPkKvdtOVMLKzaObhWmc6vhW96QICQHEaXI0_qHZ6Wb_u7C8zysJeorz8LLi-zckoNZuNHoWijwIvph0SUYGDBX8cQiBP3Hm5qSkJBir0RHhqBdNHxhtVtOEqTszpRCpmU_vJQVPMFV9z0PjeTCKPskHKG4NBg7z_ny0)


