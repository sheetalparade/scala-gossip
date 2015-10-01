package org.gossip.network

import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.ByteBuffer

import org.gossip.network.actors._

import akka.actor.ActorSystem
import akka.io.Tcp._

object Communication {
  /**
   * Only used for testing and when used as standalone application.
   *
   * Use case as standalone application are very minimal and might not be used at all.
   *
   * @param args
   */
  def main(args: Array[String]) {
    println("Hi this is start of communication implementation")
    println()
    val handler = new DummyWorkerActor
    val communication = new Communication(InetAddress.getLoopbackAddress, 4000, handler)

    Thread.sleep(5000)

    communication.connect(new InetSocketAddress(InetAddress.getLoopbackAddress, 4000), handler)
    val workerActor = communication.getWorkerActorRef(handler)
    workerActor ! "TEST MSG"

    communication.awaitTermination
    println("End of communication")
    communication.shutdown
    //    sys.addShutdownHook()
    System.exit(0)
  }

  class DummyWorkerActor extends WorkerHandler {
    var countRemote = 0;
    override def handleRemoteMessage(data: ByteBuffer): ByteBuffer = {
      println(s"received ${new String(data.array())}")
      countRemote = countRemote + 1;
      if (countRemote == 5) return null;
      println("returning World")
      return ByteBuffer.wrap("World".getBytes)
    }

    override def firstMessage: ByteBuffer = ByteBuffer.wrap("Hello".getBytes());
    
    override def handleStorageMessage(data: Any) {
      println("in DummyWorkerActor")
      println(s"received storage message outside of network layer $data")
    }
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
class Communication(binding: InetAddress, port: Int, handler: WorkerHandler) {

  /**
   * Initialize the class
   */
  private val actorSystem = ActorSystem()
  private val serverActor = actorSystem.actorOf(ServerSystem.props(handler))

  serverActor ! Bind(serverActor, new InetSocketAddress(binding, port))

  /**
   * Start gossiping with initial communication with seed servers.
   * The seed server could be itself and no communication needed.
   */
  def connect(seed: InetSocketAddress, handler: WorkerHandler) {
    println(s"Connect to seed $seed with worker handler")
    val workerActor = actorSystem.actorOf(WorkerSystem.props(handler))
    workerActor ! Connect(remoteAddress = seed)
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