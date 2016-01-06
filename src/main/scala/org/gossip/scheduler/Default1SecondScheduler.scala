package org.gossip.scheduler

import org.gossip.network.actors.WorkerSystem
import akka.actor.Scheduler
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import org.gossip.akka.GossipActorSystem
import org.gossip.network.actors.WorkerHandler
import akka.io.Tcp.Connect
import java.net.InetSocketAddress

/**
 * @author sparade
 */
object Default1SecondScheduler {
  
  def executeOnSchedule(handler: WorkerHandler){
    import scala.concurrent.ExecutionContext.Implicits.global
    GossipActorSystem.getScheduler.schedule(0 seconds, 1 seconds)(
      GossipActorSystem.getWorkerActorRef(handler) ! Connect(remoteAddress = WorkerSystem.nextHost) 
    )
  }
  
  
}