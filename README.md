# pcd-actors
A mock system that abstract a simplified implementation of the [actor model](https://en.wikipedia.org/wiki/Actor_model). 
The system has to be considered as a mock because the main components are intentionally left abstract.

The main abstract types of the system are the following:

 * `Actor`: this type represents an actor, which can receive a message and react accordingly
 * `Message`: the message actors can send each others. A message should contain a reference to the sender actor
 * `ActorRef`: a reference to an instance of an actor. Using this abstraction it is possible to treat in the same way
   local actors and actors that execute remotely
 * `ActorSystem`: an actor system provides the utilities to create new instances of actors and to locate them

The system was intended as a mock to realize the project of the Java course I run in the bachelor-level informatics 
curriculum at the University of Padova (please, refer to 
[Programmazione Concorrente e Distribuita](http://www.math.unipd.it/~rcardin/pcd.html), in italian).

## Logical architecture 
 
All together they build the software architecture that in the figure below.

![Class diagram of the logical architecture of the pcd-actor system](http://www.math.unipd.it/~rcardin/pcd/pcd-actors/Actor%20model_1.png)

In blue are colored the interfaces of the system. in order to let the system properly working, every interface MUST have 
at least a concrete implementation. In green are colored the type that have to be implemented / extended / completed.
 
It follows a brief description of each of the main logical types of `pcd-actors`.

### Actor
An actor belonging to the type `Actor` holds the "interface" of the actor. The interface of an actor is identified by
the message it can respond to. The actor interface is fully defined by the method

    void receive(T message) 

Messages received by an actor are not immediately processed. They must be placed inside a dedicated queue, called 
**mail box**. Messages inside mail box have to be processed *asynchronously*, which means that the processing of a 
message has not to block the receiving loop of other messages by the actor.
  
The implementation of the actor must optimize the use of synchronized threads to satisfy the above requirements.

An actor has an actor reference (see the below type `ActorRef`) to itself and to the sender of the current processed
message.

#### Unknown messages

In the simple implementation requested by `pcd-actors`, if an actor does not know how to respond to a particular message 
type, an `UnsupportedMessageException` is thrown. This is not the standard behaviour of an actor model. In a full
implementation of an actor model it should be a responsibility of the user to decide which action to take with respect
to an unknown message.

Moreover, the policy that let us thrown an exception in response to an unknown message is possible because in 
`pcd-actors` an actor cannot change its interface through time. Actually, throwing an exception will stop the actor,
making useless any possible change of interface.
 
### ActorRef
A reference to an actor (formally an `ActorRef`) is an abstraction of the model used to address actors. There are two
different modes to address actors:

 * Local mode: the actor is running in the local machine
 * Remote mode: the actor may be running in a remote machine
 
Using this abstraction a remote actor can be used as a local actor, simplify the model of processing. (**WARNING: This 
feature is not requested anymore**)
 
Once an instance of `ActorRef` was obtained, it is possible to send a messages to the corresponding actor using the 
following method:
 
    void send(T message, ActorRef to);

To do the magic, it is necessary to use the instance of `ActorSystem` described below. Messages can be sent only among 
actors. No other type can send a message to an actor.

#### Actor reference for testing purpose

For *testing purpose*, it is necessary to give the possibility to retrieve the `Actor` associated to a reference. For 
this reason, among the `test` types it's present the class `TestActorRef`. This class is a 
[*decorator*](http://www.slideshare.net/RiccardoCardin/design-pattern-strutturali) of the `ActorRef`
type, that adds a single method:
    
    protected abstract Actor<T> getUnderlyingActor(ActorSystem system);
    
Using this method it is possible to retrieve the corresponding `Actor`. The above method **must be implemented**.

### Message
A `Message` is the piece of information that actor send among each others. Each message should be logically divided into
three parts:

 * A *tag*, which represents the operation requested by the message
 * A *target*, which represents the address of the actor receiving the message
 * A *payload*, which may represent the data that have to be sent with the message
 
![Graphical representation of the structure of a message](http://www.math.unipd.it/~rcardin/pcd/pcd-actors/Message%20structure.png)

### Actor system
The actor system (`ActorSystem`) has the responsibility to maintain reference to each actor created. Using the actor
system should be the only way to build a new instance of an actor. The factory methods exposed by the `ActorSystem` type
are:

    ActorRef<? extends Message> actorOf(Class<Actor<?>> actor);
    ActorRef<? extends Message> actorOf(Class<Actor<?>> actor, ActorMode mode);
    
The former lets to build a local instance of an actor of the given type. The latter lets to decide if a local instance
or a remote instance has to be built.

The actor system maintain the relationship between each actor and its reference, using a map. The map is indexed by
`ActorRef` and it is located inside the `AbsActorSystem` type. Accesses to the map have to be properly synchronized.

The actor system has also the responsibility to stop an actor and to stop the entire system, using the following 
methods:

    void stop();
    void stop(ActorRef<?> actor);

Stopping an actor means that it cannot receive any message after the stopping operation. This operation must be accomplished
*gracefully*, which means that an actor has to process the messages that are already present in the mailbox before
stopping.

Trying to do any operation on a stopped actor must rise an `UnsupportedMessageException`. An actually stopped actor 
should be eligible for garbage collection by the JVM an no thread should be associated to it anymore.

The `stop` method stops all the actors that are active in the actor system. Every actor has to be stopped *gracefully*,
as stated in above sentences.
    
#### Singleton view of the actor system
The actor system MUST have a single active instance. This instance have to be necessarily initialized in the `main` 
method of the program.

In order to implement correctly the remote system, this instance have to be serializable. The best way to achieve this 
functionality is to use a *dependence injection* framework, such as [Google Guice](https://github.com/google/guice), 
[Spring](http://projects.spring.io/spring-framework/) or [CDI](http://docs.oracle.com/javaee/6/tutorial/doc/giwhl.html). 
However, the use of an DI framework is far beyond the scopes of this little project. 

So, the above property must be fulfilled using other techniques, that do not use explicitly any form of design pattern
Singleton

### Type's interactions

This section shows how the above types interact with each other to fulfill the relative functionality.
 
#### Actor creation 
To create a new actor, ask the actor system to do the dirty job.
 
![Actor creation](http://www.math.unipd.it/~rcardin/pcd/pcd-actors/Actor%20creation.png)

So, first of all, a client must obtain a reference to the actor system. Using this reference, it asks the system to 
create an new instance of an actor. The result of this request is the actor reference to the actor.

#### Message sending

Once a client have obtained the references to two actors it can ask the first to send a message to the second. Clearly,
to obtain the real instance of an actor (not its actor reference) the actor system must be queried.

![Message sending](http://www.math.unipd.it/~rcardin/pcd/pcd-actors/Message%20sending_1.png)

Clearly, the `ActorRef` cannot be directly responsible of the `receive` method call on an `Actor`. The responsibility of
an `ActorRef` is managing to let a `Message` to be put inside the `Actor`'s mailbox.

Most of time, the client will be an actor itself, that ask to the self reference to send a message to another actor.

## Building

The `pcd-actors` project is configured as a [Maven](https://maven.apache.org/) project. In detail, it was generated using the following command line:
 
    mvn archetype:generate -DarchetypeGroupId=it.unipd.math.pcd.actors -DarchetypeArtifactId=pcd-actors -DarchetypeVersion=1.0-SNAPSHOT

The folder tree generate is the following:

    project
    |-- pom.xml
    `-- src
        |-- main
        |   `-- java
        |       `-- App.java
        `-- test
            `-- java
                `-- AppTest.java

As usual, put the source files under the folder called `scr/main/java`; Put the test files (unit / integration) under
the folder `scr/test/java`.

To build the actor system library use the following command

    $ mvn package
    
The output library will be created by Maven inside the folder `target`, with name `pcd-actors.jar`. 

To run the tests use the command

    $ mvn test
     
The output of the console will tell you if the build and the test processes have finished correctly.  

## Testing

Testing of each entity is done with [JUnit 4](http://junit.org/). As `pcd-actors` is a Maven project, tests are located
in the `scr/test/java` folder. Integration tests will be added in the next weeks. These tests aim to verify that the
whole system satisfies above requirements.

You're free (which means that you're expected) to add your own tests to your implementation of the actor system.

### Create an instance of `ActorSystem`
Tests need to create an instance of a concrete class that implements `ActorSystem`. Using the current architecture of
`pcd-actors`, it is not possible to know which is the concrete class *a priori*. One solution to instantiate an object of 
a concrete implementation of `ActorSystem` is using *reflection* mechanism. 

In detail, the class `ActorSystemFactory` scans the classpath searching for all subtypes of class `AbsActorSystem`. 
Through the method `buildActorSystem` it builds an instance of the first class found.
 
    ActorSystemFactory.buildActorSystem()
 
To accomplish this need an external library is used. Such library is [`org.reflections`](https://github.com/ronmamo/reflections).
The library is added as dependency to the project only during testing process. Then, it cannot be used main code. 

    <dependency>
        <groupId>org.reflections</groupId>
        <artifactId>reflections</artifactId>
        <version>0.9.10</version>
        <scope>test</scope>
    </dependency>

## F.A.Q.

> **Q**: The meta framework contains four interfaces and two abstract classes. What should I have to implement?

If you think about the testing process, you will sure understand that tests cannot be run on types that do not exist in
the original process. Then, you're not expected to implement the `Message` interface, nor to give a concrete 
implementation of `AbsActor`.

> **Q**: Implementations should create abstract or concrete type?

It depends: a user of `pcd-actors` has not to give an implementation of `ActorSystem`, then it is up to you to give a 
full implementation of this type. Also `ActorRef` needs at least two implementation: one that refers to local actors,
and one that refers to remote actors.

> **Q**: We have to implement some synchronized data structures. `Actor` and `ActorSystem` have to be implemented has
  thread their own?
  
No, they don't. These two types probably generates many threads to accomplish to their scope, but they are not intended
to be thread on their own.

> **Q**: `ActorSystem` is a Singleton?

In an idyllic and perfect world we would used a *dependency injection* framework, to guarantee that `ActorSystem` will be
instantiated only once. Unfortunately, we can't use such frameworks, due to their complexity. You can't implement 
`ActorSystem` as a Singleton neither. Then, you have to be sure that every object that has to use an instance of 
`ActorSystem` will use the same one.

> **Q**: Can a user of `pcd-actors` extend `ActorSystem`?

No, she definitely can not

> **Q**: How will `ActorRef` interact with the rest of the system? Which is its role?

`ActorRef` decouples the implementation of an actor from the way it is invoked by another actor. Then, there will be
at least two implementation of this type: one that refers to local actors, and one that refers to remote actors. Its
interaction with the rest of system is summarized in the following sequence diagram:

![Message sending](http://www.math.unipd.it/~rcardin/pcd/pcd-actors/Message%20sending_1.png)

> **Q**: The logical view that was given of the `Message` type is equal to its physical view (a.k.a. implementation), 
  isn't it?
    
No, the logical view was given to describe the generic actor model. In `pcd-actors` `Message`s are completely free of
implementation, from a library point of view.

> **Q**: How does an `Actor` instance acquire the reference to the sender `ActorRef`

You don't have to use the `Message` to implement this feature.

> **Q**: A user of `pcd-actors` can use only the methods of the interfaces that are defined in the meta-framework, 
  can't she?
  
Definitely yes. You can add which ever method you desire, but these will not be tested and valuated.

> **Q**: Why can we send only messages of type `Message` to actors?

This is a simplification. This way, we can define a custom protocol of communication among actors that is the same in
all the implementations of the library and relies on the object oriented principles.

> **Q**: If the meta-framework gives only the abstract class `AbsActorSystem`, how the hell will the tests to use the
  library?
  
This is a good question. I think I will use some reflection to discover which of your classes extends `AbsActorSystem`.

> **Q**: Do we have to implement the `receive` method of the type `Actor` for the project? 

No, you don't. That is the hook that a user of `pcd-actors` have to implement to use the library.

> **Q**: A client that intends to use both local and remote actors might interacts with them in the same way, 
  doesn't she?
  
Local and remote actors have to expose the same interface to a client.

> **Q**: How can I guess when the actor have to process the next message?

This is the focal point of the project and I can't tell you how to implement this feature ;)

> **Q**: Can a `Message` contain some logic or is it a simple placeholder that tells an actor what to do?
  
As previously stated a `Message` must not have any implementation. Refer to 
[Akka/Java: Handling multiple message types inside a custom actor?](http://stackoverflow.com/questions/25917613/akka-java-handling-multiple-message-types-inside-a-custom-actor)
on Stackoverflow to understand why.

> **Q**: An `Actor` is an immutable object? Can an `Actor` change its interface?

An actor can change it's interface in the original actor model. In `pcd-actors` we can operate a simplification and **we
don't allow an actor to change its interface during time**. An actor has an internal state that changes during time. So
it cannot be consider as an immutable object.

> **Q**: An actor that sends a message to another actor have to receive back an ack/nack message, doesn't she?

Nope. We do not want to implement any predefined protocol of communication between actors. It will be up to the end-user
to define a protocol such that.

> **Q**: Can we use some external framework to implement the meta-framework, such as Spring?

I prefer that you don't add any additional framework to the original project. The only framework you can use is 
[Mockito](http://mockito.org/) during unit test process

> **Q**: Can we use external tools to check the style of the code we will produce?

Yes, you can (and you should do too). 

> **Q**: Can we add some methods to the given classes? 

Yes, sure. You can add whatever method you think it could help. But, be aware: Do not modify the public interface of 
these types because unit tests cannot rely on your custom interface.

> **Q**: Should an `ActorRef` call directly the method `receive` of the corresponding `Actor`?
 
Using the current architecture it is not possible. First of all, a `Message` has to be put into the `Actor`'s mailbox. 
Then the `Message` becomes eligible for elaboration.

## License

The MIT License (MIT)

Copyright (c) 2015 Riccardo Cardin

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
documentation files (the "Software"), to deal in the Software without restriction, including without limitation the 
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit 
persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the 
Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

