# Theory Questions Distributed Systems Technologies 2023S

## 1.6.1. Annotation vs. XML Declarations

In the previous tasks you already gained some experiences using annotations and XML. What are the benefits and drawbacks
of each approach? In what situations would you use which one? Hint: Think about maintainability and the different roles
usually involved in software projects.

**Solution**

Advantages:

- Seperation of relationships and all logic
- More readable for complex definitions

Disadvantages:

- A lot less ressources online
- More verbose APIs for often quite simple definitions
- Two seperate files to define a Table, which do need to be changed in
  lockstep to make sense (less maintable)
- Less obvious where the relationships are defined (you need to read the
  pom.xml config)

I would almost always opt for the annotations, except for in projects where there
is a DB Architect. But even than I as a developer might still write the files
and the expert might only define the schema.
Often I might start out with annotations and later change to XML if the
relations become to complex to be readable in Java Annotations.

## 1.6.2. Entity Manager and Entity Lifecycle

What is the lifecycle of a JPA entity, i.e., what are the different states an entity can be in? What EntityManager
operations change the state of an entity? How and when are changes to entities propagated to the database?

**Solution**

1) New: A new entity is created and it is not yet associated to the DB in any
   way.
2) Managed: The new entity was submitted to the DB (with something like
   `EntityManager.persist()`). Any changes that are now made to the entity will
   be stored send to the DB.
3) Detached: The entity is now no longer connected to the DB, so all changes
   made to it won't be communicated to the DB (with something like
   `EntityManager.detach()`)
4) Removed: The item was removed from the DB
   (with something like `EntityManager.remove()`)

## 1.6.3. Optimistic vs. Pessimistic Locking

The database systems you have used in this assignment provide different types of concurrency control mechanisms. Redis,
for example, provides the concept of optimistic locks. The JPA EntityManager allows one to set a pessimistic read/write
lock on individual objects. What are the main differences between these locking mechanisms? In what situations or use
cases would you employ them? Think of problems that can arise when using the wrong locking mechanism for these use
cases.

**Solution**

**Pesemistic Locking** requires the user to set a and hold a lock while they are
reading and modifying data on an item.

**Optimistic Locking** doesn't provide explicit locks. A user simply reads, and
modifies an item. The modification however can fail if in the item was
changed by another actor since the reading. In which case the whole transaction
fails and has to be tried again.

Pesemistic Locking can lead to poor performance, but might be prefered in
scenarios which many overlapping locks which is a worst-case scenario for
optimistic locking.

## 1.6.4. Database Scalability

How can we address system growth, i.e., increased data volume and query operations, in databases? Hint: vertical vs.
horizontal scaling. What methods in particular do MongoDB and Redis provide to support scalability?

**Solution**

Like most services, databases can scale vertically. However there is a limit at
which point, throwing more storage, cpu, ram won't increase performance.

Luckily, many databasses also support horizontal scaling (not all, especially
not in memory databases like SQLite or H2). MongoDB and Redis have two
horizontal scaling approaches, replication and partitioning.

**Replication** can help with many (read) query operations as the data is stored
on many replicants which all can proccess read queries. However, it does not
help with many write queries or increased data volume.

**Partitioning** also known as Sharding, splits the data over multiple servers,
so that each server only needs to hold a subset. This helps with increased
data volume and write queries. But it might happen that a complex read query
needs to be run on all partitions and can therefore increase CPU utilization.

MongoDB also supports sharding witg replication which can address all problems
by combining both solutions.

## 2.4.1. Java Transaction API

Consider the match method and how you would handle things different if you were
to implement it with pessimistic or optimistic locking.

**Solution**

With optimistic locks the whole match would have been reexecuted with on
conflicts. While I implemented it with optimistic locks we should have
implemented it with pesimistcs to improve performance.

## 2.4.2. Remoting Technologies

Compare gRPC remoting and Web services. When would you use one technology, and
when the other? Is one of them strictly superior? How do these technologies
relate to other remoting technologies that you might know from other lectures
(e.g., Java RMI, or even socket programming)?

**Solution**

gRPC seams a lot simpler, as it is clearly designed for calling functions on
another machine. With Webservices you need to check, HTTP-Status codes, should
verify the content header, the service itself should check your content accept
header.

For services that need to communicate, I would prefer gRPC simply because of
their simplicity. Compared to Java RMI it also has the big advantage that it
isn't limiting to a single language (well, to the JVM).

For public facing APIs I would prefer Web Services (REST-ish), because even
though it is not as simple, I know that the consumers can choose almost any
programming language since they all can perform requests and can parse JSON.

If it is avoidable, I will avoid socket programming. From experience, it forces
you to think about many low level aspects that are often irrelevant to your
application. Maybe if latency is a extremely critical aspect of the application
and it is currently bottlenecked by the network.

## 2.4.3. Class Loading

Explain the concept of class loading in Java. What different types of class
loaders exist and how do they relate to each other? How is a class identified
in this process? What are the reasons for developers to write their own class
loaders?

**Solution**

Class loading is the process of loading compiled Java classes into the JVM.

There are three class loaders that come with Java:

1) Bootstrap Class Loader: Loads internal classes like `java.lang*`.
2) Extension Class Loader: Loads the JDK extensions.
3) System Class Loader: Loads classes from the current classpath.

Java class names are preserved in the *.jar and there fore the classloader can
identify all classes in the jar.

There are multiple reasons why one might want to implement their own class
loader:

