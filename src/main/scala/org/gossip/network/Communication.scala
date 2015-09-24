package org.gossip.network

/**
 * This is start of a Gossip Implementation.
 *
 * This class will be used to run Gossip as standalone application.
 *
 */

import java.net.{InetAddress, InetSocketAddress}
import java.nio.ByteBuffer
import akka.actor.ActorSystem
import akka.io.Tcp._
import akka.util.ByteString
import org.gossip.network.actors.{ServerSystem, WorkerActor, WorkerSystem}

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

    val communication = new Communication(InetAddress.getLoopbackAddress, 4000, classOf[DummyWorkerActor])

    Thread.sleep(5000)

    class DummyWorkerActor extends WorkerActor() {
      override def handleMessage(data: ByteBuffer): ByteBuffer = {
        println(s"received ${data}")
        println("returning World")
        return ByteBuffer.wrap("World".getBytes())
      }
    }
    communication.connect(new InetSocketAddress(InetAddress.getLoopbackAddress, 4000), classOf[DummyWorkerActor], ByteBuffer.wrap("Hello".getBytes()))


    communication.awaitTermination
    println("End of communication")
//    gossip.shutdown
//    sys.addShutdownHook()
      System.exit(0)
  }
}

/**
 *
 * Create a Communication Class with binding network address and port.
 *
 * To be used within broader framework.
 *
 * Starts the servers on given ip and port.
 */
class Communication(binding: InetAddress, port: Int, worker: Class[_ <:WorkerActor]) {

  /**
   * Initialize the class
   */
  val actorSystem = ActorSystem()
  val serverActor = actorSystem.actorOf(ServerSystem.props (worker))

  serverActor ! Bind(serverActor, new InetSocketAddress(binding, port))

  /**
   * Start gossiping with initial communication with seed servers.
   * The seed server could be itself and no communication needed.
   */
  def connect(seed: InetSocketAddress, worker: Class[_ <:WorkerActor], byteBuffer: ByteBuffer) {
    println(s"Connect to seed $seed with worker $worker")
    val workerActor = actorSystem.actorOf(WorkerSystem.props(worker))
    workerActor ! Connect(remoteAddress = seed)
    println("now connected and sleep")
    Thread.sleep(1000)
    workerActor ! Write(ByteString(byteBuffer), NoAck)
  }

  def shutdown: Unit = {
    serverActor ! Close
    actorSystem.shutdown()
  }

  def awaitTermination: Unit = {
    actorSystem.awaitTermination()
  }
}