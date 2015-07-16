package org.gossip.messages
import java.net.InetAddress
import org.gossip.state.State
import org.gossip.state.GossipDigest
import com.typesafe.scalalogging._

trait Message extends Serializable with LazyLogging  {
  
    /**
   * Message header
   */
  //var header: Header
  
  /**
   * Body of the message
   */
  //var body: Array[Byte]
  
  /**
   * Version for the message
   */
  //var version: Int
  
  /**
   * Handler for Gossip Messages.
   * Depending on the type of message 
   * this handler should take appropriate action.
   */
  def messageHandler(message: Message) = message match {

      case GossipSyn(header: Header, body: Array[Byte], version: Int, clusterName: String, gossipDigests: List[GossipDigest]) => 
        val from_ = header.from
        //TODO check for cluster clusterName.
        //TODO use gossipDigests to notify failure detectors.
        //TODO examine gossipDigests and prepare acks
        
        logger.info("Recieved GOSSIP SYNC")
        
        
        
      case GossipAck(header: Header, body: Array[Byte], version: Int, states: Map[InetAddress, State]) =>
        logger.info("Recieved GOSSIP ACK")
        //handle
        //GossipSyn ("", )
      
      case GossipSynAck(header: Header, body: Array[Byte], version: Int, gossipDigests: List[GossipDigest], states: Map[InetAddress, State]) => println("Recieved GOSSIP SYNC ACK")
        logger.info("Recieved GOSSIP SYN ACK")

    }
  
}

/**
   * The first message that is passed around, initialization of Gossip with a peer node.
   */
  case class GossipSyn (header: Header, body: Array[Byte], version: Int, clusterName: String, gossipDigests: List[GossipDigest] ) extends Message
  
  /**
   * An ack to {@link GossipSyn} by the peer node.
   */
  case class GossipAck(header: Header, body: Array[Byte], version: Int, states: Map[InetAddress, State]) extends Message
    
  /**
   * An ack by the node originating the gossip handshake to the peer node, an ack to {@link GossipAck}  
   */
  case class GossipSynAck(header: Header, body: Array[Byte], version: Int, gossipDigests: List[GossipDigest], states: Map[InetAddress, State]) extends Message
  
  