
import javax.swing._
import java.awt.event._
import java.awt.geom._
import javax.swing.event._
import java.awt.{RenderingHints,Rectangle,Dimension,Component,Container}
import java.awt.{Graphics,Graphics2D}
import ox.CSO._
import ox.cso.Components._
import scala.collection.mutable.{HashSet,HashMap}


package ox.cswing
{
  import event._
  //import cswing._
  import visual._

  object Viewer
  { 
    /**
        A <tt>Viewer</tt> maintains a composite image formed
        from the <tt>Visual</tt>s associated with a collection 
        of <tt>Thing</tt>s. All objects that are to be shown
        on a Viewer should inherit this trait.
    */
    trait Thing 
    { /** Return a Visual representing the current state of this thing */
      def image: Visual 
      /** The current layer of the image in which this thing should be shown:
          its default initial value is 3, and it must remain
          between 
      */
      var layer: Int = 3
      /**
          Number of times this thing has been hit. As an experimental
          feature, a mouse press on a thing causes it to be hit if 
          reportHits is true.
      */
      private var _hits: Int = 0
      def hits          = synchronized { _hits }
      def hit_=(n: Int) = synchronized { _hits=n }
      def hit           = synchronized { _hits = _hits + 1}
    }
    
    /** The number of layers permitted: higher numbered layers 
        appear in front of lower numbered layers. There is no
        rendering order within layers. In this version 
        the value of layers is 20.
    */
    val layers     = 20
    /** The front layer */
    val frontLayer = layers-1
    /** The back layer */
    val backLayer  = 0
    
    /**
        Construct a <tt>Thing</tt> from the given <tt>Visual</tt> with
        the default layer.
    */
    def thing(im: Visual) = new Thing { def image = im }
    
    /**
            Instructions for Viewer
    */
    trait Instruction {}
    /**
            Notify a Viewer that the state of <tt>t hing</tt> 
            in its collection has changed.
    */
    case  class  Alter  (thing: Thing)      extends Instruction {}
    /**
            Notify a Viewer that <tt>thing</tt> is
            to be added to its collection.
    */
    case  class  Add    (thing: Thing)      extends Instruction {}
    /**
            Notify a Viewer that <tt>thing</tt> is
            to be removed from its collection.
    */
    case  class  Remove (thing: Thing)      extends Instruction {}
    /**
            Notify a Viewer that its debugging state is to be changed
    */
    case  class  Debug  (state: Boolean)    extends Instruction {}
    /**
            Notify a Viewer that it is to regenerate and redisplay
            its composite image. A message such as this MUST
            be sent periodically if the view is to be kept 
            up-to-date. 
    */
    case  object Display                    extends Instruction {}
    /**
            Notify a Viewer that it is to remove all things from
            its collection and update its view accordingly.
    */
    case  object Clear                      extends Instruction {}
    /**
            Do nothing. 
    */
    case  object Skip                       extends Instruction {}
    

  }
  
  /**
      A <code>Viewer</code> is a minimalist
      animation/graphics container: it maintains a composite
      image formed from a collection of
      <code>Viewer.Thing</code>s. The collection can
      change dynamically, as can the states of any of
      the things in it.
       <p>    
      The expected mode of operation is to read
      <code>Instruction</code>s from its <code>in</code> port
      and send <code>Event</code>s to its <code>out</code>
      port. In contrast to standard ATW/Swing components
      it is not very configurable (and thus not
      very complicated to configure). Various Boolean
      attributes control what events it reports and whether
      it uses antialiased rendering.
      <p>
      <code>Thing</code>s are added to the container by sending an 
      <code>Add(thing: Thing)</code> 
      and are removed by sending a
      <code>Remove(thing: Thing)</code> 
      <code>Instruction</code>.
      <p>
      An <code>Viewer.Thing</code> is a mutable object that has
      an <code>Visual</code> and a layer index associated with
      it.  Both its <code>Visual</code> and its layer index may
      change dynamically, and the <code>Viewer</code>
      expects to be notified by an <code>Alter(thing: Thing)</code> 
      <code>Instruction</code> when this
      happens. 
      (In the present implementation <code>Add</code>
      and <code>Alter</code> are implemented in the same way
      and right now I cannot foresee a good reason for this to
      change.)
      <p>
      The viewer acts on messages it receives in ``extended rendezvous''
      with their senders. Thus a process sending <code>viewer!message</code> 
      through a synchronized (not buffered) <code>viewer</code>
      channel can be sure that the full effect of the
      message on the viewer has been completed when the <code>viewer!message</code>
      terminates.
  */
  class   Viewer( _name: String
                , _in:   ?[Viewer.Instruction]
                , _out:  OutPort[Event]
                )
  extends Widget[Viewer]
  { type Thing = Viewer.Thing
    import Viewer._
    
    private val out  = _out
    private val name = _name
    private val in   = _in
    
    /** When this is true mouse moves over this component(ie
        with no button pressed) are reported as
        <code>MouseMoved(name, (x,y), ...)</code>
        events.  All other mouse events are always reported as
        the appropriate kind of event.
    */
  
    var reportMoves : Boolean = false 
    
    /** When this is true, and the mouse is pressed within the
        bounding shape of one or more things, then a MouseHit event
        is generated containing all such things. 
    */
    var reportHits : Boolean = false 
    
    /** 
        When this is true keystrokes are reported, as Key events. 
    */
    var reportKeys : Boolean = false
    
    /** When this is true, anti-aliasing of the images is enabled. This
        is computationally expensive in some contexts.
    */
    var antiAlias = true
    
    /** When this is true, the box that bounds changes in the
        image since the last doDisplay is shown whenever the
        component is repainted
    */ 
    var debug = false
    
    /** Maps each thing to its most recent image */
    private val thingVisuals  = new HashMap[Thing,Visual]
    
    
    private def makeVisualLayers: Seq[HashSet[Visual]]  = 
            { val a = new Array[HashSet[Visual]](layers) 
              for (i<-0 until layers) a(i) = new HashSet[Visual]
              a
            }
    
    /** 
        The image layers that will be repainted during the
        next <code>repaintComponent</code>
    */
    
    private var imageLayers: Seq[HashSet[Visual]] = makeVisualLayers
    
    /** 
        The image layers currently being extracted from the mapping.
    */
    private var imageLayersBuf: Seq[HashSet[Visual]] = makeVisualLayers
    
    /** Mutex to protect the current imageLayers */
    private val imageLock = new AnyRef
    
    /** Switch the image layer buffers */
    private def switchVisualLayers = imageLock synchronized
    { val t          = imageLayers
      imageLayers    = imageLayersBuf
      imageLayersBuf = t
    }
    
             
    /** 
        Trigger a repaint if any changes in the
        effective composite image have been made. The
        implementation calculates the layers of the new
        composite image ready for the next
        <code>paintComponent</code> call.
    */
    private def doDisplay : Unit = 
    { if (changedBounds . nonEmpty)
      { 
        for (layer<-imageLayersBuf) layer.clear
        for (thing<-thingVisuals.keys) 
            imageLayersBuf(thing.layer)+=thingVisuals(thing)
        if (debug)
        { val bbox = 
              new Rect(changedBounds.x,       changedBounds.y, 
                       changedBounds.width-1, changedBounds.height-1)
              { override val solid=false }         
          imageLayersBuf(frontLayer) += bbox
        }
        switchVisualLayers
        repaint(changedBounds)
        clearBounds
      } 
    }
    
    private var focussed = false
    
    /** Bounding box of the current clip */
    private val clipBounds = new Rectangle()
    
    /** Paint this component */
    override def paintComponent(gr: Graphics) 
    { import RenderingHints._
      gr.getClipBounds(clipBounds)
      gr.clearRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height) 
      val g = gr.asInstanceOf[Graphics2D]  
      if (antiAlias)  
      { g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON)
        g.setRenderingHint(KEY_ANTIALIASING,      VALUE_ANTIALIAS_ON)
      }
      imageLock synchronized 
      {
        for (layer<-imageLayers; image<-layer) image.draw(g, clipBounds)
      }
      g.setColor(if (focussed) getBackground.darker else getBackground.brighter) 
      g.drawRect(0, 0, getWidth-1, getHeight-1)
    }
    
    class Bounds extends Rectangle
    { var nonEmpty : Boolean  = false
      def clear: Unit = nonEmpty = false
      def += (r: Rectangle) = 
          if (nonEmpty) 
             add(r)
          else 
          {  nonEmpty = true
             setBounds(r) 
          }
    }
    
    /** Bounding rectangle of the changes to the composite
        image since the last repaint was triggered. 
    */
    private val changedBounds: Bounds = new Bounds
    
    /** Clear the changes-bounding rectangle */
    private def clearBounds = 
    { changedBounds.clear }
    
    
    /** Note that a Thing's image has changed, and modify the changed
        bounds accordingly. 
    */
    private def changeThing(thing: Thing) : Unit =
    {   thingVisuals.get(thing) match
        { case Some(image) => changedBounds += image.getBounds
          case None        => {}   
        }
        changedBounds += thing.image.getBounds
    }
    
    /** Bounding box of the component when it was last cleared */
    private val currentBounds = new Rectangle()
  
    /** Listen to instructions from the world. This process must be started 
        at the application level if the viewer is to be run as part of an
        animation system.
    */
    val process = proc
    { import Viewer._
      repeat
      { in ? // extended rendezvous
        { case Display       => { doDisplay }
          case Skip          => { }
          case Remove(thing) => 
               { changeThing(thing); thingVisuals -= thing }
          case Alter(thing)  => 
               { changeThing(thing); thingVisuals.update(thing, thing.image) }
          case Add(thing)    => 
               { changeThing(thing); thingVisuals.update(thing, thing.image) }
          case Debug(state)  => 
               { debug = state }
          case Clear         => 
          {  
             thingVisuals.clear
             changedBounds += getBounds(currentBounds)
             doDisplay
          }
        }
        ()
      }
    }
    
    /** 
        Borders may not be set on this component; the implementation
        silently ignores any request to do so.
    */
  
    override def setBorder(border: javax.swing.border.Border) { }
    
    /** Handler for mouse events */
    val mouseInput = new MouseInputProxy
    { val out  = _out
      val name = _name
      
      override def mouseMoved(ev: MouseEvent) =
               if (reportMoves) super.mouseMoved(ev)
      
      override def mousePressed(ev: MouseEvent) =
               if (reportHits) 
               { val hits: Iterable[Thing] = 
                     for (thing<-thingVisuals.keys 
                          if (thingVisuals(thing).contains(ev.getPoint)) 
                         ) yield { thing.hit; thing }
                 if (hits.length==0)
                     super.mousePressed(ev)
                 else
                     out!MouseHit(name, (ev.getX, ev.getY), ev.getButton, ev.getModifiersEx, hits)
               }
               else
                  super.mousePressed(ev)
               
      override def mouseEntered(ev: MouseEvent) {  requestFocusInWindow() }
     
      override def mouseExited(ev: MouseEvent)  { }
    }
    
    /** Handler for key presses */
    val keyInput = new KeyAdapter()
    { override def keyPressed(ev: KeyEvent) 
      { if (reportKeys) out!Key(name, ev.getKeyCode, ev.getModifiers) 
      }
    }
    
    /** Handler for component events */
    val componentInput = new ComponentListener()
    { def componentHidden (ev: ComponentEvent)   { out!Hidden(name)  }
      def componentMoved  (ev: ComponentEvent)   { out!Moved(name)   }
      def componentResized(ev: ComponentEvent)   { out!Resized(name) }
      def componentShown  (ev: ComponentEvent)   { out!Shown(name)   }
    }
  
    /* Set various Swing parameters, borders, etc. */
    setDoubleBuffered(true)
    setEnabled(true)
    setOpaque(true)
    setPreferredSize(new Dimension(400, 300))   
    addKeyListener         (keyInput) 
    addMouseListener       (mouseInput) 
    addMouseMotionListener (mouseInput)  
    addComponentListener   (componentInput)  
    setFocusable(true)
      
    /* Show visible evidence when we have the focus */
    (addFocusListener
     ( new FocusListener()
       { def focusGained(ev: FocusEvent) { focussed = true;  repaint() }
         def focusLost(ev: FocusEvent)   { focussed = false; repaint() }
       }
     )
    ) 
  }
    
}
















