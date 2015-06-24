package org.gossip.messages
import java.net.InetAddress
import org.gossip.state.State
import org.gossip.state.GossipDigest

object Message {

  /**
   * Messages to be passed by gossip handshake.
   *
   */
  trait Msg extends Serializable {
    //TODO header
    //TODO body
    //TODO version

    def messageHandler(message: Msg) = message match {

      case GossipAck(states: Map[InetAddress, State]) =>
        println("Recieved GOSSIP ACK")
        //handle
        //GossipSyn ("", )
      case GossipSyn(clusterName: String, gossipDigests: List[GossipDigest])                => println("Recieved GOSSIP SYNC")
      case GossipSynAck(gossipDigests: List[GossipDigest], states: Map[InetAddress, State]) => println("Recieved GOSSIP SYNC ACK")

    }

  }
  
  /**
   * The first message that is passed around, initialization of Gossip with a peer node.
   */
  case class GossipSyn (clusterName: String, gossipDigests: List[GossipDigest] ) extends Msg
  
  /**
   * An ack to {@link GossipSyn} by the peer node.
   */
  case class GossipAck(states: Map[InetAddress, State]) extends Msg
    
  /**
   * An ack by the node originating the gossip handshake to the peer node, an ack to {@link GossipAck}  
   */
  case class GossipSynAck(gossipDigests: List[GossipDigest], states: Map[InetAddress, State]) extends Msg
  

}
