package org.gossip.messages

import java.net.InetAddress

case class State (inetAddress : InetAddress, version: Int)