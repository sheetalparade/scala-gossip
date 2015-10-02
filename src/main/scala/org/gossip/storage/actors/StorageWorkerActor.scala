package org.gossip.storage.actors

import akka.actor.ActorLogging
import akka.util.ByteString
import java.nio.ByteBuffer
import org.gossip.network.actors.WorkerHandler

/**
 * Implementation of WorkerActor
 * 
 * StorageWorkers handles all incoming request from the server. 
 * This class is aware of all storage needs. All writes to 
 * Storage should be handles through this Actor to avoid 
 * data concurrency issues. 
 * 
 * Reads to Data could be direct access.
 * 
 * 
 * @author sparade
 */
class StorageWorkerActor extends WorkerHandler{
    
  override def handleRemoteMessage(data: ByteBuffer): ByteBuffer = {
    return null;  
  }
  
  override def firstMessage: ByteBuffer = {
    return null;
  }
  
  /**
   * All data writes will be handled over actor messaging with this call.
   */
  override def handleStorageMessage(data: Any) {
  }

}