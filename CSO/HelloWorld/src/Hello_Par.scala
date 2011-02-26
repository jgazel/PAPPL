package helloworld
import ox.CSO._

object Hello_Par {

    val NP = 3
    val NC = 4
    
    def main( args: Array[String] ) {
        
        println("Début du programme HelloWord_Par :")
        ( 
           || ( for (i <- 0 until NP) yield Hello(i) )
        || 
           || ( for (i <- 0 until NC) yield World(i) )
        )()
    }


    def Hello( i: Int ) : PROC={
    	say("Hello" + i)
    } 

    def World( i: Int ) : PROC={
    	say("World" + i)
    }

    def say( word: String ) {
      println(word)
    }  
}

