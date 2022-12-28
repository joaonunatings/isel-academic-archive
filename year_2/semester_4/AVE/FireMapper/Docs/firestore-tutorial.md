FireStore database is a cloud-hosted, NoSQL database that may store JSON
documents. FireStore databases are managed within a FireBase project, thus you
have first to create a Firebase project, following next steps.

At the end you will create a simple Dotnet console application to get your JSON
documents.

1. [https://firebase.google.com/](https://firebase.google.com/)
2. You should be logged in with a google account.
3. Click on “Get started”


![drawing](Images/image20.png)


4. Click on “Create Project”

![drawing](Images/image24.png)


5. Give a name to your project. A unique identifier will be proposed for your
   project (Remember it! You will need it later to access your project).

![drawing](Images/image10.png)

6. Choose whether you want to track with Google analytics or not. It is your
   choice.

![drawing](Images/image11.png)

7. Create Project. It will take a while.

![drawing](Images/image1.png)

8. Proceed with continue

![drawing](Images/image5.png)

9. Expand the Build menu and select Firestore Database

![drawing](Images/image19.png)

10. Click on “Create Database”

![drawing](Images/image7.png)

11. Start in production mode

![drawing](Images/image21.png)

12. Set your location

![drawing](Images/image26.png)

13. Wait …

![drawing](Images/image3.png)

14. Start a new Collection
15. Give the collection ID “students”

![drawing](Images/image27.png)

16. Select Auto-ID and add two fields for your first document: name and number

![drawing](Images/image14.png)

17. Add a couple of more documents with the same structure: name and number.

![drawing](Images/image4.png)

18. Select Project Settings

![drawing](Images/image2.png)

19. Service and Accounts

![drawing](Images/image16.png)

20. Generate a new private key

![drawing](Images/image8.png)

21. Generate Key

![drawing](Images/image23.png)

22. Download and later copy it to the AppFireStudents project folder.

![drawing](Images/image22.png)

23. Create a dotnet console application to access your fire-students database

![drawing](Images/image6.png)

24. cd AppFireStudents
25. Open [https://www.nuget.org/](https://www.nuget.org/)
26. Type on search: google cloud firestore
27. Select Google.Cloud.Firestore by: google-cloud

![drawing](Images/image17.png)

28. Copy .NET CLI command

![drawing](Images/image15.png)

29. Paste

![drawing](Images/image25.png)

30. Type `cat AppFireStudents.csproj` and you will observe:

![drawing](Images/image13.png)

31. Paste here, in the AppFireStudents folder,  the json file downloaded in step
    22.

32. Set an environment variable GOOGLE_APPLICATION_CREDENTIALS with the path to
    the json file, such as (notice in Linux use EXPORT rather than SET):

![drawing](Images/image12.png)

33. Write the following `Program.cs`. **NOTICE that you should use your project ID on line 9 where you find `FirestoreDb.Create("USE here YOUR Project ID");`** (e.g. `fire-students`). On step 19 of this guide you may find your Project ID.

![drawing](Images/image9.png)

34. dotnet run should produce something like:

![drawing](Images/image18.png)