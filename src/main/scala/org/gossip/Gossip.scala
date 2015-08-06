
/**
 * This is start of a Gossip Implementation.
 *
 * This class will be used to run Gossip as standalone application.
 *
 */

import java.net.InetAddress
import org.gossip.state.GossipDigest

object Gossip extends App {
  println("Hi this is start of Gossip implementation")
  println()
  System.exit(0)
}

/**
 * Create a Gossip Class with binding network address and port.
 *
 */
class Gossip(binding: InetAddress, port: Int) {

  /**
   * Initial the class
   */
  def init = {

  }

  /**
   * Start gossiping with initial communication with seed servers.
   * The seed server could be itself and no communication needed.
   */
  def start(seed: InetAddress) {

  }

}