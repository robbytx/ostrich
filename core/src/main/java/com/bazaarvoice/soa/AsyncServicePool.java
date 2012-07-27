package com.bazaarvoice.soa;

import java.io.Closeable;
import java.util.concurrent.Future;

/**
 * An asynchronous service pool.  This mimics the behavior of a {@link ServicePool}, but instead of executing its
 * callbacks synchronously, it will run them in the background.
 *
 * @param <S> The service interface that this pool keeps track of end points for.
 */
public interface AsyncServicePool<S> extends Closeable {
    /**
     * Execute a request asynchronously against one of the remote services in this <code>ServicePool</code>, returning
     * a future representing the asynchronous call.
     *
     * @param retryPolicy The retry policy for the operation.
     * @param callback The user provided callback to invoke with a service end point.
     * @param <R> The return type for the call.
     */
    <R> Future<R> execute(RetryPolicy retryPolicy, ServiceCallback<S, R> callback);
}