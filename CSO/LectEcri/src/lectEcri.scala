package gazel_schiano_cso
import ox.CSO._

object LectEcri {
    var NL = 4
    var NE = 2
    var ressource = 0;
    
    val pipeEcriAcces, pipeEcriLibere ,pipeLectAcces, pipeLectLibere = ManyMany[String]
    
    def main( args: Array[String] ) {
        
        println("Debut du programme Lecteur-Ecrivain (" + NL + "," + NE + ").")
        
        // Instanciation
        val lecteurs =  for (i <- 0 until NL) yield new Lecteur(i, pipeLectAcces,pipeLectLibere)
        val ecrivains = for (i <- 0 until NE) yield new Ecrivain(i, pipeEcriAcces, pipeEcriLibere)
        val controleur = new Controleur(pipeEcriAcces, pipeEcriLibere ,pipeLectAcces, pipeLectLibere )
        
        // Lancement en parallèle des processus
        ( 
           || ( for (i <- 0 until NL) yield lecteurs(i).process )
        || 
           || ( for (i <- 0 until NE) yield ecrivains(i).process )
        || 
           controleur.process 
        )()
    }

    class Lecteur(i: Int, outA: ![String],outL: ![String]) {
        def process : PROC = {
            println("Creation du lecteur (" + i + ").")
            repeat 
            {
                sleep(500)
                println("Demande de lecture " + i + " ...")
                outA ! "."
                println("ressource : " + ressource + " [lecteur " +  i + "]")
                outL ! "."
            }
        }
    }

    class Ecrivain(i: Int, outA: ![String],outL: ![String]) {
        def process : PROC = {
            println("Creation de l'ecrivain (" + i + ").")
            var message =""
            repeat
            {
                sleep(500)
                println("Demande d'écriture " + i + " ...")
                outA ! "."
                ressource = ressource +1
                println("ressource : " + ressource + " [ecrivain " +  i + "]")
                outL ! "."
             }
        }
    }  

    
    class Controleur( inLectAcces: ?[String], inLectLibere: ?[String], inEcriAcces: ?[String], inEcriLibere: ?[String]) {
        def process : PROC = {
            println("Debut du controleur")
            (
                proc{ repeat{ val v=inLectAcces ?; NL = NL + 1; println("Nombre de lecteurs : " + NL + "\n") } }
            ||
                proc{ repeat{ val v=inLectLibere ?; NL = NL - 1 } }
            ||
                proc{ repeat{ val v=inLectAcces ?; var e=inLectLibere ? } }     
            )()
        }
    }
    
}


