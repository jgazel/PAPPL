import ox.CSO.OutPort

package ox.cswing.event
{  import ox.cswing.Viewer.Thing


/**

        <tt>AbstractEvent</tt> is the ancestor of all event-message classes.

        In <code>cswing</code> events are coded as messages
        that are sent down channels by their originating components.
        <p>
        The package
        <code>ox.cswing.event</code> defines
        the messages generated 
        by <code>ox.cswing</code> components.
        <pre>
        $Id: Event.scala 505 2010-08-16 18:09:23Z sufrin $
        </pre>
        <p> 

   */
   class Event(_name: String) { def getName = _name }

   /**   Ancestor of all events sent by components that respond to input */
   class   InputEvent(name: String, _mods: Int)
   extends Event(name)
   { import java.awt.event.InputEvent._
     def isControlDown  = (_mods & CTRL_DOWN_MASK)      == CTRL_DOWN_MASK
     def isShiftDown    = (_mods & SHIFT_DOWN_MASK)     == SHIFT_DOWN_MASK
     def isAltDown      = (_mods & ALT_DOWN_MASK)       == ALT_DOWN_MASK
     def isAltGraphDown = (_mods & ALT_GRAPH_DOWN_MASK) == ALT_GRAPH_DOWN_MASK
     def isMetaDown     = (_mods & META_DOWN_MASK)      == META_DOWN_MASK
   }
   
   /** Keyboard event happened in the named widget */
   case  class Key(name: String, value:Int, mods: Int)
         extends InputEvent(name, mods)
   
   /** The mouse moved in the named widget */
   case  class MouseMoved(name: String,   coord:(Int,Int),   but:Int, mods: Int)
         extends InputEvent(name, mods)
   
   /** The mouse was dragged in the named widget */
   case  class MouseDragged(name: String,  coord:(Int,Int),  but:Int, mods: Int)
         extends InputEvent(name, mods)
   
   /** The mouse was pressed in the named widget */
   case  class MousePressed(name: String,  coord:(Int,Int),  but:Int, mods: Int)
         extends InputEvent(name, mods)
   
   /** The mouse was pressed in a location in the named
       (Viewer) widget which was inside each of the
       <code>things</code>. 
   */
   case  class MouseHit(name: String,  coord:(Int,Int),  but:Int, mods: Int, 
                        things: Iterable[Thing])
         extends InputEvent(name, mods)
   
   /** The mouse was released in the named widget */
   case  class MouseReleased(name: String, coord:(Int,Int),  but:Int, mods: Int)
         extends InputEvent(name, mods)
   
   case  class MouseEntered(name: String,  coord:(Int,Int),  but:Int, mods: Int)
         extends InputEvent(name, mods)
   
   case  class MouseExited(name: String,   coord:(Int, Int), but:Int, mods: Int)
         extends InputEvent(name, mods)

   /**   Ancestor of all events sent by components when their
         visibility changes
   */
   class   ComponentVisibilityEvent(name: String)
   extends Event(name) {}

   case  class Hidden(name: String)
         extends ComponentVisibilityEvent(name) {}

   case  class Moved(name: String)
         extends ComponentVisibilityEvent(name) {}

   case  class Resized(name: String)
         extends ComponentVisibilityEvent(name) {}

   case  class Shown(name: String)
         extends ComponentVisibilityEvent(name) {}

}