- Modify existing classes (like we did with javaassist)
- Creating classes dynamically
- Loading different versions of the same class, depending on its usecase.

## 2.4.4. Weaving Times in AspectJ

What happens during weaving in AOP? At what times can weaving happen in
AspectJ? Think about advantages and disadvantages of different weaving times.

**Solution**

Weaving is when the defined aspects get "woven" into their target classes.
There are three ways this can happen, during compile time, post-comile time
and load time.

Compile time weaving is the first choice if the source code for the target is
given and the aspect is known at compile time. Because the AspectJ compiler
can rewrite the target classes here, this leads to the best performance.

Post compile time weaving also runs during compilation but here the source code
can be unknown and it is possible to weave directly into the java bytecode.
This can be handy for libraries, when only a jar is provided.

Load time weaving happens during runtime and can therefore modify already running
programs which can be utialized for plugins.

## 3.4.1. Message-Oriented Middleware Comparison

Message-Oriented Middleware (MOM), such as RabbitMQ, is an important technology
to facilitate messaging in distributed systems. There are many different types
and implementations of MOM. For example, the Java Message Service (JMS) is part
of the Java EE specification. Compare JMS to the Advanced Message Queuing
Protocol (AMQP) used by RabbitMQ. How are JMS and AMQP comparable? What are
the differences? When is it useful to use JMS?

**Solution**

Differences include:
- JMS only defines an API, but leaves the implementation open, while AMQP 
defines the exact layout of how bytes are sent over the wire. This also means 
that the same implementation might be needed to be used on server on client while
AMQP doesn't have such requirements.
- AMQP doesn't have a standard API, so switching clients can be quite expensive.
- JMS is quite a bit simpler as it only supports point-to-oint and 
publish-subscribe modes. AMQP on the other hand has exchanges, different modes 
like, `fanout`, `direct` or `topic`.
- AMQP can be used to implement the JMS API, as there are quite a few JMS clients
https://www.rabbitmq.com/jms-client.html.

In general, both technologies can be used to implement a message queue. 

I cannot think of a reason to use JMS over AMQP, the API might be simpler but
since we have used AMQP in the lecture I haven't had any problems with the API.

## 3.4.2. Messaging Patterns

Describe the different messaging patterns that can be implemented with RabbitMQ.
Also, describe for each pattern:

- a) a use cases where you would use the pattern (and why)
- b) an alternative technology that also allows you to implement this pattern.

**Solution**

**Work queue**

A single queue is used to publish and receive messages. On both ends multpile 
producers and consumers are allowed however a message will only be send to 
a single consumer. This might be useful for long running tasks like OCR on 
documents.

This could also be implemented with JMS. 

**Publish Subscribe**

A producer sends messages and multiple receiver can subscribe to that producer, 
where each consumer has their own queue. In RabbitMQ we would implement that
with an exchange and the `fanout` mode. A possible usecase is a messaging app 
where we the user need to subscribe to a chat to receive its messages.

In the past I have ~misused~ creatively utilized a Postgres Database to archive
the same but that approach greatly limits the available connections.

**Routing**

This is conceptually similar to publish subscribe but each subscriber can set
to which type of messages they want to listen to. In RabbitMQ we would implement
that with the `direct` mode. This might be useful in a monitoring environment 
where muliple logger are subscribed to error messages. Some logger might want to 
listen to all errors to write them to the log file and others might only be 
interested in high-severity failures, like a call robot to wake up the 
on-call developer.

In general most of these patterns can also be created in other message brokers 
that implement the AMQP like Apache Qpid (the original AMQP broker 
implementation).

**Topic**

Topic is even more advanced than routing and the queues can run a matching 
on the topic key. For example the keys of queues are like `eu.austria.vienna`, 
`eu.austria.linz`, `us.california.berkley` than just with routing it is 
possible to just get a simple city but sometimes it might be handy to get all 
requests from a country and that would be possible for austria with 
`eu.austria.`.

Even though it would be less reliable, more work and a lot more maintanance we 
__could__ roll our own solution with grpc and a lot of engineering. First the 
cosumer would need to register at the producer to get discoverd. Next the 
producer would send all messages to all consumers which would first filter them
according to their match algorithms and then store them themself in a queue.




## 3.4.3. Container vs. Virtual Machines

Explain the differences between container-based virtualization (in particular
Docker) and Virtual Machines. What are the benefits of container over VMs and
vice versa?

**Solution**

Containers are implemented with kernel-level isolation (with namespaces). 
That means that the host kernel is reused for the containers, but can isolate 
the processes into thinking that they run on their own machine.

Overall this results in less overhead as many resources can be shared, and 
the isolation can be turned of where needed, for example for servers it can 
be quite handy to allow the container to run on the same network.

Containers, however are only native on Linux, other operating systems need to 
virtualize at least one Linux kernel to run a container.

VMs provide better isolation, which comes with a greater performance impact. 
For example, trying another operating system with a different kernel is not 
possible in containers.

## 3.4.4. Scalability of Stateful Stream Processing Operators

A key mechanism to horizontally scale stream processing topologies is
auto-parallelization, i.e., identifying regions in the data flow that can be
executed in parallel, potentially on different machines. How do key-based
aggregations, windows or other stateful operators affect the ability for
parallelization? What challenges arise when scaling out such operators?

**Solution**

Key based aggregation first needs to group the data by key. Than each key can 
only be processed on a single node to verify correctness.

