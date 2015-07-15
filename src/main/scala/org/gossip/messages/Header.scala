package org.gossip.messages
import java.net.InetAddress
import org.gossip.messages.Verb._


/**
 * Header for the message
 */
class Header(from: InetAddress, verb: Verb, options: Map[String, Array[Byte]]) {

}