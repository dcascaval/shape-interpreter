name := "synthmod"

version := "0.2"

scalaVersion := "2.13.3"

classpathTypes ++= Set("jnilib")

resolvers += MavenRepository("jogamp", "http://jogamp.org/deployment/maven")

libraryDependencies += "org.jogamp.jogl"    % "jogl-all"   % "2.3.2"
libraryDependencies += "org.jogamp.jogl"    % "newt"       % "2.3.2"
libraryDependencies += "org.jogamp.gluegen" % "gluegen-rt" % "2.3.2"

fork in run := true
