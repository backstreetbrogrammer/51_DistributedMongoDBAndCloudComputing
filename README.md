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
4. [MongoDB Sharding](https://github.com/backstreetbrogrammer/51_DistributedMongoDBAndCloudComputing?tab=readme-ov-file#chapter-04-mongodb-sharding)
5. [Introduction to Cloud Computing](https://github.com/backstreetbrogrammer/51_DistributedMongoDBAndCloudComputing?tab=readme-ov-file#chapter-05-introduction-to-cloud-computing)
6. [Create Continuous Delivery Pipeline in AWS](https://github.com/backstreetbrogrammer/51_DistributedMongoDBAndCloudComputing?tab=readme-ov-file#chapter-06-create-continuous-delivery-pipeline-in-aws)
    - [Set Up Git Repo](https://github.com/backstreetbrogrammer/51_DistributedMongoDBAndCloudComputing?tab=readme-ov-file#set-up-git-repo)
    - [Create Build Project](https://github.com/backstreetbrogrammer/51_DistributedMongoDBAndCloudComputing?tab=readme-ov-file#set-up-git-repo)
    - [Create Delivery Pipeline](https://github.com/backstreetbrogrammer/51_DistributedMongoDBAndCloudComputing?tab=readme-ov-file#set-up-git-repo)
    - [Finalize Pipeline and Test](https://github.com/backstreetbrogrammer/51_DistributedMongoDBAndCloudComputing?tab=readme-ov-file#set-up-git-repo)
    - [Clean up resources](https://github.com/backstreetbrogrammer/51_DistributedMongoDBAndCloudComputing?tab=readme-ov-file#set-up-git-repo)

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
cd <local path>
mkdir rs0-0 rs0-1 rs0-2
```

Now, we will launch our **first** `mongodb` replica instance.

```
mongod --replSet rs0 --port 27017 --bind_ip 127.0.0.1 --dbpath "<local path>\rs0-0" --oplogSize 128
```

We will launch other two `mongod` replica instances belonging to the same replica set `rs0`.

```
mongod --replSet rs0 --port 27018 --bind_ip 127.0.0.1 --dbpath "<local path>\rs0-1" --oplogSize 128
```

```
mongod --replSet rs0 --port 27019 --bind_ip 127.0.0.1 --dbpath "<local path>\rs0-2" --oplogSize 128
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

**_Simple Java App for Guidemy Online courses_**

- Application can enroll new students
- Each course will be stored in a separate `mongodb` **collection**
- Application will validate if:
    - if a new student is already enrolled
    - if the student has passed entrance test exams with minimum cutoff marks

We need to add `mongdb` driver dependency in `pom.xml`.

```
        <dependency>
            <groupId>org.mongodb</groupId>
            <artifactId>mongodb-driver</artifactId>
            <version>3.12.14</version>
        </dependency>
```

Class `GuidemyOnline`:

```java
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class GuidemyOnline {

    private static final String MONGO_DB_URL
            = "mongodb://127.0.0.1:27017,127.0.0.1:27018,127.0.0.1:27019/?replicaSet=rs0";
    private static final String DB_NAME = "guidemy-online";
    private static final double MIN_PASSING_MARKS_PERCENTAGE = 90.0D;

    public static void main(final String[] args) {
        final String courseName = args[0];
        final String studentName = args[1];
        final int age = Integer.parseInt(args[2]);
        final double testMarksPercentage = Double.parseDouble(args[3]);

        final MongoDatabase guidemyOnlineDb = connectToMongoDB(MONGO_DB_URL, DB_NAME);
        enroll(guidemyOnlineDb, courseName, studentName, age, testMarksPercentage);
    }

    private static void enroll(final MongoDatabase database, final String courseName, final String studentName,
                               final int age, final double testMarksPercentage) {
        if (!isValidCourse(database, courseName)) {
            System.err.printf("Invalid course: %s%n", courseName);
            return;
        }

        final MongoCollection<Document> courseCollection
                = database.getCollection(courseName)
                          .withWriteConcern(WriteConcern.MAJORITY)
                          .withReadPreference(ReadPreference.primaryPreferred());

        if (courseCollection.find(eq("name", studentName)).first() != null) {
            System.out.printf("Student %s has already enrolled%n", studentName);
            return;
        }

        if (testMarksPercentage < MIN_PASSING_MARKS_PERCENTAGE) {
            System.out.printf("Please give the entrance test again and get more than cutoff: [%.1f] %n",
                              MIN_PASSING_MARKS_PERCENTAGE);
            return;
        }

        courseCollection.insertOne(new Document("name", studentName)
                                           .append("age", age)
                                           .append("testMarksPercentage", testMarksPercentage));
        System.out.printf("Student [%s] was successfully enrolled in course [%s]%n", studentName, courseName);

        for (final Document document : courseCollection.find()) {
            System.out.println(document);
        }
    }

    private static boolean isValidCourse(final MongoDatabase database, final String courseName) {
        boolean isCollectionFound = false;
        for (final String collectionName : database.listCollectionNames()) {
            if (collectionName.equals(courseName)) {
                isCollectionFound = true;
                break;
            }
        }
        return isCollectionFound;
    }

    public static MongoDatabase connectToMongoDB(final String url, final String dbName) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(url));
        return mongoClient.getDatabase(dbName);
    }

}

```

Create the packaged jar: `mvn clean package`

**_Demo Run_**

a) Running the program with arguments: `Python John 25 91.5`

- Output: `Invalid course: Python`

b) Let's connect to our MongoDB primary node and then add Python course: `mongo --port 27017`

```
$ mongo --port 27017

# create new collection
rs0:PRIMARY> use guidemy-online

# add Python course
rs0:PRIMARY> db.createCollection("Python")

# verify
rs0:PRIMARY> show collections
Python
```

c) Let's rerun the program with the same arguments: `Python John 25 91.5`

- Output:

```
Student [John] was successfully enrolled in course [Python]
Document{{_id=65e8ec252853ca1558e652e7, name=John, age=25, testMarksPercentage=91.5}}
```

d) Rerun the program with the same arguments: `Python John 25 91.5`

- Output: `Student John has already enrolled`

e) Running the program with arguments: `Python Rahul 31 88.3`

- Output: `Please give the entrance test again and get more than cutoff: [90.0] `

f) Let's rerun the program with the improved entrance test marks arguments: `Python Rahul 31 94.0`

- Output:

```
Student [Rahul] was successfully enrolled in course [Python]
Document{{_id=65e8ec252853ca1558e652e7, name=John, age=25, testMarksPercentage=91.5}}
Document{{_id=65e8ed5e5c5cd95f9276c850, name=Rahul, age=31, testMarksPercentage=94.0}}
```

g) Let's shut down the mongoDB primary node and add a new student to the course: `Python Anna 21 97.5`

- Output:

```
Student [Anna] was successfully enrolled in course [Python]
Document{{_id=65e8ec252853ca1558e652e7, name=John, age=25, testMarksPercentage=91.5}}
Document{{_id=65e8ed5e5c5cd95f9276c850, name=Rahul, age=31, testMarksPercentage=94.0}}
Document{{_id=65e8eea0494d1b36b560742e, name=Anna, age=21, testMarksPercentage=97.5}}
```

Thus, we observed that even if the primary node was down, one of the two secondary nodes became primary.

Data was replicated correctly, providing fault tolerance.

---

## Chapter 04. MongoDB Sharding

Sharding allows us to scale your database to handle increased loads to a nearly unlimited degree.

It does this by increasing **read/write throughput**, and storage **capacity**.

In MongoDB, a sharded cluster consists of:

- Shards
- Mongos
- Config servers

A **shard** is a **_replica set_** that contains a subset of the cluster's data.

The `mongos` acts as a query **router** for client applications, handling both read and write operations.

It dispatches client requests to the relevant shards and aggregates the result from shards into a consistent client
response.

Clients connect to a `mongos`, not to individual shards.

**Config servers** are the authoritative source of sharding **metadata**.

The sharding metadata reflects the state and organization of the sharded data.

The metadata contains the list of sharded collections, routing information, etc.

In its simplest configuration (a single shard), a sharded cluster will look like this:

![SingleShard](SingleShard.PNG)

**_Sharding strategy_**

MongoDB supports two sharding strategies for distributing data across sharded clusters:

- Ranged Sharding
- Hashed Sharding

**Ranged sharding** divides data into **_ranges_** based on the shard key values.

Each chunk is then assigned a range based on the shard key values.

![RangedSharding](RangedSharding.PNG)

A range of shard keys whose values are **“close”** are more likely to reside on the same chunk.

This allows for targeted operations as a `mongos` can route the operations to only the shards that contain the required
data.

**Hashed Sharding** involves computing a **_hash_** of the shard key field’s value.

Each chunk is then assigned a range based on the hashed shard key values.

![HashedSharding](HashedSharding.PNG)

While a range of shard keys may be **“close”**, their hashed values are unlikely to be on the same chunk.

Data distribution based on hashed values facilitates more even data distribution, especially in data sets where the
shard key changes monotonically.

However, hashed sharding does not provide efficient range-based operations.

**_Sharding demo in local_**

- Create three folders in local:

```
cd <local-folder>
mkdir config-srv-0 config-srv-1 config-srv-2
```

- Run the local `mongod` config servers:

```
$ mongod --configsvr --replSet config-rs --dbpath "<local path>\config-srv-0" --bind_ip 127.0.0.1 --port 27020 
```

```
$ mongod --configsvr --replSet config-rs --dbpath "<local path>\config-srv-1" --bind_ip 127.0.0.1 --port 27021 
```

```
$ mongod --configsvr --replSet config-rs --dbpath "<local path>\config-srv-2" --bind_ip 127.0.0.1 --port 27022 
```

- Connect to `mongod` config server

```
$ mongo --port 27020
```

Initialize our config servers by running this command:

```
rs.initiate({
  _id: "config-rs",
  members: [
    {
      _id: 0,
      host: "127.0.0.1:27020"
    },
    {
      _id: 1,
      host: "127.0.0.1:27021"
    },
    {
      _id: 2,
      host: "127.0.0.1:27022"
    }
  ]
})
```

- Let's create our two shards:

```
cd <local-folder>
mkdir shard-0 shard-1
```

- Run these two shards:

```
$ mongod --shardsvr --port 27017 --bind_ip 127.0.0.1 --dbpath "<local path>\shard-0" --oplogSize 128
```

```
$ mongod --shardsvr --port 27018 --bind_ip 127.0.0.1 --dbpath "<local path>\shard-1" --oplogSize 128
```

- Lastly, let's run our `mongos` router:

```
$ mongos --configdb config-rs/127.0.0.1:27020,127.0.0.1:27021,127.0.0.1:27022 --bind_ip 127.0.0.1 --port 27023
```

- Let's add our shard details to `mongos`:

```
$ mongo --port 27023
```

```
mongos> sh.addShard("127.0.0.1:27017")
```

```
mongos> sh.addShard("127.0.0.1:27018")
```

- Change the chunk size:

```
mongos> use config
mongos> db.settings.save( { _id:"chunksize", value: 1 } )
```

**Video DB Demo app**

- Let's create our new database:

```
mongos> use videodb

mongos> sh.enableSharding("videodb")
```

- Insert some sample movies:

```
mongos> db.movies.insertOne({ "name": "Pulp Fiction", "directors": ["Quentin Tarantino"], "year": 1994, "cast": ["Amanda Plummer", "Samuel L. Jackson", "Bruce Willis", "John Travolta", "Uma Thurman"], "rating": 10.0 })
mongos> db.movies.insertOne({ "name": "Moana", "directors": ["Ron Clements", "John Musker"], "year": 2016, "cast": ["Aulii Wayne Johnson", "Temuera Morrison", "Rachael House", "Alan Tudyk", "Rachael House"], "rating": 9.9 })
```

We can shard the `movies` collection:

- based on `name` field, OR,
- using the **range-based** strategy.

Let's create an index first on `name` field:

```
mongos> db.movies.createIndex( { name: 1 } )

# shard the collection
mongos> sh.shardCollection("videodb.movies", { name : 1 } )

# check the status
mongos> sh.status()
```

Now, we can try the **hash-based** sharding.

- We can create a new collection: `users`

```
mongos> use videodb

# insert 2 documents
mongos> db.users.insertOne({ "user_name": "Michael Pogrey", "watched_movies": ["Moana", "Start Wars"], "favorite_genres": ["anime", "action"] })
mongos> db.users.insertOne({ "user_name": "Mary Gardener", "watched_movies": ["Dracula", "Spider Man"], "favorite_genres": ["horror", "cartoon", "action"] })

# create hash-based index
mongos> db.users.createIndex( { _id: "hashed" } )
```

- We can shard the collection

```
mongos> sh.shardCollection("videodb.users", { _id: "hashed" } )

# check the status
mongos> sh.status(true)
```

---

## Chapter 05. Introduction to Cloud Computing

**Before Cloud Computing**, if we wanted to deploy a distributed system, we had only one option:

- Buy / Rent a server room
- Buy and setup all the infrastructure ourselves or hire someone to do it
- Need a permanent staff to maintain and upgrade all the servers, routers, load balancers, etc.

**Birth of Cloud Computing**

- As all the companies (big or small) had to do the above, a few large companies came to the conclusion that
  distributed systems building blocks should be treated as a **utility**
- **Amazon (AWS), Microsoft (Azure) and Google (GCP)** first created an internal API: infrastructure API
- Then, they externalized it in a form of cloud offering called **infrastructure as a service (IAAS)**
- In addition, they launched features as **Platform as a Service (PAAS)** that provides higher level of abstraction
  for developers

**Benefits of the Cloud**

- Cloud vendors made their internal infrastructure for us available for rent on demand
- Paying just for what we use
- We can get our start-ups and running within minutes
- No need for large initial investment up front
- Cloud vendors focus on the infrastructure
- We focus on our business logic

Many cloud vendors are present now, offering hundreds of features with:

- Ease to use UIs
- High quality documentation
- Low prices
- Constantly changing products

**Geo Regions**

Amazon cloud computing resources are hosted in multiple locations world-wide.

These locations are composed of **AWS Regions**, **Availability Zones**, and **Local Zones**.

Each **AWS Region** is a separate geographic area.

Each **AWS Region** has multiple, isolated locations known as **Availability Zones**.

By using **Local Zones**, we can place resources, such as _compute_ and _storage_, in multiple locations closer to our
users.

**To summarize,**

Data replication and configuration sharing between regions is limited, but this isolation provides the following
benefits:

- Fault tolerance and stability in case of a natural disaster
- Compliance with local rules and regulations
- Security isolation
- Low latency

Data replication across regions is possible, however it:

- incurs higher costs
- slower and complex

**Cloud Infrastructure Building Blocks**

**a) Compute Nodes (VMs)**

