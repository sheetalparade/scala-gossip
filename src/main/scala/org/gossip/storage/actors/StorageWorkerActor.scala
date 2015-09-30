package org.gossip.storage.actors

import org.gossip.network.actors.WorkerActor
import akka.actor.ActorLogging
import akka.util.ByteString
import java.nio.ByteBuffer

/**
 * @author sparade
 */
class StorageWorkerActor extends WorkerActor with ActorLogging{
    
  override def handleMessage(data: Iterable[ByteBuffer]): ByteBuffer = {
    return null;  
  }

}