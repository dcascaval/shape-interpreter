import processing.core._
import PConstants._
import PApplet._
import processing.event.KeyEvent

object DisplayData {
  var currentAST            = 0
  var ASTs: IndexedSeq[AST] = IndexedSeq.empty[AST]

  def setASTs(asts: IndexedSeq[AST]) = {
    ASTs = asts
    currentAST = 0
  }

  def setCurrentAST(index: Int) = {
    currentAST = Math.min(index, ASTs.size - 1)
  }

  def current: AST = {
    ASTs(currentAST)
  }

}

class Display extends PApplet {
  val WIDTH  = 800
  val HEIGHT = 600

  def mapTo(x: Double, y: Double): (Float, Float) = {
    (WIDTH / 2 + (x.toFloat / 5) * WIDTH / 2, HEIGHT / 2 + (y.toFloat / 4) * HEIGHT / 2)
  }

  def drawAST(ast: AST): Unit = {
    stroke(255, 255, 255)
    strokeWeight(2)
    ast match {
      case Union(components) => components.foreach(drawAST)
      case Line(
            Pt(Literal(ax), Literal(ay)),
            Pt(Literal(bx), Literal(by))
          ) => {
        val (sx, sy) = mapTo(ax, ay)
        val (ex, ey) = mapTo(bx, by)
        line(sx, sy, ex, ey)
      }
      case _ => ???
    }
  }

  // Don't redraw when not needed.
  var changed = true

  override def keyPressed(k: KeyEvent): Unit = {
    val code = k.getKeyCode()
    if (code >= 0x31 && code <= 0x39) {
      changed = true
      DisplayData.setCurrentAST(code - 0x31)
    }
  }

  override def settings(): Unit = {
    size(WIDTH, HEIGHT, P3D)
  }

  override def draw() = {
    if (changed) {
      background(64);
      drawAST(DisplayData.current)
      textSize(15)
      text(s"Displaying Option ${DisplayData.currentAST + 1} of ${DisplayData.ASTs.size} ASTs", 30, 30)
      changed = false
    }
  }
}
