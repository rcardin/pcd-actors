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
package it.unipd.math.pcd.actors.utils;

/**
 * Permits to wait on a condition
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public class Waiter {

    /**
     * Default sleeping time between two different retries.
     */
    public static final int SLEEP_TIME = 2000;

    /**
     * Waits untils {@code condition} is fulfilled or the condition was evaluated for
     * a {@code maxRetry} of times. Between each evaluation the waiter waits for {@link #SLEEP_TIME}
     * milliseconds
     *
     * @param condition The condition to evaluate
     * @param maxRetry Maximum number of retries
     *
     * @throws InterruptedException
     */
    public static void wait(Condition condition, int maxRetry) throws InterruptedException {
        int retry = 0;
        while (condition.evaluate() && retry < maxRetry) {
            retry++;
            Thread.sleep(SLEEP_TIME);
        }
    }

    public static interface Condition {
        boolean evaluate();
    }
}