- Examples:
    - AWS - _Elastic Cloud Compute (EC2)_
    - Microsoft Azure - _Virtual Machine_
    - GCP - _Compute Engine_
- Running on virtual machines
- Coming in different sizes and configurations

**b) Autoscaling**

- Examples:
    - AWS - _Autoscaling groups_
    - Microsoft Azure - _Virtual Machine Scale Sets_
    - GCP - _Instance Group Autoscaling_
- Allows us to intelligently and automatically adjust our compute capability
- Maintain steady performance at the lowest cost
- Run and pay just for as many compute instances as we need

**c) Load Balancers**

- Examples:
    - AWS - _Elastic Load Balancer (ELB)_
    - Microsoft Azure - _Azure Load Balancer_
    - GCP - _Google Cloud Load Balancer (GCLB)_
- Provides:
    - Unified IP Address
    - Load balancing capabilities
    - Failure detection
    - Monitoring

**d) Cloud Storage Solutions**

- Examples:
    - AWS - _Amazon Simple Storage Service (S3)_
    - Microsoft Azure - _Azure Storage_
    - GCP - _Google Cloud Storage_
- Multiple tiers:
    - Ultra economical rarely accessed storage for long-term backup
    - High QPS, low latency file storage for video, audio and app data
- All provide above 99.9% availability and durability

