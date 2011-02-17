package gazel_schiano_cso
import ox.CSO._

object ProdCons {
    val NP = 4
    val NC = 1
    val N = 4
    var enVie = true

    val left, mon, right = OneOne[String]

    def main( args: Array[String] ) {
        
        println("Début du programme Producteur-Consommateur")
        ( 
           || ( for (i <- 0 until NP) yield Producteur(i, left) )
        || 
           || ( for (i <- 0 until NC) yield Consommateur(i, right) )
        || 
           Buffer(mon) 
        )()
    }

    def Producteur(i : Int, out : ![String]) : PROC = {
        var nb = 0
        var message = ""
        println("Création d'un producteur (" + i + ") :: enVie? " + enVie)
        
        repeat(enVie) {
            println("   Boucle Producteur -- Objet numéro " + nb)
            sleep(i)
            message = "Objet numéro " + nb
            println(message)
            out ! message
            println("Production " + i + " : " + message)
            nb = nb + 1
        }
    }

    def Consommateur(i : Int, in : ?[String]) : PROC = {
        var k = 0
        println("Création d'un consommateur (" + i + ") :: enVie? " + enVie)
        
        repeat(enVie) {
            println("   Boucle Consommateur")
            in ? { message => 
                    {
                        mon ! message 
                        right! message 
                        println("Consommateur " + i + " : " + message)
                    } 
                }
            //sleep(500*i)
        }
    }  
    

    def  Buffer( tuyau : ?[String] ) : PROC = {
        println("Entrée Buffer")
        tuyau ? { v => {println(v)}}
    }
    
}

