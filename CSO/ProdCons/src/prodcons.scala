package gazel_schiano_cso
import ox.CSO._

object ProdCons {
    val NP = 4
    val NC = 1
    
    val left,right = ManyMany[String]

    def main( args: Array[String] ) {
        
        println("Debut du programme Producteur-Consommateur (" + NP + "," + NC + ").")
        
        // Instanciation
        val prods =  for (i <- 0 until NP) yield new Producteur(i, left)
        val cons = for (i <- 0 until NC) yield new Consommateur(i, right)
        val tampon = new Buffer(left,right)
        
        // Lancement en parallèle des processus
        ( 
           || ( for (i <- 0 until NP) yield prods(i).produire )
        || 
           || ( for (i <- 0 until NC) yield cons(i).consommer )
        || 
           tampon.process 
        )()
    }

    class Producteur(i: Int, out: ![String]) {
        def produire : PROC = {
            var nb = 0
            var message = ""
            println("Creation d'un producteur (" + i + ").")
            
            repeat 
            {
                sleep(500)
                message = "Objet numero " + nb
                out ! message
                println("Production " + i + " : " + message + "...>>")
                nb = nb + 1
            }
        }
    }

    class Consommateur(i: Int, in: ?[String]) {
        def consommer : PROC = {
            println("Creation d'un consommateur (" + i + ").")
            var message =""
            repeat
            {
                //in ? { message => 
                //        {
                //            println("<<Consommation " + i + " : " + message)
                //        } 
                //    }
                sleep(500)
                var message =""
                repeat 
                    {
                    message=in ?; 
                    println("<<Consommation " + i + " : " + message)
                }
             }
        }
    }  
    
    class Buffer( in: ?[String], out: ![String]) {
        def process : PROC = {
            println("Entree Buffer")
            //in ? { v => {out ! v}}
            var message =""
            repeat 
            {
                message=in ?; 
                out!message
            }
        }
    }
    
}


