// Structure:
//
//  Input program ->
//    Constraint extraction ->
//    Query application ->
//        CANDIDATE SOLUTION GENERATION
//    -> Transformed model candidates


// Program + References
sealed trait Stmt
sealed trait Expr

case class Define(name: String, expr: Expr) extends Stmt
case class Var(reference: String) extends Expr
case class Ast(expr: AST) extends Expr

type Program = List[Stmt]


// Syntax tree (values, no references)
sealed trait AST

case class Pt(x: Double, y: Double)                               extends AST
case class Line(a: Pt, b: Pt)                                     extends AST
case class Arc(c: Pt, r: Double, t1: Double, t2: Double)          extends AST
case class Range(start: Int, end: Int, step: Int, fn: Int => AST) extends AST
case class Union(components: List[AST])                           extends AST


// Selections and Transformations
sealed trait Query
case class ClosestTo(point: Pt)               extends Query
case class Move(source: AST, vector: Pt)      extends Query // We will use points as vectors
case class BoundingBox(components: List[AST]) extends Query

// Example programs
object Examples {
  implicit val crossExample: AST =
    Union(
      List(
        Union(List(Line(Pt(1, -1), Pt(3, -1)), Line(Pt(3, -1), Pt(3, 1)), Line(Pt(3, 1), Pt(1, 1)))),
        Union(List(Line(Pt(1, 1), Pt(1, 3)), Line(Pt(1, 3), Pt(-1, 3)), Line(Pt(-1, 3), Pt(-1, 1)))),
        Union(List(Line(Pt(-1, 1), Pt(-3, 1)), Line(Pt(-3, 1), Pt(-3, -1)), Line(Pt(-3, -1), Pt(-1, -1)))),
        Union(List(Line(Pt(-1, -1), Pt(-1, -3)), Line(Pt(-1, -3), Pt(1, -3)), Line(Pt(1, -3), Pt(1, -1))))
      ))
}

// One-off geometric element
case class Axis(root: Pt, dir: Pt)

sealed trait Constraint
/// p1 and p2 are the same point. we use an integer representation so this is easy
case class Equal(p1: Pt, p2: Pt) extends Constraint
/// a1 is adjacent to a2 (the point of one is on the boundary of another).
case class Adjacent(a1: AST, a2: AST) extends Constraint
/// a1 is a2 but mirrored across axis
case class Symmetric(a1: AST, a2: AST, axis: Axis) extends Constraint
// Lines have particular properties we can check and enforce
case class Parallel(l1: Line, l2: Line)      extends Constraint
case class Perpendicular(l1: Line, l2: Line) extends Constraint

/// a1 and a2 are the same modulo an affine transform of their points
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
  def query(ast: AST, query:Query) : AST = ???

  // A transformed version of the AST after query execution
  def transform(ast: AST, query: Query) : AST = ???
}




object Main extends App {
  println("Hello World")
}

class Parser {
  def parse(input: String): AST = {
    ???
  }
}

object REPL {}
