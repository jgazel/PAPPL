package gazel_schiano_cso
import ox.CSO._

object ProdCons {
    val NP = 4
    val NC = 1
    val N = 4
    var enVie = true

    val left, mon, right = OneOne[String]

    def main( args: Array[String] ) {
        (|| (for (i <- 0 until NP) yield Producteur(i, left) )
         || (for (i <- 0 until NC) yield Consommateur(i, right) )
         || Buffer(mon) 
        )()
    }

    def Producteur(i : Int, out : ![String]) : PROC = {
        var nb = 0
        var message = ""

            repeat(enVie) {
                message = "Objet numéro " + nb
                out ! message
                println("Production " + i + " : " + message)
                nb = nb + 1
            }
    }

    def Consommateur(i : Int, in : ?[String]) : PROC = {
        var k = 0
        
            repeat(enVie) {
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
         tuyau ? { v => {println(v)}}
    }
}

