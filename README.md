#### Sheet Sharing

###### Frameworks Used
* Kotlin
* Ktor (Framework for building asynchronous servers and clients in connected systems using kotlin)
* Koin (Dependency Injection)
* jetbrains exposed
* H2 db as inmemory data storage
* uchuhimo (for Config)
* Flyway (For database migration)

###### Reasons to choose the frameworks
I wanted to illustrate the idea i discussed during the initial interview regarding using frameworks other than Spring Umbrella.
That's why I Used Ktor , Koin , exposed which give better performance for small services
Choosing of H2 to Facilitate the reviewer testing , so no need to have pre-installed DBMS (Plug & Play :) ) and as well Flyway.
No need to worry about Configurations. Everything can be done through the package manager.
(P.S.) As H2 is in memory DB , so once teh server is being shut down , all the data will be lost. but it will help you to play around the service.
Change app config for other DBMS, (it's one line change)

###### Data Model Concept
As General Concept , The service is expecting to receive selections that should be stored along with for whom are those changes are intended
and because the service is intermediate service , it doesn't need to store full not needed data, so the following entities are considered:

Sharing :
* Selection (The Intended Share that should be visible to users)
* Sheet (it would be better for FE to have this info. )
* CreatedAt  

Recipient Sharing:
* rec-email ( it should be anonlymized for GDBR reasons)
* Sharing Id

So when saving Sharings , data will be saved on those two tables , upon retrieval , a complete model of sharings and associated recipient will be formed
and returned to client

###### How to start?
* Please Generate self contained Jar (**gradle clean build** is good option :D )
* Start the server by java -jar build/libs/Layer.jar
* OR (Smart Docker ;) , Build Docker image : **docker build -t layer .** and Run it : **docker run -p 8080:8080 layer**
* Use Curl or Insomnia (it's cool) to trigger your calls
* Code is covered by Tests (Please approach me for clarifications)-(hit **./gradlew test** will not bite :D)
* Hint , Use the following Endpoints to navigate (Don't worry you are almost covered)

      GET   "/api/shares"                            -> list all sharings with associated recipients
      GET   "/api/shares/recipient/{recipient}"      -> retrieve recipient sharings
      GET   "/api/shares/{id}"                       -> retrieve sharing by id
      POST  "/api/shares"                            -> create new sharings

    
#### For Concurrency : 
I want to add Concurrency according to the following Article if time was sufficient  
     https://blog.mindorks.com/mastering-kotlin-coroutines-in-android-step-by-step-guide 
               (It's very cool illustration of concurrency in kotlin coroutines)






