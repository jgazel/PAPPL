package helloworld
import ox.CSO._
import ox.Format._
import ox.cso.Components.{console}

object Final {

    var enVie = true
    var N = 5
    val pipe = OneOne[String](N)

    def main( args: Array[String] ) {
        println("Début du programme Hello")
 
        var senders =  for (i <- 0 until N) yield new Sender(i, pipe(i))
        var receivers = for (i <- 0 until N) yield new Receiver(i, pipe(i))
        
        ( 
           || ( for (i <- 0 until N) yield senders(i).process )
        || 
           || ( for (i <- 0 until N) yield receivers(i).process )
        )()
    }

    class Sender(i: Int, out: ![String])
    {
        val process : PROC =
        { 
            println("Création d'un sender")
            repeat 
            {
                var message = "Mon Objet"
                println(">> [" + i + "] Envoi de \"" + message + "\"...")
                out!message
                sleep(500)
            }
        }  
    }

    class Receiver(i: Int, in: ?[String])
    {
        val process : PROC =
        { 
            println("Création d'un receiver")
            var message =""
            repeat 
            {
                message=in ?; 
                println(" [" + i + "] \"" + message + "\" reçu.")
            }
         }  
    }   
}

