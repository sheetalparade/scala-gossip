package org.gossip.network

import java.nio.ByteBuffer
import org.gossip.network.actors.WorkerHandler
import java.net.InetSocketAddress
import java.net.InetAddress
import org.gossip.akka.GossipActorSystem
import org.gossip.scheduler.DefaultExecutionOnce


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
    val handler = new DummyWorkerHandler
    Server(InetAddress.getLoopbackAddress, 4000, handler)

    Thread.sleep(500)

    DefaultExecutionOnce.executeOnce(handler, new InetSocketAddress(InetAddress.getLoopbackAddress, 4000))
    
    GossipActorSystem.awaitTermination
    println("End of communication")
    GossipActorSystem.shutdown
    //    sys.addShutdownHook()
    System.exit(0)
  }

  class DummyWorkerHandler extends WorkerHandler {
    var countRemote = 0;
    override def handleRemoteMessage(data: ByteBuffer): ByteBuffer = {
      val recd = new String(data.array())
      println(s"received $recd")
      countRemote = countRemote + 1;
      if (countRemote == 5) return null;
      val ret = if(recd == "World") "Hello" else "World"
      println(s"returning $ret")
      return ByteBuffer.wrap(ret.getBytes)
    }

    override def initialMessage: ByteBuffer = ByteBuffer.wrap("Hello".getBytes());
    
    override def handleStorageMessage(data: Any) :ByteBuffer = {
      println("in DummyWorkerActor")
      println(s"received storage message outside of network layer $data")
      return null;
    }
  }

}