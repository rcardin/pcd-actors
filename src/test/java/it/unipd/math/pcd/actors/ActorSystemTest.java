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
import it.unipd.math.pcd.actors.utils.ActorSystemFactory;
import it.unipd.math.pcd.actors.utils.actors.TrivialActor;
import it.unipd.math.pcd.actors.utils.messages.TrivialMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests features of an actors' system.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public class ActorSystemTest {

    private ActorSystem system;

    /**
     * Initializes the {@code system} with a concrete implementation before each test.
     */
    @Before
    public void init() {
        system = ActorSystemFactory.buildActorSystem();
    }

    @Test
    public void shouldCreateAnActorRefWithActorOfTest() {
        ActorRef ref = system.actorOf(TrivialActor.class);
        Assert.assertNotNull("A reference was created and it is not null", ref);
    }

    @Test
    public void shouldCreateAnActorRefOfWithActorModeLocalTest() {
        ActorRef ref = system.actorOf(TrivialActor.class, ActorSystem.ActorMode.LOCAL);
        Assert.assertNotNull("A reference to a local actor was created and it is not null", ref);
    }

    /**
     * It is not requested to implement remote mode for actors anymore. So, an attempt to create a remote
     * actor should rise an {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldCreateAnActorRefOfWithActorModeRemoteTest() {
        system.actorOf(TrivialActor.class, ActorSystem.ActorMode.REMOTE);
    }

    @Test
    public void shouldBeAbleToCreateMoreThanOneActor() {
        ActorRef ref1 = system.actorOf(TrivialActor.class);
        ActorRef ref2 = system.actorOf(TrivialActor.class);
        Assert.assertNotEquals("Two references that points to the same actor implementation are not equal", ref1, ref2);
    }

    @Test(expected = NoSuchActorException.class)
    public void shouldStopAnActorAndThisCouldNotBeAbleToReceiveNewMessages() {
        ActorRef ref1 = system.actorOf(TrivialActor.class);
        system.stop(ref1);
        ref1.send(new TrivialMessage(), ref1);
    }

    @Test(expected = NoSuchActorException.class)
    public void shouldStopAnActorAndThisCouldNotStoppedASecondTime() {
        ActorRef ref1 = system.actorOf(TrivialActor.class);
        system.stop(ref1);
        system.stop(ref1);
    }
}
