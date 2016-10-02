/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Riccardo Cardin
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p/>
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */

/**
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A map-based implementation of the actor system.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public abstract class AbsActorSystem implements ActorSystem {

    /**
     * Associates every Actor created with an identifier.
     */
    private Map<ActorRef<?>, ActorInfo> actors;

    /**
     * The executor that manage threads associated to actors
     */
    private ExecutorService service;

    public AbsActorSystem() {
        // For now, let the map be synchronized
        this.actors = Collections.synchronizedMap(new HashMap<ActorRef<?>, ActorInfo>());
        // Build the executor service
        // XXX Is it the right policy to use?
        this.service = Executors.newCachedThreadPool();
    }

    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor, ActorMode mode) {

        // ActorRef instance
        ActorRef<?> reference;
        try {
            // Create the reference to the actor
            reference = this.createActorReference(mode);
            // Create the new instance of the actor and set its actor system
            AbsActor<?> actorInstance = ((AbsActor) actor.newInstance()).setActorSystem(this).setSelf(reference);
            // Start the actor
            ActorInfo info = startActor(actorInstance);
            // Associate the reference to the actor
            actors.put(reference, info);

        } catch (InstantiationException | IllegalAccessException e) {
            throw new NoSuchActorException(e);
        }
        return reference;
    }

    /**
     * Starts a task for the {@code actor} and returns information to
     * control the execution
     *
     * @param actor
     *
     * @return Information to control actor execution
     */
    private ActorInfo startActor(AbsActor<?> actor) {
        AbsActor.ActorRunnable task = actor.getTask();
        Future<?> future = this.service.submit(task);
        return new ActorInfo(actor, future);
    }

    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor) {
        return this.actorOf(actor, ActorMode.LOCAL);
    }

    /**
     * Returns the actor implementation that is associated to {@code ref}.
     *
     * @param ref A reference to an actor
     * @return An actor implementation
     *
     * @throws NoSuchActorException If {@code ref} is not associated to any actor implementation
     */
    Actor<? extends Message> findActor(ActorRef<? extends Message> ref) {
        return find(ref).getActor();
    }

    /**
     * Return execution information about {@code ref}.
     *
     * @param ref The reference to the actor
     * @return Execution information about {@code ref}
     *
     * @throws NoSuchActorException If {@code ref} is not associated to any actor implementation
     */
    private ActorInfo find(ActorRef<? extends Message> ref) {
        ActorInfo info = this.actors.get(ref);
        if (info == null) {
            throw new NoSuchActorException("No actor found with reference " + ref.hashCode());
        }
        return info;
    }

    protected abstract ActorRef createActorReference(ActorMode mode);

    @Override
    public void stop(ActorRef<?> actor) {
        // Stopping an actor means to remove it from the map. This guarantees that no
        // new message will be sent to the actor
        ActorInfo info = this.find(actor);
        // Also, the actor task must be stopped
        info.stop();
        this.actors.remove(actor);
    }

    @Override
    public void stop() {
        // Stop each actor
        Iterator<ActorRef<?>> it = this.actors.keySet().iterator();
        while (it.hasNext()) {
            stop(it.next());
            it = this.actors.keySet().iterator();
        }
        this.actors.clear();
    }

    class ActorInfo {
        private Actor<?> actor;
        private Future<?> future;

        public ActorInfo(Actor<?> actor, Future<?> future) {
            this.actor = actor;
            this.future = future;
        }

        public Actor<?> getActor() {
            return actor;
        }

        /**
         * Tries to cancel the future associated to actor's execution
         */
        public void stop() {
            this.future.cancel(true);
        }
    }
}