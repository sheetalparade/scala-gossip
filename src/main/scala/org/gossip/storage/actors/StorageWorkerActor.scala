package org.gossip.storage.actors

import org.gossip.network.actors.WorkerActor
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
 * data concurrency issues. This is still a TODO item.
 * 
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
  
  override def handleStorageMessage(data: Any) : ByteBuffer = {
    return null;
  }

}