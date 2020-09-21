object PrettyPrinters {
  implicit class printParam(p: Parameter) {
    def print: String = {
      p match {
        case Literal(value) => value.toString()
        case s              => s.toString()
      }
    }
  }

  implicit class printAST(ast: AST) {
    def print: String = {
      ast match {
        case Pt(a, b)          => s"Pt(${a.print},${b.print})"
        case Line(a, b)        => s"Line(${a.print},${b.print})"
        case Union(components) => s"Union(${components.map(_.print).mkString(", ")})"
      }
    }
  }
}
