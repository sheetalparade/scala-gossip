package org.gossip.scheduler

import org.gossip.network.actors.WorkerHandler
import java.net.InetSocketAddress
import akka.io.Tcp.Connect
import org.gossip.akka.GossipActorSystem
import org.gossip.network.actors.WorkerSystem

/**
 * @author sparade
 */
object DefaultExecutionOnce {
    def executeOnce(handler: WorkerHandler, remoteAddress: InetSocketAddress) {
      GossipActorSystem.getWorkerActorRef(handler) ! Connect(remoteAddress)     
  }
  
  def executeOnceNext(handler: WorkerHandler) {
      GossipActorSystem.getWorkerActorRef(handler) ! Connect(remoteAddress = WorkerSystem.nextHost)     
  }

}