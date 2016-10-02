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

import java.util.*;
import java.util.concurrent.ThreadFactory;

/**
 * Defines common properties of all actors.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public abstract class AbsActor<T extends Message> implements Actor<T> {

    /**
     * The mailbox that contains the messages that have to be elaborated
     */
    private Queue<Packet<T>> mailbox;

    /**
     * Self-reference of the actor
     */
    protected ActorRef<T> self;

    /**
     * Sender of the current message
     */
    protected ActorRef<T> sender;

    /**
     * The actor system
     */
    private ActorSystem system;

    private ActorRunnable task;

    public AbsActor() {
        // Initializing the list in a synchronized way
        this.mailbox = (Queue<Packet<T>>) new LinkedList<Packet<T>>();
        this.task = new ActorRunnable();
    }

    /**
     * Sets the self-referece.
     *
     * @param self The reference to itself
     * @return The actor
     */
    protected final AbsActor<T> setSelf(ActorRef<T> self) {
        this.self = self;
        return this;
    }

    /**
     * Sets the actor system.
     *
     * @param system The actor system
     * @return The actor
     */
    protected final AbsActor<T> setActorSystem(ActorSystem system) {
        this.system = system;
        return this;
    }

    /**
     * Puts the {@code message} inside the mailbox of the actor.
     *
     * @param message The message that has to be elaborated
     */
    protected final synchronized void send(T message, ActorRef<T> sender) {
        this.mailbox.add(new Packet<T>(message, sender));
    }

    /**
     * Returns the next message in the mailbox
     *
     * @return A message.
     *
     * @throws java.util.NoSuchElementException If the mailbox is empty.
     */
    protected final synchronized Packet<T> nextMessage() {
        return this.mailbox.remove();
    }

    protected ActorRunnable getTask() {
        return this.task;
    }

    /**
     * Returns {@code true} if the mailbox is empty, {@code false} otherwise.
     *
     * @return {@code true} if the mailbox is empty
     */
    protected  final synchronized boolean isMailboxEmpty() {
        return this.mailbox.isEmpty();
    }

    // TODO Think if the type ActorRunnable should be represented as an internal class of Actor.
    protected final class ActorRunnable implements Runnable {

        @Override
        public void run() {
            // Until the mailbox is empty and the thread is not been interrupted
            while (!Thread.currentThread().isInterrupted() || !isMailboxEmpty()) {
                try {
                    Packet<T> packet = nextMessage();
                    // Retrieve the sender of the message
                    sender = packet.getSender();
                    receive(packet.getMessage());
                } catch (NoSuchElementException nsee) {
                    // The mailbox is empty: this is not an error
                    // TODO Log something
                }
            }   // while (true)
        }
    }

    /**
     * Associates each message to the actor reference that sent it.
     *
     * @param <T> A type of message.
     */
    class Packet<T extends Message> {
        private final T message;
        private final ActorRef<T> sender;

        public Packet(T message, ActorRef<T> sender) {
            this.message = message;
            this.sender = sender;
        }

        public T getMessage() {
            return message;
        }

        public ActorRef<T> getSender() {
            return sender;
        }
    }
}
