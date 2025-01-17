/*
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  The OpenSearch Contributors require contributions made to
 *  this file be licensed under the Apache-2.0 license or a
 *  compatible open source license.
 *
 *  Modifications Copyright OpenSearch Contributors. See
 *  GitHub history for details.
 */

package com.amazon.dataprepper.pipeline.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FutureHelper {
    private static final Logger LOG = LoggerFactory.getLogger(FutureHelper.class);

    public static <A> FutureHelperResult<A> awaitFuturesIndefinitely(final List<Future<A>> futureList) {
        final List<A> completedFutureResults = new ArrayList<>(futureList.size());
        final List<ExecutionException> failedExceptionList = new ArrayList<>();

        final Queue<Future<A>> futureQueue = new LinkedList<>(futureList);
        while(!futureQueue.isEmpty()) {
            final Future<A> future = futureQueue.remove();
            try{
                completedFutureResults.add(future.get());
            } catch (ExecutionException e) {
                failedExceptionList.add(e);
                LOG.error("FutureTask failed due to: ", e);
            } catch (InterruptedException e) {
                LOG.error("FutureTask is interrupted or timed out");
            }
        }
        return new FutureHelperResult<>(completedFutureResults, failedExceptionList);
    }
}
