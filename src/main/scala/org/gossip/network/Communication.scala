package org.gossip.network

import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import org.gossip.network.actors._
import akka.actor.ActorSystem
import akka.io.Tcp._
import akka.util.ByteString

object Communication {

  private var commObjs: scala.collection.mutable.Map[String, Communication] = scala.collection.mutable.Map()

  /**
   * single communication per name-space.
   *
   */
  def apply(namespace: String, binding: InetAddress, port: Int, handler: WorkerHandler): Unit = {
    val comm = commObjs.get(namespace)
    if(comm == None)
       commObjs += (namespace ->  new Communication(namespace: String, binding: InetAddress, port: Int, handler: WorkerHandler))
  }

  def connect(namespace: String) {
    val communication = commObjs.get(namespace)
    if (communication != None)
      communication.get.connect()
  }
  
  def awaitTermination(namespace: String) {
    val communication = commObjs.get(namespace)
    if (communication != None)
      communication.get.awaitTermination
  }
  
  
  def shutdown(namespace: String) {
    val communication = commObjs.get(namespace)
    if (communication != None)
      communication.get.shutdown    
  }

  /**
   *
   * Create a Communication Class with binding network address and port.
   *
   * To be used within broader framework.
   *
   * Starts the servers on given ip address and port as part of constructor.
   */
  private class Communication(namespace: String, binding: InetAddress, port: Int, handler: WorkerHandler) {

    /**
     * Initialize the class
     */
    private val actorSystem = ActorSystem(s"Gossip-Communication-$namespace")
    private val serverActor = actorSystem.actorOf(ServerSystem.props(handler))

    serverActor ! Bind(serverActor, new InetSocketAddress(binding, port))
    
     /**
     * Start gossiping with initial communication with seed servers.
     * The seed server could be itself and no communication needed.
     */
    def connect() {
      val seed = new InetSocketAddress(binding, port)
      println(s"Connect to seed $seed with worker handler")
      val worker = getWorkerActorRef(handler)
      worker ! Connect(remoteAddress = seed)
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
}
