# pcd-actors
A mock system that abstract a simplified implementation of the [actor model](https://en.wikipedia.org/wiki/Actor_model). 
The system has to be considered as a mock because the main components are intentionally left abstract.

The main abstract types of the system are the following:

 * `Actor`: this type represents an actor, which can receive a message and react accordingly
 * `Message`: the message actors can send each others. A message should contain a reference to the sender actor
 * `ActorRef`: a reference to an instance of an actor. Using this abstraction it is possible to treat in the same way
   local actors and actors that execute remotely
 * `ActorSystem`: an actor system provides the utilities to create new instances of actors and to locate them
 
All together they build the software architecture that in the figure below.

![Class diagram of the logical architecture of the pcd-actor system](http://www.math.unipd.it/~rcardin/pcd/pcd-actors/Actor%20model.png)

