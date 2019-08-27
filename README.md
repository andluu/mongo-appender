
#Pros and cons of MongoAppender

##Pros
- Opportunity to choose in which database and collection to store logs.
- Opportunity to store logs secured (add auth to MongoDB)
- Using fast and flexible searching provided by MongoDB ``$text`` operator
- Opportunity to query logs within some range through MongoDB
- MongoDB's native support of horizontal scaling of log database.

##Cons
- Strictly tied to MongoDB
- Low performance due to sync inserts. (It can be solved with replacing sync driver with async)
- Max document size: 16 MB (Constraint of MongoDB)