package gazel_schiano_cso
import ox.CSO._

object ProdCons {
    val NP = 4
    val NC = 1
    val N = 4
    val enVie = true

    val left, mon, right = Chan[T]

    def main( args: Array[String] ) {
        (|| ( for (i <- 0 until NP) ) yield Producteur(i, left)
         || ( for (i <- 0 until NC) ) yield Consommateur(i, right)
         || Buffer(mon) 
        )()
    }

    def Producteur(i : int, out : ![T]) : PROC = {
        val nb = 0
        val message = ""

            repeat(enVie) {
                message = "Objet numéro " + nb
                out ! message
                print ("Production " + i + " : " + message + "\n");
                nb = nb + 1
            }
    }

    def Consommateur(i : int, in : ?[T]) : PROC = {
        val k = 0
        
            repeat(enVie) {
                in ? { message => 
                        {
                            mon ! message ;
                            right! message ;
                            println("Consommateur " + i + " : " + message + "\n")
                        } 
                    }
                //sleep(500*i)
            }
    }  
    

    def  Buffer( tuyau : ?[T] ) : PROC = {
         tuyau ? { v => {println(v)}}
    }
}
