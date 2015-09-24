package org.gossip.network.actors

import akka.actor.Actor
import java.net.InetAddress
import org.gossip.state.NodeState
import akka.event.Logging

/**
 * StorageActor manages the state of data received from various agents on the cluster.
 * 
 * This is the only place with read/write of data
 * 
 */
trait StorageActor extends Actor{

  val log = Logging(context.system, this)
  
}

/**
 * An in-memory implementation of storage mechanism.
 */
class MemoryStorage extends StorageActor {
  
  override def preStart() = {
    log.info("Starting MemoryStorage ...")
  } 
  
  override def preRestart(reason: Throwable, message: Option[Any]) = {
    log.error(reason, "Restarting due to [{}] while processing [{}] ", reason.getMessage, message.getOrElse(""))
  }
  
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
  
  
  override def receive = {
    null
  }
  
}