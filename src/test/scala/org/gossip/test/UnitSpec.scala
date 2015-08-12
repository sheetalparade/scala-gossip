package org.gossip.test
import org.scalatest._

/**
 * An abstract class that extends all the traits that we might need
 * for unit testing.
 */
abstract class UnitSpec extends FlatSpec with Matchers with OptionValues with Inside with Inspectors {

}