**e) Databases**

- SQL Databases:
    - AWS - _Amazon Relational Database Service (RDS)_
    - Microsoft Azure - _Azure SQL Database_
    - GCP - _Spanner / Cloud SQL_
- NoSQL Databases and Caching layers:
    - AWS - _DynamoDB_, _Elasticache_
    - Microsoft Azure - _Azure Cosmo DB_
    - GCP - _Bigtable / Memorystore_
- Mix of cloud and our own databases deployments

**f) Other general purpose services**

- Unified logging
- Monitoring
- Alerting
- Big Data Analytics
- Distributed Queues
- Specialized services:
    - Serverless / Functional as a services
    - Machine Learning APIs and hardware
    - Internet Of Things (IOT) services
    - Blockchain
    - Security
    - etc.

Example of AWS building blocks (subset):

![AWSBuildingBlocks](AWSBuildingBlocks.PNG)

**Global DNS and Traffic Management**

- Cloud Storage Solutions:
    - AWS - _Amazon Route 53 DNS and Traffic Flow_
    - Microsoft Azure - _Azure DNS and Traffic Manager_
    - GCP - _Google Domains and Google DNS_
- We can register our service domain:
    - Example: "www.backstreetbrogrammer-online.com"
- Route traffic to different regions based on:
    - Latency
    - Geo proximity
    - etc.

