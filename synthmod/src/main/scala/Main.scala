import scala.collection.immutable._
import java.{util => ju}
import Operator._
import MathUtils._

// Structure:
//
//  Input program ->
//    Constraint extraction ->
//    Query application ->
//        CANDIDATE SOLUTION GENERATION
//    -> Transformed model candidates

// Strategies for generating candidate solutions:
// "Constraint repair"
//  - Try to fix constraints.
//      Some are hard, local (perp, par)
//      Some are soft, global (symmetries, affine)
//  -> Leads to a few ambiguous options.
//
// "Parameter merging"
//  - Reduce the dimensionality of the program's input by merging
//    its constants into parameters and small expressions.
//  - Then, try adjusting these found-parameters and see where model goes.
//
// "Pattern finding"
// - Similar to parameter merging... attempt to recast some elements
//   as transforms of other elements.

// Syntax tree (values, no references)
sealed trait AST

// Parameter Language (Arithmetic)
sealed trait Parameter
case class Literal(value: Double)                    extends Parameter
case class Reference(value: String)                  extends Parameter
case class Operation(op: Operator, args: Parameter*) extends Parameter

// Geometric Language (Constructions)
case class Pt(x: Parameter, y: Parameter)                         extends AST
case class Line(a: Pt, b: Pt)                                     extends AST
case class Arc(c: Pt, r: Parameter, t1: Parameter, t2: Parameter) extends AST
case class Range(start: Int, end: Int, step: Int, fn: Int => AST) extends AST
case class Union(components: Array[AST])                          extends AST

// Selections and Transformations
sealed trait Query
case class ClosestTo(point: Pt)                extends Query
case class Move(source: AST, vector: Pt)       extends Query // We will use points as vectors
case class BoundingBox(components: Array[AST]) extends Query

// Example programs
object Examples {
  // Some helper construction functions
  def polyline(pts: (Double, Double)*): AST = {
    val ps = pts.map { case (x, y) => Pt(Literal(x), Literal(y)) }.toArray
    val ls = ps.dropRight(1).zipWithIndex.map { case (p, i) => Line(p, ps(i + 1)): AST }
    Union(ls)
  }

  implicit val crossExample: AST =
    Union(
      Array(
        polyline((1, -1), (3, -1), (3, 1), (1, 1)),
        polyline((1, 1), (1, 3), (-1, 3), (-1, 1)),
        polyline((-1, 1), (-3, 1), (-3, -1), (-1, -1)),
        polyline((-1, -1), (-1, -3), (1, -3), (1, -1))
      )
    )
}

class Context(var known_variables: Map[Parameter, String]) {

  def add(value: Double): Parameter = {
    known_variables.get(Literal(value)) match {
      case None => {
        val size   = known_variables.size
        val newVar = s"var$size"
        known_variables += ((Literal(value), newVar))
        Reference(newVar)
      }
      case Some(variable) => {
        Reference(variable)
      }
    }
  }

  def addDefinition(parameter: Parameter): Parameter = {
    parameter match {
      case Literal(value) => add(value)
      case _              => parameter
    }
  }

  // Perform parameter-merging operations.
  def meld(): Unit = {
    meldNegatives()
    // Todo: more rewrites
  }

  // Perform a rewrite where values that are negative versions
  // of other values are unified into a single value and a derived expression of that value.
  def meldNegatives(): Unit = {
    var new_variables = new HashMap[Parameter, String]
    var eject_vars    = new HashSet[Parameter]

    for ((k1, v1) <- known_variables) {
      for ((k2, v2) <- known_variables if k1 != k2) {
        (k1, k2) match {
          case (Literal(x), Literal(y)) if x ~== -y => {
            debug(s"matched ($x,$y)")
            def operate(k1: Parameter, v1: String, k2: Parameter, v2: String) = {
              if (!((new_variables contains k1) || (new_variables contains k2))) {
                val rebound_k2 = Operation(Neg, Reference(v1))
                new_variables += ((k1, v1), (rebound_k2, v2))
                eject_vars += (k2)
              }
            }
            if (x > y) operate(k1, v1, k2, v2) else operate(k2, v2, k1, v1)
          }
          case _ =>
        }
      }
    }
    // println(new_variables)
    for (k      <- eject_vars) { known_variables -= (k) }
    for ((k, v) <- new_variables) { known_variables += ((k, v)) }
    debug(verifyBijection)
  }

  // Verify that each value has only one key
  def verifyBijection: Boolean = {
    known_variables.size == known_variables.values.toSet.size
  }

  def inverted: Map[String, Parameter] = {
    for ((k, v) <- known_variables) yield (v, k)
  }
}

object Unifier {

  // Implement specific unifiers for certain types so that
  // we can recursively replace parameters and know that the replaced
  // object will still be of the same type so we can reassemble them.
  implicit class PointUnifier(pt: Pt) {
    def unify(ctx: Context): Pt = {
      val xt = ctx.addDefinition(pt.x)
      val yt = ctx.addDefinition(pt.y)
      Pt(xt, yt)
    }
  }

  implicit class LineUnifier(line: Line) {
    def unify(ctx: Context): Line = {
      val p1 = line.a.unify(ctx)
      val p2 = line.b.unify(ctx)
      Line(p1, p2)
    }
  }

  // Does a pass at unifying the parameters in a syntax tree from literals.
  def unify(ast: AST): (AST, Context) = {
    def unifyRec(ctx: Context, ast: AST): AST = {
      ast match {
        case p: Pt   => p.unify(ctx)
        case l: Line => l.unify(ctx)
        case Union(components) => {
          for (i <- 0 until components.size) {
            components(i) = unifyRec(ctx, components(i))
          }
          Union(components)
        }
        case a: Arc   => ???
        case r: Range => ???
      }
    }

    val ctx  = new Context(new HashMap)
    val tree = unifyRec(ctx, ast)
    (tree, ctx)
  }

}

// One-off geometric element
case class Axis(root: Pt, dir: Pt)

sealed trait Constraint
/// p1 and p2 are the same point.
case class Equal(p1: Pt, p2: Pt) extends Constraint
/// a1 is adjacent to a2 (the point of one is on the boundary of another).
case class Adjacent(a1: AST, a2: AST) extends Constraint
/// a1 is a2 but mirrored across axis
case class Symmetric(a1: AST, a2: AST, axis: Axis) extends Constraint
// Lines have particular properties we can check and enforce
case class Parallel(l1: Line, l2: Line)      extends Constraint
case class Perpendicular(l1: Line, l2: Line) extends Constraint

// a1 and a2 are the same modulo an affine transform of their points
// in practice we don't want the ENTIRE affine space (all line segments
// will map to each other!) but we can maybe do translation + rotation.
case class AffineRepeated() extends Constraint

object Analyzer {
  // Extract constraints present in the input from a syntax tree.
  // Some of these will exist by construction, others of these
  // will be incidental in the current parameterization.
  def analyze(ast: AST): Set[Constraint] = ???

  // There is also a version of satisfies we can write that returns an optimality,
  // i.e. "it's okay but not really perfect"
  def satisfies(ast: AST, constraint: Constraint): Boolean = ???
}

object Transformer {
  // Extract the sub-segment of the AST that responds to the query
  def query(ast: AST, query: Query): AST = ???

  // A transformed version of the AST after query execution
  def transform(ast: AST, query: Query): AST = ???
}

object Main extends App {
  val cross      = Examples.crossExample
  val (ast, ctx) = Unifier.unify(cross)
  ctx.meld()
  println(ctx.inverted)
}

class Parser {
  def parse(input: String): AST = {
    ???
  }
}

object REPL {}
