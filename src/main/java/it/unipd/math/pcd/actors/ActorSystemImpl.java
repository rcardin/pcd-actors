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
 * A possible implementation of the actor system. This implementation is accessible from everywhere due to a Singleton
 * approach. A better implementation should use Dependency Injection
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public class ActorSystemImpl extends AbsActorSystem {

    /**
     * The unique instance of the actor system
     */
    private static final ActorSystem INSTANCE = new ActorSystemImpl();

    /**
     * Returns the unique instance of the actor system
     *
     * @return A reference to the {@link ActorSystem actor system}
     */
    public static ActorSystem getInstance() {
        return INSTANCE;
    }

    public ActorSystemImpl() {
        // TODO Insert your implementation here
    }

    @Override
    protected ActorRef createActorReference(ActorMode mode) {
        // TODO Insert your implementation here
        return null;
    }

    @Override
    public void stop(ActorRef<?> actor) {
        // TODO Insert your implementation here
    }

    @Override
    public void stop() {
        // TODO Insert your implementation here
    }
}
