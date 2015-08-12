package org.gossip.messages
import java.net.InetAddress
import org.gossip.state.NodeState
import org.gossip.state.GossipDigest
import com.typesafe.scalalogging._

trait Message extends Serializable 

/**
 * The first message that is passed around, initialization of Gossip with a peer node.
 */
case class GossipSyn(header: Header, body: Array[Byte], version: Int, clusterName: String, gossipDigests: List[GossipDigest]) extends Message 
/**
 * An ack to {@link GossipSyn} by the peer node.
 */
case class GossipAck(header: Header, body: Array[Byte], version: Int, states: Map[InetAddress, NodeState]) extends Message

/**
 * An ack by the node originating the gossip handshake to the peer node, an ack to {@link GossipAck}
 */
case class GossipSynAck(header: Header, body: Array[Byte], version: Int, gossipDigests: List[GossipDigest], states: Map[InetAddress, NodeState]) extends Message
  

object MessageHandler extends LazyLogging {
  
  /**
   * Handler for Gossip Messages.
   * Depending on the type of message
   * this handler should take appropriate action.
   */
  def handle(message: Message) = message match {

    case GossipSyn(header: Header, body: Array[Byte], version: Int, clusterName: String, gossipDigests: List[GossipDigest]) =>
      val from_ = header.from
      //TODO check for cluster clusterName.
      //TODO use gossipDigests to notify failure detectors.
      //TODO examine gossipDigests and prepare acks
      //calculateDelta()

      logger.info("Recieved GOSSIP SYNC")

    case GossipAck(header: Header, body: Array[Byte], version: Int, states: Map[InetAddress, NodeState]) =>
      logger.info("Recieved GOSSIP ACK")
    //handle
    //GossipSyn ("", )

    case GossipSynAck(header: Header, body: Array[Byte], version: Int, gossipDigests: List[GossipDigest], states: Map[InetAddress, NodeState]) =>
      logger.info("Recieved GOSSIP SYN ACK")

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
  