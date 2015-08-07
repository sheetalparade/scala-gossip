
/**
 * This is start of a Gossip Implementation.
 *
 * This class will be used to run Gossip as standalone application.
 *
 */

import java.net.{InetSocketAddress, InetAddress}
import akka.actor.{Props, ActorSystem}
import akka.io.Tcp.Bind
import org.gossip.actors.ServerActor
import org.gossip.state.GossipDigest

object Gossip  {
  
  def main(args: Array[String]) {
  println("Hi this is start of Gossip implementation")
  println()

  val gossip = new Gossip( InetAddress.getLocalHost, 40000)


  sys.addShutdownHook()
  println("End of Gossip")
//  System.exit(0)
  }
}

/**
 * Create a Gossip Class with binding network address and port.
 *
 */
class Gossip(binding: InetAddress, port: Int) {

  /**
   * Initial the class
   */
  val actorSystem = ActorSystem()
  val serverActor = actorSystem.actorOf(ServerActor.props (new InetSocketAddress(binding, port)))
  serverActor ! Bind

  /**
   * Start gossiping with initial communication with seed servers.
   * The seed server could be itself and no communication needed.
   */
  def start(seed: InetAddress) {

  }

}