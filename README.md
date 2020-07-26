#### Yellow Pages (Messaging)

###### Frameworks Used
* Kotlin
* Ktor (Framework for building asynchronous servers and clients in connected systems using kotlin)
* Koin (Dependency Injection)
* jetbrains exposed
* H2 db as inmemory data storage (easier and faster for quick tasks no configurations needed in partners side)
* uchuhimo (for Config)
* Flyway (For database migration)
* Kafka (Message exchange)

###### Reasons to choose the frameworks
I appreciate the runtime and performance specially when it comes to reviewing tasks, that's why I Used Ktor , Koin , exposed which give better performance and more readable with kotlin
Choosing of H2 to Facilitate the reviewer/ participator to test without much configurations in their side , so no need to have pre-installed DBMS (Plug & Play :) ) and as well Flyway.
No need to worry about Configurations. Everything can be done through the package manager.
(P.S.) As H2 is in memory DB , so once the server is being shut down , all the data will be lost. but it will help you to play around the service.
Change app config for other DBMS, (it's one line change) to use postgres or others. i tried it and can say 100% works

###### Data Model Concept
As General Concept , User should be able to send message to anymore who is also a user in the system,
The user can have account easily by providing Nickname , so the following entities are considered:

User :
* id
* nickname (unique , db make sure of that but i added another check in the code )
* CreatedAt  

Message:
* id
* text 
* CreatedAt
* fromUserNickName
* toUserNickName (can't be fromUserNickName , sorry can't send to yourself you have to socialize)


###### How to start?
* Please Generate self contained Jar (**gradle clean build** is good option :D )
* Start the server by java -jar build/libs/Messaging.jar
* OR (Smart Docker ;) , Build Docker image : **docker build -t messaging .** and Run it : **docker run -p 8080:8080 messaging**
* Use Curl or Insomnia (it's cool) to trigger your calls
* Code is covered by Tests (Please approach me for clarifications)-(hit **./gradlew test** will not bite :D)
* Hint , Use the following Endpoints to navigate (Don't worry you are almost covered)

      POST   "/api/user"                            -> create new user
             provide the following json body like:
             `
             {
              	"nickname": "maha"
              }
              `

      GET    "/api/user"                            -> list existing users (helper api to make ur life easier cause it's H2)
    
      POST   "/api/message"                         -> Send message
              provide the following json body like:
               `{
                 "text": "maha"
                }
               `
               and **from-user** , **to-user** in the header
               
      GET    "/api/message/sent/all"                -> Retrieve all sent messages
               ** user ** in the header
     
      GET    "/api/message/sent/to-user"            -> Retrieve  sent messages to specific user
               ** user ** and **to-user** in the header
     
      GET    "/api/message/received/all"            -> Retrieve  received messages
                ** user ** in the header
     
      GET    "/api/message/received/from-user"      -> Retrieve  received messages from specific user
                ** user ** and **from-user** in the header

###### Happy Note :/
  Kafka as a messaging Queue is working fine in this demo, but i have to run zookeeper and kafka server on port 9093 to get it done
  change the port in the yaml file to follow yours
  Run Zookeeper and Kafka server at first (you will have message topic created for you) read the next paragraph please
  
###### Sad Note :/
  Unfortunately i am so naiive in Kafka and i found that the application is flaky when the kafka server is down.
  i am not sure if you will have Kafka up and running while running the app, thats why i commented the code.
  to get it work after you have it running in your machine please uncomment:
   MessageController -> line 32
   KafkaUtils -> lines 23/24 (They check is the topic exists, if not it create it so no message will be missed)





