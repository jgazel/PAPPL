package gazel_schiano_cso
import ox.CSO._

object ProdCons {
    val NP = 10
    val NC = 4
    val N = 4
    var enVie = true

    def main( args: Array[String] ) {
        
        println("Début du programme Producteur-Consommateur")
        ( 
           || ( for (i <- 0 until NP) yield Producteur(i) )
        || 
           || ( for (i <- 0 until NC) yield Consommateur(i) )
        )()
    }

    def Producteur(i : Int) : PROC = {
        var nb = 0
        var message = ""
        println("Création d'un producteur (" + i + ") :: enVie? " + enVie)
        repeat(enVie) {
            println("yep")
            sleep(500)
        }

    }

    def Consommateur(i : Int) : PROC = {
        var k = 0
        println("Création d'un consommateur (" + i + ") :: enVie? " + enVie)
        
        repeat(enVie) {
            println("nop")
            sleep(1000)
        }

    }  
}

