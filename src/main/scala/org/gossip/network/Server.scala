package org.gossip.network

import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import org.gossip.network.actors._
import akka.actor.ActorSystem
import akka.io.Tcp._
import akka.util.ByteString
import org.gossip.akka.GossipActorSystem

object Server {

    /**
   * single communication per name-space.
   *
   */
  def apply(binding: InetAddress, port: Int, handler: WorkerHandler): Unit = {
    new Server(binding: InetAddress, port: Int, handler: WorkerHandler)
  }
  


  /**
   *
   * Create a Communication Class with binding network address and port.
   *
   * To be used within broader framework.
   *
   * Starts the servers on given ip address and port as part of constructor.
   */
  private class Server(binding: InetAddress, port: Int, handler: WorkerHandler) {

    /**
     * Initialize the class
     */
    val serverActorRef = GossipActorSystem.getServerActorRef(handler)
    serverActorRef ! Bind(serverActorRef, new InetSocketAddress(binding, port))
    
    def status = {
      
    }
  }
}
