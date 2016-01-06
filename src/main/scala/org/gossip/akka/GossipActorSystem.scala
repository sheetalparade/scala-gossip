package org.gossip.akka

import akka.actor.ActorSystem
import org.gossip.network.actors.WorkerHandler
import org.gossip.network.actors.ServerSystem
import org.gossip.network.actors.WorkerSystem

/**
 * @author sparade
 */
object GossipActorSystem {
  private val actorSystem = ActorSystem("Gossip")

  def getScheduler = {
    actorSystem.scheduler
  }

  def getServerActorRef(handler: WorkerHandler) = {
    actorSystem.actorOf(ServerSystem.props(handler))
  }

  def getWorkerActorRef(handler: WorkerHandler) = {
    actorSystem.actorOf(WorkerSystem.props(handler))
  }
  /**
   * Invoked to shutdown communication layer.
   *
   * Closes all connection and shutdowns actor system
   */
  def shutdown: Unit = {
    actorSystem.shutdown()
  }

  /**
   * await termination. Useful when gossip or communication layer invoked on its own.
   */
  def awaitTermination: Unit = {
    actorSystem.awaitTermination()
  }

}