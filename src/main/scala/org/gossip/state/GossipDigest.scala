package org.gossip.state

import java.net.InetAddress

/**
 * A gossip digest contains information about a specific node that is currently known. 
 * <ul>
 * <li> Node address (directly reachable)
 * <li> Generation, increases after node restart, helps to track node restarts.
 * <li> Version, increments after every heartbeat.
 * </ul>
 */
class GossipDigest(address: InetAddress, generation: Int, version: Int) {

}