package jerome_cso
import ox.CSO._

object Hello_CSO {
    def main( args: Array[String] ) {
     
    ( Hello_CSO.Hello
     || 
      Hello_CSO.World
    )()

    }


    def Hello( ) : PROC={
	say("Hello")
    }

    def  World( ) : PROC={
	say("World")
    }

    def say( word: String ) {
      println(word)
    }  
}