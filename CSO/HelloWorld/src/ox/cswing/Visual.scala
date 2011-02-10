package ox.cswing
import  java.awt.{Graphics2D, Rectangle, Shape, Point}

/**
        Root trait for the visual objects used by ox.cswing.Viewer
        <pre>
        $Id: Visual.scala 505 2010-08-16 18:09:23Z sufrin $
        </pre>
*/
trait Visual
{ val shape: java.awt.Shape
  
  def draw(gr: Graphics2D, clip: Rectangle) : Unit
  
  def getBounds   : Rectangle   = 
  { val r = shape.getBounds   
    r.grow(1,1)
    r
  }
  
  def contains(point: Point) = shape.contains(point)
} 








