#!/bin/bash
# Script pour désinstaller JCSP sur une machine sous GNU/Linux

sudo apt-get remove openjdk-6-jdk
rm -fr ~/JCSP

sed -i".bak" '/export JCSP_HOME=$HOME\/JCSP\/jcsp-1.1-rc4/d' ~/.bashrc 

source ~/.bashrc

echo "Désinstallation JCSP terminée"
