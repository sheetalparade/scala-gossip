
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

  /**
   * This method is called by the syn message handler.<p>
   * Calculates the delta between the received list of gossipDigests
   * and the one that this node has; and prepare:
   * <li> 
   * 1) A list of request of new data from the sender (gossip initiator)
   * to overwrite our old state.
   * </li>
   * <li>
   * 2) A Map of endpoints <-> state to be send to the sender to 
   * overwrite their old state.
   * </li>
   * The comparison is done based on the generation number 
   * and the version number.
   */
  def calculateDelta(gossipDigests: List[GossipDigest], requestGossipData: List[GossipDigest], sendGossipData: Map[InetAddress, GossipDigest]) = {

  }

}