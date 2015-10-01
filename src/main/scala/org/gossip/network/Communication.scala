package org.gossip.network

import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.ByteBuffer

import org.gossip.network.actors._

import akka.actor.ActorSystem
import akka.io.Tcp._

object Communication {
  
  var commObjs :Map[String, Communication] = _
  
  /**
   * single communication per name-space.
   * 
   */
  def apply (namespace: String, binding: InetAddress, port: Int, handler: WorkerHandler): Communication = {
    val comm = commObjs.getOrElse(namespace, new Communication(namespace: String, binding: InetAddress, port: Int, handler: WorkerHandler))
    commObjs + (namespace -> comm)
    comm
  }
  
}

/**
 *
 * Create a Communication Class with binding network address and port.
 *
 * To be used within broader framework.
 *
 * Starts the servers on given ip address and port as part of constructor.
 */
class Communication(namespace: String, binding: InetAddress, port: Int, handler: WorkerHandler) {

  /**
   * Initialize the class
   */
  private val actorSystem = ActorSystem(s"Gossip-$namespace")
  private val serverActor = actorSystem.actorOf(ServerSystem.props(handler))

  serverActor ! Bind(serverActor, new InetSocketAddress(binding, port))

  /**
   * Start gossiping with initial communication with seed servers.
   * The seed server could be itself and no communication needed.
   */
  def connect(seed: InetSocketAddress, handler: WorkerHandler) {
    println(s"Connect to seed $seed with worker handler")
    getWorkerActorRef(handler) ! Connect(remoteAddress = seed)
    println("now connected")
  }

  /**
   * returns the worker actor reference from same actorsystem.
   *
   */
  def getWorkerActorRef(handler: WorkerHandler) = actorSystem.actorOf(WorkerSystem.props(handler))

  /**
   * Invoked to shutdown communication layer.
   *
   * Closes all connection and shutdowns actor system
   */
  def shutdown: Unit = {
    serverActor ! Close
    actorSystem.shutdown()
  }

  /**
   * await termination. Useful when gossip or communication layer invoked on its own.
   */
  def awaitTermination: Unit = {
    actorSystem.awaitTermination()
  }
}