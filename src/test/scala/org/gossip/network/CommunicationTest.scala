package org.gossip.network

import java.nio.ByteBuffer
import org.gossip.network.actors.WorkerHandler
import java.net.InetSocketAddress
import java.net.InetAddress


/**
 * @author sparade
 */
object CommunicationTest {
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
    val communication = Communication("test", InetAddress.getLoopbackAddress, 4000, handler)

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