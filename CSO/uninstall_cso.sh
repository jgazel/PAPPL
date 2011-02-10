rm -fr ~/Scala
rm -fr ~/ScalaLocal
rm antlib.xml build.xml NOTICE.txt README.txt README.txt~~ ARTISTIC.txt PRELUDE.txt  README.txt~ 
rm -fr src
rm -fr CSwing 
sed -i".bak" '/export PATH=$PATH:${SCALA_HOME}\/bin/d' ~/.bashrc 
sed -i".bak" '/export SCALA_HOME="~\/Scala/d' ~/.bashrc     

