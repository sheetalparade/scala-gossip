package org.gossip.messages
import org.gossip.state.GossipDigest

/**
 * GossipSyn is the first message in the three-way handshake
 * for Gossip.
 */
case class GossipSyn (clusterName: String, gossipDigests: List[GossipDigest] ) extends Message {

}