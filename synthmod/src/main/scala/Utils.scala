// Miscellaneous small utilities for debugging, float comparisons, etc

object debug {
  val debug = false
  def apply(expr: Boolean): Unit = {
    if (debug) {
      assert(expr)
    }
  }
  def apply(expr: String): Unit = {
    if (debug) {
      println(expr)
    }
  }
}

object MathUtils {
  implicit class approxEqual(x: Double) {
    def ~==(y: Double): Boolean = {
      if ((x - y).abs < 1e-8) true else false
    }
  }
}

object Operator extends Enumeration {
  type Operator = Value
  val Add, Sub, Mul, Neg = Value
}
