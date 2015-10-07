package org.gossip.storage

import scala.collection.mutable.ListBuffer
import java.nio.ByteBuffer
import org.gossip.network.actors.WorkerHandler

/**
 * @author sparade
 */

object StorageManager {

  def getVersions(namespace: String): Map[String, Int] = {
    null;
  }

  def save(namespace: String, keys: Array[String]): Data = {
    null;
  }

  def checkVersionsDiff(namespace: String, iVersions: Map[String, Int]): Map[String, Int] = {
    null;
  }

  /**
   * Implementation of WorkerActor
   *
   * StorageWorkers handles all incoming request from the server.
   * This class is aware of all storage needs. All writes to
   * Storage should be handles through this Actor to avoid
   * data concurrency issues.
   *
   * Reads to Data could be direct access.
   *
   *
   * @author sparade
   */
  class StorageWorkerHandler extends WorkerHandler {

    override def handleRemoteMessage(data: ByteBuffer): ByteBuffer = {
      return null;
    }

    override def firstMessage: ByteBuffer = {
      return null;
    }

    /**
     * All data writes will be handled over actor messaging with this call.
     */
    override def handleStorageMessage(data: Any) {
    }

  }
}

class Data {

  private var dataMap = ListBuffer[Record]()

  def save = {}

  def update = {}

  def get = dataMap

  case class Record(namespace: String, version: Int, bytes: Array[Byte]);
}