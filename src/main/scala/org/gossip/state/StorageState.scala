package org.gossip.state
import java.net.InetAddress

/**
 * Manages the state of data received from various agents on the cluster.
 * 
 */
trait StorageState {
  
}

/**
 * An in-memory implementation 
 * of the storage component.
 */
class MemoryStorage extends StorageState {
  
  /**
   * Stores the local (known) state of endpoints.
   */
  private var endpointStateMap = Map[InetAddress, NodeState]()
  
  /**
   * A list of live endpoints
   */
  private var liveEndpoints = List[InetAddress]()
  
  /**
   * A list of dead endpoints with timestamp
   */
  private var deadEndpoints = Map[InetAddress, Long]()
  
  /**
   * A map of endpoints that was removed from gossip.
   * Any gossip for these endpoints will be ignored
   */
  private var justRemovedEndpoints = Map[InetAddress, Long]()
  
  /**
   * A map of endpoints that are expired.
   */
  private var expiredEndpoints = Map[InetAddress, Long]()
  
  /**
   * A map of endpoint versions. 
   * Let's know which endpoint is on what version.
   */
  private var endpointVersions = Map[InetAddress, Integer]()
}