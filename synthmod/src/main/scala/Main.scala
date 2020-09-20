// Structure:
//
//

sealed trait AST

case class Point(x: Double, y: Double) extends AST
case class Line(a: Point, b: Point) extends AST
case class Arc(c: Point, r: Double, t1: Double, t2: Double) extends AST
case class Range(start: Int, end: Int, step: Int, fn: Int => AST) extends AST
case class Union(components: List[AST]) extends AST

object Main extends App {
  println("Hello World")
}


class Parser {
  def parse(input: String): AST = {
    ???
  }
}


object REPL {

}


