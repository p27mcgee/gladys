# gladys

[![Build Status](https://travis-ci.com/p27mcgee/gladys.svg?branch=master)](https://travis-ci.com/p27mcgee/gladys)

A proof of concept JVM Tool Interface Java Agent

| ![Gladys Kravitz, a classic snoop.][mskravitz] |
|:--:|
| *Gladys Kravitz, a classic snoop.* |

#### If you go snooping you may be shocked by what you find!

Gladys is a Java Agent that accesses the internals of the Java virtual machine through its Tool Interface (JVMTI).
Currently, both it's functionality and flexibility are very limited.  Gladys targets web applications running in a
Java Servlet container (e.g., Apache Tomcat) and attempts to measure, for each HTTP request: 
* the processing time
* the number of String objects created
* the number of newly loaded classes

The results of these measurements are displayed on the web application's console and, optionally, POSTed 
to a separate HTTP server for display, recording or analysis.  

## Getting Started

In order to build and test gladys you'll need to have the following installed on your test machine:
* Oracle JDK 8  -- Other JDKs may work fine, but this is what gladys was developed and tested on.
* Apache Maven 3.5

Also, make sure that port 8080 on your test machine is not already in use.

Download the master branch of gladys respository to your test machine either using git or as a zip file.  
If you downloaded the zip file expand it into a directory of your choosing.

Open a console window and change directory to the gladys repository root directory.  
Build gladys using Maven:

\> mvn clean install

Hopefully that has gone well and you now have built three gladys jar files located in 
sub-directories of the gladys repository root directory:
* %GLADYS_ROOT%/gladys-agent/target/gladys-agent-1.0.0-alpha3.jar
* %GLADYS_ROOT%/gladys-bootstrap-jar/target/gladys-bootstrap-jar-1.0.0-alpha3.jar
* %GLADYS_ROOT%/gladys-remote-console/target/gladys-remote-console-1.0.0-alpha3.jar

Gladys was developed using OWASP's WebGoat web application as it's target and the WebGoat 
jar is included in the gladys repository root directory (webgoat-server-8.0.0.M3.jar).  
When starting the WebGoat server gladys is attached by adding special JVM command line parameters.
Assuming your JDK's bin directory is included on your search path, the following command 
should be sufficient to launch WebGoat with gladys attached doing it's modest bit of 
profiling:

\> java -javaagent:gladys-agent/target/gladys-agent-1.0.0-alpha3.jar -Xbootclasspath/a:gladys-bootstrap-jar/target/gladys-bootstrap-jar-1.0.0-alpha3.jar -Dlog4j.configurationFile=log4j2.xml -jar webgoat-server-8.0.0.M3.jar

When the console has stopped spewing and displayed the line containing:

    ... : Started StartWebGoat in nn.nnn seconds ...

You should be able to connect your browser to the WebGoat application using the following URL:

http://localhost:8080/WebGoat
    
If all is right you should see a WebGoat signin page.  You need to register a user and sign in to proceed.  
Then you should find yourself on WebGoat's home page which asks the question "What is WebGoat?"
While you were registering and signing in to WebGoat the web application and the gladys agent 
were pouring messages onto your WebGoat console.  The gladys performance metrics are the lines that
look something like this:

>02:20:32.502     12.437 mSec to GET http://localhost:8080/WebGoat/service/lessonmenu.mvc | requestId: 87fb963e-1d61-4bfb-8066-7f2091dbf74e |   2759 Strings created |    0 Classes loaded






[mskravitz]: https://vignette.wikia.nocookie.net/bewitched/images/e/ee/Gladys_Kravitz.jpg/revision/latest?cb=20090817040052

