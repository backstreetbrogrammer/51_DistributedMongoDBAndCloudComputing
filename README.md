# Distributed MongoDB and Introduction to Cloud Computing

> This is a tutorial course covering MongoDB and introduction to cloud computing.

Tools used:

- JDK 11
- Maven
- JUnit 5, Mockito
- IntelliJ IDE
- MongoDB
- AWS

## Table of contents

1. Introduction to MongoDB
2. MongoDB installation for Windows

---

## Chapter 01. Introduction to MongoDB

MongoDB is a **document database** designed for ease of application development and scaling.

**_Document Database_**

A **record** in MongoDB is a **document**, which is a data structure composed of **field** and **value** pairs.

MongoDB documents are similar to JSON objects.

The values of fields may include other documents, arrays, and arrays of documents.

![DocumentDB](DocumentDB.PNG)

The advantages of using documents are:

- Documents correspond to native data types in many programming languages
- Embedded documents and arrays reduce the need for expensive joins
- Dynamic schema supports fluent polymorphism

MongoDB stores documents in **collections**.

**Collections** are analogous to **tables** in relational databases.

MongoDB stores data records as **BSON** documents.

BSON is a binary representation of JSON documents, though it contains more data types than JSON.

Each document object is assigned a unique immutable id field: `_id`, which uniquely identifies the object. For example,

```
_id: ObjectId("5099803df3f4948bd2f98391"),
```

**_SQL DB vs MongoDB Terminology_**

![SQLvsMongo](SQLvsMongo.PNG)

**Primary Key** can be marked equivalent to **_id** field in MongoDB.

**_MongoDB CRUD Operations_**

- **Create**
    - `db.collection.insertOne(object)`
    - `db.collection.insertMany([object1, object2...])`

![InsertOne](InsertOne.PNG)

- **Read**
    - `db.collection.find(filter)`
    - `db.collection.findOne(filter)`

![Find](Find.PNG)

- **Update**
    - `db.collection.updateOne(filter, update action)`
    - `db.collection.updateMany(filter, update action)`
    - `db.collection.replaceOne(filter, replacement)`

![UpdateMany](UpdateMany.PNG)

- **Delete**
    - `db.collection.deleteOne(filter)`
    - `db.collection.deleteMany(filter)`

![DeleteMany](DeleteMany.PNG)

The document's `_id` field is immutable and can **NOT** be changed after the document's creation.

There are many operations for querying, aggregation and bulk operations available.

---

## Chapter 02. MongoDB installation for Windows

**Steps to Download MongoDB:**

- Navigate to [MongoDB downloads](https://www.mongodb.com/try/download/community)
- Download the Community Server msi file
- Double-click the msi file to start the installation

**Install MongoDB:**

- Chose the `Custom Setup`
- Install `MongoD` as a Service:

![MongoDBSetup_Service](MongoDBSetup_Service.PNG)

**Verify installation:**

- Search for `Services`
- MongoDB server should be running:

![MongoDBSetup_Running](MongoDBSetup_Running.PNG)

**Install Mongo Shell**

- Navigate to download [link](https://www.mongodb.com/try/download/shell)
- Download the latest msi file and install
- Copy the `bin` directory to system `Path`

Launch Mongo Shell by executing `mongosh.exe` from command prompt

Some sample commands to run on shell:

- show all the databases: `show dbs`
- create a new database (say, **"shop"**) and use it at the same time: `use shop`
- create a new collection (say, **"products"**) and use it (insert records):
  `db.products.insertOne({name: "Guitar", price: 100.99})`
- show all the collections: `show collections`
- find all the documents in the collection: `db.products.find()`
- update a document: `db.products.updateOne({name: "Guitar"}, {$set: {price: 156.2}})`
- count all the documents: `db.products.countDocuments({})`
- delete all the documents: `db.products.deleteMany({})`
- exit the shell: `quit()`

