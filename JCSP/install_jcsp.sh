#!/bin/bash
# Script pour installer JCSP sur une machine sous GNU/Linux

sudo apt-get install openjdk-6-jdk

mkdir ~/JCSP
cd ~/JCSP
wget http://www.cs.kent.ac.uk/projects/ofa/jcsp/jcsp-1.1-rc4.jar
jar xf jcsp-1.1-rc4.jar
rm jcsp-1.1-rc4.jar

echo 'export CLASSPATH=$PATH:$.:~/JCSP/jcsp-1.1-rc4/jcsp.jar:~/JCSP/jcsp-1.1-rc4/jcsp-demos-util.jar' >> ~/.bashrc

echo "Installation JCSP terminée"