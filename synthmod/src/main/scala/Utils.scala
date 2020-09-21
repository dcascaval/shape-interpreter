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

object VariationUtils {
  class ChooseSeq(bound: Int, arity: Int) {
    var values: Array[Int] = (0 until arity).toArray

    def hasNext: Boolean = {
      values.exists(i => i != bound)
    }

    def apply(i: Int) = values(i)

    def increment(): Unit = {
      def inc(n: Int): Unit = {
        if (values(n) != bound) {
          values(n) += 1
        } else if (n > 0) {
          values(n) = 0
          inc(n - 1)
        }
      }
      inc(arity - 1)
    }
  }

  def choose[T](list: List[Array[T]], choices: ChooseSeq): List[T] = {
    list.zipWithIndex.map { case (a, i) => a(choices(i)) }
  }

  implicit class WithValues[A, B](ctx: Map[A, B]) {
    def withValues(params: Seq[(A, B)]): Map[A, B] = {
      var current = ctx
      for (p <- params) {
        current += (p)
      }
      current
    }
  }
}
