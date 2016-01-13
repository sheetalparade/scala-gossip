package org.gossip.scheduler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

import org.gossip.akka.GossipActorSystem
import org.gossip.network.actors.WorkerHandler
import org.gossip.network.actors.WorkerSystem

import akka.io.Tcp.Connect

/**
 * @author sparade
 */
object Default1SecondScheduler {
  
  def executeOnSchedule(handler: WorkerHandler) = {
    GossipActorSystem.getScheduler.schedule(0 seconds, 1 seconds)({
      val nextHost = WorkerSystem.nextHost
      if(!nextHost.isEmpty)
        GossipActorSystem.getWorkerActorRef(handler) ! Connect(remoteAddress = WorkerSystem.nextHost.get) 
    })
  }
}