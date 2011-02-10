package jerome_cso
import ox.CSO._

object Hello_CSO {
    val n = 3

    def main( args: Array[String] ) {
        say("Hello_CSOprogram")
    }

    def say( word: String ) {
      println(word)
    }
    
    
    
    trait Message {}
    case object Ping extends Message {}
    case class Data[T] ( data: T ) extends Message {}
    
    
}