---

## Chapter 06. Create Continuous Delivery Pipeline in AWS

We will create a continuous delivery pipeline for a simple web application.

We will first use a version control system to store our source code.

Then, we will learn how to create a continuous delivery pipeline that will automatically deploy our web application
whenever our source code is updated.

Steps to create the continuous delivery pipeline:

- Set up a `GitHub` repository for the application code
- Create an `AWS Elastic Beanstalk` environment to deploy the application
- Configure `AWS CodeBuild` to build the source code from GitHub
- Use `AWS CodePipeline` to set up the continuous delivery pipeline with source, build, and deploy stages

![ApplicationArchitecture](ApplicationArchitecture.PNG)

### Set Up Git Repo

Set up a GitHub repository to store the application code.

_**Fork the starter repo**_

- Login to your GitHub account
- Navigate to repo:
  [aws-elastic-beanstalk-express-js-sample](https://github.com/aws-samples/aws-elastic-beanstalk-express-js-sample)
- Fork the repo

![ForkRepo](ForkRepo.PNG)

- Now the forked repo will be showing under your account's **Repositories**

_**Clone and push a change**_

- Open Git Bash in your local folder
- Git clone the forked repo (change <USER> with your GitHub username)

```
$ git clone https://github.com/<USER>/aws-elastic-beanstalk-express-js-sample.git
```

- Edit a file

```
$ cd aws-elastic-beanstalk-express-js-sample/

# change app.js file, for ex: print "Hello Guidemy Students!" instead of "Hello World!"
$ vi app.js
```

- Add, Commit and Push

```
$ git add .
$ git commit -m "first commit"
$ git push
```

We have created a code repository containing a simple web app.

We will be using this repository to start our continuous delivery pipeline.

It's important to set it up properly, so we push code to it.

### Deploy Web App

Create the environment where the web application will be deployed using AWS Elastic Beanstalk.

In this module, we will use the AWS Elastic Beanstalk console to create and deploy a web application.

**AWS Elastic Beanstalk** is a **compute** service that makes it easy to deploy and manage applications on AWS without
having to worry about the infrastructure that runs them.

We will use the `Create web app` wizard to create an application and launch an environment with the AWS resources needed
to run our application.

In subsequent steps, we will be using this environment and our continuous delivery pipeline to deploy the `Hello World!`
web app created in GitHub in the previous step.

**_Steps:_**

- Configure and create an AWS Elastic Beanstalk environment
- Deploy a sample web app to AWS Elastic Beanstalk
- Test the sample web app

**_Terms used:_**

**AWS Elastic Beanstalk** - A service that makes it easy to deploy our application on AWS. We simply upload our code
and Elastic Beanstalk deploys, manages, and scales our application.

**Environment** - Collection of AWS resources provisioned by Elastic Beanstalk that are used to run our application.

**EC2 instance** - Virtual server in the cloud. Elastic Beanstalk will provision one or more Amazon EC2 instances when
creating an environment.

**Web server** - Software that uses the HTTP protocol to serve content over the Internet. It is used to store, process,
and deliver web pages.

**Platform** - Combination of operating system, programming language runtime, web server, application server, and
Elastic Beanstalk components. Our application runs using the components provided by a platform.

**_Configure and create an AWS Elastic Beanstalk app_**

- In a new browser tab, open the
  [AWS Elastic Beanstalk console](https://us-west-2.console.aws.amazon.com/elasticbeanstalk/home?region=us-west-2#/welcome)
- Choose the orange `Create Application` button
- Choose `Web server environment` under the Configure environment heading
- In the text box under the heading Application name, enter `DevOpsGettingStarted`

![AWSBeanStalk](AWSBeanStalk.PNG)

- Choose the `Platform` and `Application Code` as given:

![PlatformApplicationCode](PlatformApplicationCode.PNG)

- Choose `Presets` as single instance free tier and click `Next`

![Presets](Presets.PNG)

- On the Configure service access screen, choose `Use an existing service role` for Service Role
- For EC2 instance profile dropdown list, the values displayed in this dropdown list may vary, depending on whether our
  account has previously created a new environment.
- Choose one of the following, based on the values displayed in our list:
    - If `aws-elasticbeanstalk-ec2-role` displays in the dropdown list, select it from the EC2 instance profile dropdown
      list
    - If another value displays in the list, and it’s the default EC2 instance profile intended for our environments,
      select it from the EC2 instance profile dropdown list
    - If the EC2 instance profile dropdown list doesn't list any values to choose from, expand the procedure that
      follows, **Create IAM Role for EC2 instance** profile:
        - Complete the steps in
          [Create IAM Role for EC2 instance](https://docs.aws.amazon.com/codedeploy/latest/userguide/getting-started-create-iam-instance-profile.html)
          profile to create an IAM Role that you can
          subsequently select for the EC2 instance profile. Then, return back to this step.
        - Now that we've created an IAM Role, and refreshed the list, it displays as a choice in the dropdown list.
          Select the IAM Role we just created from the EC2 instance profile dropdown list.
- Choose `Skip to Review` on the Configure service access page

![ConfigureServiceAccess](ConfigureServiceAccess.PNG)

- The `Review` page displays a summary of all our choices
- Choose `Submit` at the bottom of the page to initialize the creation of our new environment

While waiting for deployment, we should see:

- A screen that will display status messages for our environment.
- After a few minutes have passed, we will see a green banner with a checkmark at the top of the environment screen.

![BeanStalkSuccess](BeanStalkSuccess.PNG)

**_Test Our Web App_**

- To test our sample web app, select and launch the link under the name of our environment (under `Domain`)
- A new browser tab should open with a webpage congratulating us!

![BeanStalkRunning](BeanStalkRunning.PNG)

We have created an AWS Elastic Beanstalk environment and sample application.

We will be using this environment and our continuous delivery pipeline to deploy the `Hello World!` web app we created
in the previous module.

### Create Build Project

Configure and start the build process for the application using AWS CodeBuild.

In this module, we will use [AWS CodeBuild](https://aws.amazon.com/codebuild/?e=gs2020&p=cicd-three) to build the source
code previously stored in our GitHub repository.

AWS CodeBuild is a fully managed continuous integration service that compiles source code, runs tests, and produces
software packages that are ready to deploy.

**_Steps:_**

- Create a build project with AWS CodeBuild
- Set up GitHub as the source provider for a build project
- Run a build on AWS CodeBuild

**_Terms used:_**

**Build process** - Process that converts source code files into an executable software artifact. It may include the
following steps: compiling source code, running tests, and packaging software for deployment.

**Continuous integration** - Software development practice of regularly pushing changes to a hosted repository, after
which automated builds and tests are run.

**Build environment** - Represents a combination of the operating system, programming language runtime, and tools that
`CodeBuild` uses to run a build.

**Buildspec** - Collection of build commands and related settings, in `YAML` format, that `CodeBuild` uses to run a
build.

**Build Project** - Includes information about how to run a build, including where to get the source code, which build
environment to use, which build commands to run, and where to store the build output.

**OAuth** - [Open protocol](https://oauth.net/) for secure authorization. OAuth enables us to connect our GitHub account
to [third-party applications](https://docs.github.com/en/github/authenticating-to-github/authorizing-oauth-apps),
including AWS CodeBuild.

**_Configure the AWS CodeBuild project_**

- In a new browser tab, open the
  [AWS CodeBuild console](https://console.aws.amazon.com/codesuite/codebuild/start?region=us-west-2)
- Choose the orange `Create project` button
- In the Project name field, enter `Build-DevOpsGettingStarted`
- Select `GitHub` from the `Source provider` dropdown menu
- Confirm that the `Connect using OAuth` radio button is selected
- Choose the white `Connect to GitHub` button.
  A new browser tab will open asking us to give AWS CodeBuild access to our GitHub repo.
- Choose the green `Authorize aws-codesuite` button
- Enter our GitHub password
- Choose the orange `Confirm` button
- Confirm that **Managed Image** is selected
- Confirm that `Environment` parameters look like this:

![CodeBuildEnvironment](CodeBuildEnvironment.PNG)

**_Create a BuildSpec file for the project_**

- Select `Insert build commands`
- Choose `Switch to editor
- Replace the `Buildspec` in the editor with the code below:

```javascript
version: 0.2
phases:
    build:
        commands:
            - npm i --save
artifacts:
    files:
        - '**/*'
```

- Choose the orange `Create build project` button. We should now see a dashboard for our project.

![CodeBuildCreated](CodeBuildCreated.PNG)

**_Test the CodeBuild project_**

- Choose the orange `Start build` button. This will load a page to configure the build process.
- Wait for the build to complete.
  As we are waiting, we should see a green bar at the top of the page with the message `Build started`, the progress for
  our build under `Build log`, and, after a couple minutes, a green checkmark and a `Succeeded` message confirming the
  build worked.

We have created a build project on AWS CodeBuild to run the build process of the `Hello World!` web app from our GitHub
repository.

We will be using this build project as the build step in our continuous delivery pipeline, which we will create in the
next module.

### Create Delivery Pipeline

Create a pipeline to automatically build and deploy the application using AWS CodePipeline.

In this module, we will use [AWS CodePipeline](https://aws.amazon.com/codepipeline/?e=gs2020&p=cicd-four) to set up a
continuous delivery pipeline with source, build, and deploy stages.

The pipeline will detect changes in the code stored in our GitHub repository, build the source code using AWS CodeBuild,
and then deploy our application to AWS Elastic Beanstalk.

**_Steps:_**

- Set up a continuous delivery pipeline on AWS CodePipeline
- Configure a source stage using our GitHub repo
- Configure a build stage using AWS CodeBuild
- Configure a deploy stage using our AWS ElasticBeanstalk application
- Deploy the application hosted on GitHub to Elastic Beanstalk through a pipeline

**_Terms used:_**

**Continuous delivery** - Software development practice that allows developers to release software more quickly by
automating the build, test, and deploy processes.

**Pipeline** - Workflow model that describes how software changes go through the release process. Each pipeline is
made up of a series of stages.

**Stage** - Logical division of a pipeline, where actions are performed. A stage might be a build stage, where the
source code is built and tests are run. It can also be a deployment stage, where code is deployed to runtime
environments.

**Action** - Set of tasks performed in a stage of the pipeline. For example, a source action can start a pipeline
when source code is updated, and a deploy action can deploy code to a compute service like AWS Elastic Beanstalk.

**_Create a new pipeline_**

- In a browser window, open the
  [AWS CodePipeline console](https://console.aws.amazon.com/codesuite/codepipeline/start?region=us-west-2).
- Choose the orange `Create pipeline` button. A new screen will open up, so we can set up the pipeline.
- In the `Pipeline` name field, enter `Pipeline-DevOpsGettingStarted`
- Confirm that `New service role` is selected
- Choose the orange `Next` button

**_Configure the source stage_**

- Select `GitHub version 1` from the `Source provider` dropdown menu.
- Choose the white `Connect to GitHub` button. A new browser tab will open asking to give AWS CodePipeline access to our
  GitHub repo.
- Choose the green `Authorize aws-codesuite` button
  Next, we will see a green box with the message `You have successfully configured the action with the provider`
- From the `Repository` dropdown, select the repo we created in Module 1
- Select `main` from the branch dropdown menu
- Confirm that `GitHub webhooks` is selected
- Choose the orange `Next` button

**_Configure the build stage_**

- From the `Build provider` dropdown menu, select `AWS CodeBuild`
- Under `Region` confirm that the `US West (Oregon)` Region is selected
- Select `Build-DevOpsGettingStarted` under `Project name`
- Choose the orange `Next` button

**_Configure the deploy stage_**

- Select `AWS Elastic Beanstalk` from the `Deploy provider` dropdown menu
- Under `Region`, confirm that the `US West (Oregon)` Region is selected
- Select the field under `Application name` and confirm we can see the app `DevOpsGettingStarted` created in Module 2
- Select `DevOpsGettingStarted-env` from the `Environment name` text-box
- Choose the orange `Next` button. We will now see a page where we can review the pipeline configuration.
- Choose the orange `Create pipeline` button.

**_Watch first pipeline execution_**

While watching the pipeline execution, we will see a page with a green bar at the top. This page shows all the steps
defined for the pipeline and, after a few minutes, each will change from blue to green.

- Once the `Deploy` stage has switched to green and it says `Succeeded`, choose `AWS Elastic Beanstalk`. A new tab
  listing our AWS Elastic Beanstalk environments will open.
- Select the URL in the `Devopsgettingstarted-env` row. We should see a webpage with a white background and the text
  included in our GitHub commit in Module 1 => `Hello Guidemy Students!`

![PipelineStarted](PipelineStarted.PNG)

We have created a continuous delivery pipeline on AWS CodePipeline with three stages: source, build, and deploy.

The source code from the GitHub repo created in Module 1 is part of the source stage.

That source code is then built by AWS CodeBuild in the build stage.

Finally, the built code is deployed to the AWS Elastic Beanstalk environment created in Module 3.

### Finalize Pipeline and Test

Add a review stage to the pipeline and test the pipeline.

In this module, we will use [AWS CodePipeline](https://aws.amazon.com/codepipeline/?e=gs2020&p=cicd-five) to add a
review stage to our continuous delivery pipeline.

As part of this process, we can add an approval action to a stage at the point where we want the pipeline execution to
stop so someone can manually approve or reject the action.

Manual approvals are useful to have someone else review a change before deployment.

If the action is approved, the pipeline execution resumes.

If the action is rejected - or if no one approves or rejects the action within seven days - the result is the same as
the action failing, and the pipeline execution does not continue.

**_Steps:_**

- Add a review stage to our pipeline
- Manually approve a change before it is deployed

**_Terms used:_**

**Approval action** - Type of pipeline action that stops the pipeline execution until someone approves or rejects it.

**Pipeline execution** - Set of changes, such as a merged commit, released by a pipeline. Pipeline executions traverse
the pipeline stages in order. Each pipeline stage can only process one execution at a time. To do this, a stage is
locked while it processes an execution.

**Failed execution** - If an execution fails, it stops and does not completely traverse the pipeline. The pipeline
status changes to Failed and the stage that was processing the execution is unlocked. A failed execution can be retried
or replaced by a more recent execution.

**_Create review stage in pipeline_**

- Open the [AWS CodePipeline console](https://console.aws.amazon.com/codesuite/codepipeline/pipelines?region=us-west-2)
- We should see the pipeline we created in Module 4, which was called `Pipeline-DevOpsGettingStarted`. Select this
  pipeline.
- Choose the white `Edit` button near the top of the page
- Choose the white `Add stage` button between the `Build` and `Deploy` stages.
- In the `Stage name` field, enter `Review`.
- Choose the orange `Add stage` button.
- In the `Review` stage, choose the white `Add action group` button.
- Under `Action name`, enter `Manual_Review`.
- From the `Action provider` dropdown, select `Manual approval`.
- Confirm that the optional fields have been left blank.
- Choose the orange `Done` button.
- Choose the orange `Save` button at the top of the page.
- Choose the orange `Save` button to confirm the changes. We will now see our pipeline with four stages: Source, Build,
  Review, and Deploy.

**_Push a new commit to our repo_**

- Open the `app.js` file from Module 1
- Change the message in Line 5: `'Thank you Guidemy Students!'`
- Save the file
- Add, commit and push

```
$ git add .
$ git commit -m "Full pipeline test"
$ git push
```

**_Monitor the pipeline and manually approve the change_**

- Navigate to the
  [AWS CodePipeline console](https://console.aws.amazon.com/codesuite/codepipeline/pipelines?region=us-west-2)
- Select the pipeline named `Pipeline-DevOpsGettingStarted`. We should see the Source and Build stages switch from
  blue to green.
- When the `Review` stage switches to blue, choose the white `Review` button.
- Write an approval comment in the `Comments` text-box.
- Choose the orange `Approve` button.
- Wait for the `Review` and `Deploy` stages to switch to green.
- Select the AWS Elastic Beanstalk link in the `Deploy` stage. A new tab listing our Elastic Beanstalk environments will
  open.
- Select the URL in the `Devopsgettingstarted-env` row. We should see a webpage with a white background and the text we
  had in our most recent GitHub commit => `Thank you Guidemy Students!`

Congratulations! We have a fully functional continuous delivery pipeline hosted on AWS.

We have used AWS CodePipeline to add a review stage with manual approval to our continuous delivery pipeline.

Now, our code changes will have to be reviewed and approved before they are deployed to AWS Elastic Beanstalk.

### Clean up resources

**_Delete AWS Elastic Beanstalk application_**

- In a new browser window, open the
  [AWS Elastic Beanstalk Console](https://console.aws.amazon.com/elasticbeanstalk/home?region=us-west-2#/applications)
- In the left navigation menu, click on `"Applications."` We should see the `"DevOpsGettingStarted"` application listed
  under `"All applications."`
- Select the radio button next to `"DevOpsGettingStarted."`
- Click the white dropdown `"Actions"` button at the top of the page.
- Select `"Delete application"` under the dropdown menu.
- Type `"DevOpsGettingStarted"` in the text box to confirm deletion.
- Click the orange `"Delete"` button.

**_Delete pipeline in AWS CodePipeline_**

- In a new browser window, open the
  [AWS CodePipeline Console](https://console.aws.amazon.com/codesuite/codepipeline/pipelines?region=us-west-2)
- Select the radio button next to `"Pipeline-DevOpsGettingStarted."`
- Click the white `"Delete pipeline"` button at the top of the page.
- Type `"delete"` in the text box to confirm deletion.
- Click the orange `"Delete"` button.

**_Delete pipeline resources from Amazon S3 bucket_**

- In a new browser window, open the [Amazon S3 Console](https://s3.console.aws.amazon.com/s3/home?region-us-west-2)
- We should see a bucket named `"codepipeline-us-west-2"` followed by our AWS account number. Click on this bucket.
  Inside this bucket, we should see a folder named `"Pipeline-DevOpsGettingStarted."`
- Select the checkbox next to the `"Pipeline-DevOpsGettingStarted"` folder.
- Click the white `"Actions"` button from the dropdown menu.
- Select `"Delete"` under the dropdown menu.
- Click the blue `"Delete"` button.

**_Delete build project in AWS CodeBuild_**

- In a new browser window, open the
  [AWS CodeBuild Console](https://console.aws.amazon.com/codesuite/codebuild/projects?region=us-west-2)
- In the left navigation, click on `"Build projects"` under `"Build."` We should see the `"Build-DevOpsGettingStarted"`
  build project listed under `"Build project."`
- Select the radio button next to `"Build-DevOpsGettingStarted."`
- Click the white `"Delete build project"` button at the top of the page.
- Type `"delete"` in the text box to confirm deletion.
- Click the orange `"Delete"` button.

