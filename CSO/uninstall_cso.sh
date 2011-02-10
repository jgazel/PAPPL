#!/bin/bash
# Script pour désinstaller CSO sur une machine sous GNU/Linux
# dont l'installation a été faite par install_cso.sh
# Ecrit par Jérôme GAZEL - Ecole Centrale de Nantes - 2011

rm -fr ~/Scala
rm -fr ~/ScalaLocal
rm antlib.xml build.xml NOTICE.txt README.txt README.txt~~ ARTISTIC.txt PRELUDE.txt  README.txt~ 
rm -fr src
rm -fr CSwing 
sed -i".bak" '/export PATH=$PATH:${SCALA_HOME}\/bin/d' ~/.bashrc 
sed -i".bak" '/export SCALA_HOME="~\/Scala/d' ~/.bashrc     

