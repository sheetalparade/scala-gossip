package org.gossip

/**
 * This is start of a Gossip Implementation.
 *
 * This class will be used to run Gossip as standalone application.
 *
 */

import java.net.{InetAddress, InetSocketAddress}
import java.nio.ByteBuffer
import java.nio.charset.Charset

import akka.actor.ActorSystem
import akka.io.Tcp._
import akka.util.ByteString
import org.gossip.actors.{ServerSystem, WorkerActor, WorkerSystem}

object Gossip {
  /**
   * Only used for testing and when used as standalone application.
   *
   * Use case as standalone application are very minimal and might not be used at all.
   *
   * @param args
   */
  def main(args: Array[String]) {
    println("Hi this is start of Gossip implementation")
    println()

    val gossip = new Gossip(InetAddress.getLoopbackAddress, 4000, classOf[DummyWorkerActor])

    Thread.sleep(5000)

    class DummyWorkerActor extends WorkerActor {
      override def handleMessage(data: ByteBuffer): ByteBuffer = {
        println(s"received ${data}")
        println("returning World")
        return ByteBuffer.wrap("World".getBytes())
      }
    }
    gossip.start(new InetSocketAddress(InetAddress.getLoopbackAddress, 4000), classOf[DummyWorkerActor], ByteBuffer.wrap("Hello".getBytes()))


    gossip.awaitTermination
    println("End of Gossip")
//    gossip.shutdown
//    sys.addShutdownHook()
      System.exit(0)
  }
}

/**
 *
 * Create a Gossip Class with binding network address and port.
 *
 * To be used within broader framework.
 *
 * Starts the servers on given ip and port.
 */
class Gossip(binding: InetAddress, port: Int, worker: Class[_ <:WorkerActor]) {

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
  def start(seed: InetSocketAddress, worker: Class[_ <:WorkerActor], byteBuffer: ByteBuffer) {
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