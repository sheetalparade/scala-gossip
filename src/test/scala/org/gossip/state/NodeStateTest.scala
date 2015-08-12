package org.gossip.state
import org.gossip.test.UnitSpec
import java.io.DataOutputStream
import java.io.ByteArrayOutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.DataInputStream
import java.io.ByteArrayInputStream
import sun.nio.ch.IOUtil
import sun.misc.IOUtils

/**
 * 
 * Unit test to test the class [[org.gossip.state.NodeState]]
 */
class NodeStateTest extends UnitSpec {

  "NodeState" should "serialize and deserialize" in {
    
    val version = 5
    val heartBeats = 7
    val timestamp = System.currentTimeMillis()
    val heartBeat:HeartBeatState = new HeartBeatState((System.currentTimeMillis()/1000).toInt, heartBeats)
    val nodeState:NodeState = new NodeState(heartBeat)
    
    /* serialization stuff */
    var baos = new ByteArrayOutputStream()
    var out = new DataOutputStream(baos)
   
    /** create 10 node states */
    for(i <- 1 to 10) {
       nodeState.nodeStates += new VersionedValueKey("KEY".concat(i.toString())) -> new VersionedValue("VALUE".concat(i.toString()), version)      
    }    
    
    nodeState.isAlive = true;
    nodeState.updatedTimestamp = timestamp   

    
    /* serialize */
    NodeState.serializer(nodeState, out, version)
    
    var in = new DataInputStream( new ByteArrayInputStream(baos.toByteArray()))
    
    /* deserialize */
    val nodeStateDeserialized = NodeState.deserializer(in)
    
    //println("Result : "+nodeStateDeserialized.updatedTimestamp +" timestamp: "+timestamp)    
    //nodeStateDeserialized.nodeStates.foreach(state => println("Key: "+state._1.key.toString()+" Value: "+state._2.value.toString()))
    
    assert(nodeStateDeserialized.heartBeat.beats == heartBeats)    
    
  }
}