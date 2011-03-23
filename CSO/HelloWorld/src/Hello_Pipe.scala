package helloworld
import ox.CSO._
import ox.Format._
import ox.cso.Components.{console}

object Hello_Pipe {

    var enVie = true

    val pipe = ManyMany[String]

    def main( args: Array[String] ) {
        println("Début du programme Hello_Pipe")
        var sender = new Sender(pipe)
        var receiver = new Receiver(pipe)
        ( 
           sender.process
        || 
           receiver.process
        )()
    }

    class Sender(out: ![String])
    {
        val process : PROC =
        { 
            println("Création d'un sender")
            repeat 
            {
                var message = "Mon Objet"
                println(">> Envoi de \"" + message + "\"...")
                out!message
                sleep(500)
            }
        }  
    }

    class Receiver(in: ?[String])
    {
        val process : PROC =
        { 
            println("Création d'un receiver")
            var message =""
            repeat 
            {
                in ? { message => { println("<<Reception : " + message) }  }
                //message=in ?; 
                //println("\"" + message + "\" reçu.")
            }
         }  
    }   
}


