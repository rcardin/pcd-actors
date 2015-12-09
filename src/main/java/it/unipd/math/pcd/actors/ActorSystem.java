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

/**
 * The system of actors. Using the system it is possible to:
 * <ul>
 *     <li>Create a new instance of an actor</li>
 *     <li>Stopping an actor</li>
 * </ul>
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public interface ActorSystem {

    /**
     * Create an instance of {@code actor} returning a {@link ActorRef reference} to it using the given {@code mode}.
     *
     * @param actor The type of actor that has to be created
     * @param mode The mode of the actor requested
     *
     * @return A reference to the actor
     */
    ActorRef<? extends Message> actorOf(Class<? extends Actor> actor, ActorMode mode);

    /**
     * Create an instance of {@code actor} that executes locally.
     *
     * @param actor The type of actor that has to be created
     * @return A reference to the actor
     */
    ActorRef<? extends Message> actorOf(Class<? extends Actor> actor);

    /**
     * Stops {@code actor}.
     *
     * @param actor The actor to be stopped
     */
    void stop(ActorRef<?> actor);

    /**
     * Stops all actors of the system.
     */
    void stop();

    /**
     * Possible modes to create an actor. {@code LOCALE} mode is used to create an actor
     * that acts in the local system. {@code REMOTE} mode is used to create remote actors.
     */
    enum ActorMode {
        LOCAL,
        REMOTE
    }
}