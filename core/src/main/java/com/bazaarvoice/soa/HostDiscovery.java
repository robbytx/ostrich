package com.bazaarvoice.soa;

import java.util.Collection;

/**
 * The <code>HostDiscovery</code> class is used to encapsulate the strategy that provides a set of hosts for use.
 * Users of this class shouldn't cache the results of discovery as subclasses can choose to change the set of available
 * hosts based on some external mechanism (ex. using ZooKeeper).
 */
public interface HostDiscovery {
    /**
     * Retrieve the available hosts.
     * @return The available hosts.
     */
    Iterable<ServiceInstance> getHosts();
}