/**
        Simple pictorial objects for use in graphical applications
*/

import java.awt.geom._
import java.awt.{Graphics2D, Color, Rectangle, Component}
import javax.swing.ImageIcon


package ox.cswing.visual
{ 
  import ox.cswing.Visual
  
  class Elements {} // Placeholder to prevent spurious recompilation
  
  class Ellipse(x: Double, y: Double, w: Double, h: Double) extends ShapeVisual
  { override val shape = new Ellipse2D.Double(x, y, w, h) 
  }
  
  class Rect(x: Double, y: Double, w: Double, h: Double) extends ShapeVisual
  { override val shape = new Rectangle2D.Double(x, y, w, h) 
  }
  
  class   Icon(x:Int, y:Int, icon: javax.swing.ImageIcon, component: Component) 
  extends Visual
  { 
    val shape = new Rectangle(x, y, icon.getIconWidth, icon.getIconHeight)

    def draw(gr: Graphics2D, clip: Rectangle) : Unit =
    {  
       icon.paintIcon(component, gr, x, y)
    }
  }

  abstract class ShapeVisual extends Visual
  { val color: Color          = Color.black
    val solid: Boolean        = true
    
    def draw(gr: Graphics2D, clip: Rectangle) : Unit =
    { if (gr.hit(clip, shape, !solid))
      { gr.setColor(color)
        if (solid) gr.fill(shape) else gr.draw(shape)
      }
    }
    
  }  

  object ImageIcon
  {  
    type ImageIcon = javax.swing.ImageIcon
    def  imageIcon(file: String)      = new javax.swing.ImageIcon(file)
    def  imageIcon(url: java.net.URL) = new javax.swing.ImageIcon(url)  
  }
  
}








