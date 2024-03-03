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

1. [Introduction to MongoDB](https://github.com/backstreetbrogrammer/51_DistributedMongoDBAndCloudComputing?tab=readme-ov-file#chapter-01-introduction-to-mongodb)
2. [MongoDB installation for Windows](https://github.com/backstreetbrogrammer/51_DistributedMongoDBAndCloudComputing?tab=readme-ov-file#chapter-02-mongodb-installation-for-windows)
3. [MongoDB Replication](https://github.com/backstreetbrogrammer/51_DistributedMongoDBAndCloudComputing?tab=readme-ov-file#chapter-03-mongodb-replication)

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

MongoDB has the similar distributed systems concept of having a database server (`mongod`) and client (`mongo`).

**Steps to Download MongoDB:**

- Navigate to [MongoDB downloads](https://www.mongodb.com/try/download/community)
- Download the Community Server msi file
- Double-click the msi file to start the installation

**Install MongoDB:**

- Chose the `Custom Setup`
- Install `MongoD` as a Service:

![MongoDBSetup_Service](MongoDBSetup_Service.PNG)

- Copy the `bin` directory path to system `Path`

**Verify installation:**

- Search for `Services`
- MongoDB server should be running:

![MongoDBSetup_Running](MongoDBSetup_Running.PNG)

**Install Mongo Shell**

- Navigate to download [link](https://www.mongodb.com/try/download/shell)
- Download the latest msi file and install
- Copy the `bin` directory path to system `Path`

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

MongoDB config file located in `/bin` folder: `mongod.cfg`

**Playground**

We can start `mongod` on different port than default `27017`:

```
# mongo server
mongod --port 27018

# mongo client
mongo --port 27018
```  

---

## Chapter 03. MongoDB Replication

A replica set in MongoDB is a group of `mongod` processes that maintain the **_same_** data set.

Replica sets provide **redundancy** and **high availability**, and are the basis for all production deployments.

With multiple copies of data on different database servers, replication provides a level of **fault tolerance** against
the loss of a single database server.

A replica set contains several **_data bearing_** nodes and optionally one **_arbiter_** node.

Of the **data bearing** nodes, one and only one member is deemed the **_primary_** node, while the other nodes are
deemed **_secondary_** nodes.

![ReplicaSet1](ReplicaSet1.PNG)

The **_primary_** node receives all **_write_** operations.

A replica set can have **only one primary** capable of confirming writes with `{ w: "majority" }` **write concern**.

The **primary** records all changes to its data sets in its operation log, i.e. `oplog`.

The **secondaries** replicate the primary's `oplog` and apply the operations to their data sets such that the
**secondaries'** data sets reflect the **primary's** data set.

If the **primary** is unavailable, an eligible **secondary** will hold an **_election_** to elect itself the new *
*primary**.

In some circumstances, such as we have a **primary** and a **secondary** but cost constraints prohibit adding another
**secondary**, we may choose to add a `mongod` instance to a replica set as an `arbiter`.

![Arbiter](Arbiter.PNG)

An **_arbiter_** **participates** in elections but does **not** hold data (i.e. does not provide data redundancy).

An **arbiter** will always be an **arbiter** whereas a **primary** may step down and become a **secondary** and a
**secondary** may become the **primary** during an election.

**_Launching a Replication Set_**

Let's create three new directories in our local system.

```
cd <local path>/mongodb
mkdir rs0-0 rs0-1 rs0-2
```

Now, we will launch our **first** `mongodb` replica instance.

```
mongod --replSet rs0 --port 27017 --bind_ip 127.0.0.1 --dbpath "C:\Users\rishi\Downloads\BuildWithTech\Guidemy\mongodb\rs0-0" --oplogSize 128
```

We will launch other two `mongod` replica instances belonging to the same replica set `rs0`.

```
mongod --replSet rs0 --port 27018 --bind_ip 127.0.0.1 --dbpath "C:\Users\rishi\Downloads\BuildWithTech\Guidemy\mongodb\rs0-1" --oplogSize 128
```

```
mongod --replSet rs0 --port 27019 --bind_ip 127.0.0.1 --dbpath "C:\Users\rishi\Downloads\BuildWithTech\Guidemy\mongodb\rs0-2" --oplogSize 128
```

Next step is to connect to one of the `mongod` instances via mongo client: `mongo --port 27017`

Initialize our replica set by running this command:

```
rs.initiate({
  _id: "rs0",
  members: [
    {
      _id: 0,
      host: "127.0.0.1:27017"
    },
    {
      _id: 1,
      host: "127.0.0.1:27018"
    },
    {
      _id: 2,
      host: "127.0.0.1:27019"
    }
  ]
})
```

If we `quit()` and again connect: `mongo --port 27017`, we may see that our first node `rs0-0` is already elected as
**PRIMARY**.

Similarly, connection to ports `27018` and `27019` will show as **SECONDARY** nodes.

