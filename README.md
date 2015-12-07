# pcd-actors
A mock system that abstract a simplified implementation of the [actor model](https://en.wikipedia.org/wiki/Actor_model). 
The system has to be considered as a mock because the main components are intentionally left abstract.

The main abstract types of the system are the following:

 * `Actor`: this type represents an actor, which can receive a message and react accordingly
 * `Message`: the message actors can send each others. A message should contain a reference to the sender actor
 * `ActorRef`: a reference to an instance of an actor. Using this abstraction it is possible to treat in the same way
   local actors and actors that execute remotely
 * `ActorSystem`: an actor system provides the utilities to create new instances of actors and to locate them

## Logical architecture 
 
All together they build the software architecture that in the figure below.

![Class diagram of the logical architecture of the pcd-actor system](http://www.math.unipd.it/~rcardin/pcd/pcd-actors/Actor%20model.png)

In blue are colored the interfaces of the system. in order to let the system properly working, every interface MUST have 
at least a concrete implementation. In green are colored the type that have to be implemented / extended / completed.
 
It follows a brief description of each of the main logical types of `pcd-actors`.

### Actor
An actor belonging to the type `Actor` holds the "interface" of the actor. The interface of an actor is identified by
the message it can respond to. The actor interface is fully defined by the method

    void receive(T message)
    
If an actor does not know ho to respond to a particular message type, an `UnsupportedMessageException` is thrown. 

Messages received by an actor are not immediately processed. They must be placed inside a dedicated queue, called 
**mail box**. Messages inside mail box have to be processed *asynchronously*, which means that the processing of a 
message has not to block the receiving loop of other messages by the actor.
  
The implementation of the actor must optimize the use of synchronized threads to satisfy the above requirements.

An actor has an actor reference (see the below type `ActorRef`) to itself and to the sender of the current processed
message.
 
### ActorRef
A reference to an actor (formally an `ActorRef`) is an abstraction of the model used to address actors. There are two
different modes to address actors:

 * Local mode: the actor is running in the local machine
 * Remote mode: the actor may be running in a remote machine
 
Using this abstraction a remote actor can be used as a local actor, simplify the model of processing.
 
Once an instance of `ActorRef` was obtained, it is possible to send a messages to the corresponding actor using the 
following method:
 
    void send(T message, ActorRef to);

To do the magic, it is necessary to use the instance of `ActorSystem` described below. Messages can be sent only among 
actors. No other type can send a message to an actor.

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
    
#### Singleton view of the actor system
The actor system MUST have a single active instance. In order to implement correctly the remote system, this instance 
have to be serializable. The best way to achieve this functionality is to use a *dependence injection* framework, such
as [Google Guice](https://github.com/google/guice), [Spring](http://projects.spring.io/spring-framework/) or 
[CDI](http://docs.oracle.com/javaee/6/tutorial/doc/giwhl.html). However, the use of an DI framework is far beyond the 
scopes of this little project. 

Then, the actor system is implemented as a Singleton by the `ActorSystemImpl` concrete type. The chosen Singleton 
implementation is the static-not lazy implementation.

### Type interactions

This section shows how the above types interact with each other to fulfill the relative functionality.
 
#### Actor creation 
To create a new actor, ask the actor system to do the dirty job.
 
![Actor creation](http://www.math.unipd.it/~rcardin/pcd/pcd-actors/Actor%20creation.png)

So, first of all, a client must obtain a reference to the actor system. Using this reference, it asks the system to 
create an new instance of an actor. The result of this request is the actor reference to the actor.

#### Message sending

Once a client have obtained the references to two actors it can ask the first to send a message to the second. Clearly,
to obtain the real instance of an actor (not its actor reference) the actor system must be queried.

![Message sending](http://www.math.unipd.it/~rcardin/pcd/pcd-actors/Message%20sending.png)

Most of time, the client will be an actor itself, that ask to the self reference to send a message to another actor.