wget http://www.scala-lang.org/downloads/distrib/files/scala-2.8.1.final.tgz

tar -xvzf scala-2.8.1.final.tgz
rm scala-2.8.1.final.tgz
mv scala-2.8.1.final ~/Scala
export SCALA_HOME="~/Scala"
export PATH="${SCALA_HOME}/bin:${JAVA_HOME}/bin:${PATH}"
mkdir ~/ScalaLocal

wget http://users.comlab.ox.ac.uk/bernard.sufrin/CSO/cso-sources-scala2.8.0.tgz
tar -xvf cso-sources-scala2.8.0.tgz
rm cso-sources-scala2.8.0.tgz

mv scalatasks.xml ~/ScalaLocal/
scala -version
