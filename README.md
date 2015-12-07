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
    
Messages received by an actor are not immediately processed. They must be placed inside a dedicated queue, called 
**mail box**. Messages inside mail box have to be processed *asynchronously*, which means that the processing of a 
message has not to block the receiving loop of other messages by the actor.
  
The implementation of the actor must optimize the use of synchronized threads to satisfy the above requirements.
 
### ActorRef


