package org.gossip.messages

import org.gossip.state.GossipDigest
import java.net.InetAddress
import org.gossip.state.State

case class GossipSynAck(gossipDigests: List[GossipDigest], states: Map[InetAddress, State]) extends Message {

}