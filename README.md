# gladys

[![Build Status](https://travis-ci.com/p27mcgee/gladys.svg?branch=master)](https://travis-ci.com/p27mcgee/gladys)

A proof of concept Java Agent for instrumenting the JVM

| ![Gladys Kravitz, a classic snoop.][mskravitz] |
|:--:|
| *Gladys Kravitz, a classic snoop.* |

#### If you go snooping you may be shocked by what you find!

Gladys is a Java Agent that accesses the internals of the Java virtual machine through the interface java.lang.instrument.Instrumentation.  Currently, both it's functionality and flexibility are very limited.  Gladys targets web applications running in a Java Servlet container (e.g., Apache Tomcat) and attempts to measure, for each HTTP request: 
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

Download the master branch of gladys respository to your test machine either using git or as a zip file.  If you downloaded the zip file expand it into a directory of your choosing.

Open a console window and change directory to the gladys repository root directory.

Build gladys using Maven:

\> mvn clean install

Hopefully that has gone well and you now have built three gladys jar files located in sub-directories of the gladys repository root directory:
* %GLADYS_ROOT%/gladys-agent/target/gladys-agent-1.0.0-alpha3.jar
* %GLADYS_ROOT%/gladys-bootstrap-jar/target/gladys-bootstrap-jar-1.0.0-alpha3.jar
* %GLADYS_ROOT%/gladys-remote-console/target/gladys-remote-console-1.0.0-alpha3.jar

Gladys was developed using OWASP's WebGoat web application as it's target and the WebGoat jar is included in the gladys repository root directory (webgoat-server-8.0.0.M3.jar). When starting the WebGoat server gladys is attached by adding special JVM command line parameters.  Assuming your JDK's bin directory is included on your search path, the following command should be sufficient to launch WebGoat with gladys attached, doing it's modest bit of profiling:

\> java -javaagent:gladys-agent/target/gladys-agent-1.0.0-alpha3.jar -Xbootclasspath/a:gladys-bootstrap-jar/target/gladys-bootstrap-jar-1.0.0-alpha3.jar -Dlog4j.configurationFile=log4j2.xml -jar webgoat-server-8.0.0.M3.jar

When the console has stopped spewing and displayed the line containing:

    ... : Started StartWebGoat in nn.nnn seconds ...

You should be able to connect your browser to the WebGoat application using the following URL:

http://localhost:8080/WebGoat
    
If all is well you should see a WebGoat sign in page.  You need to register a new user and sign in to proceed. Then you should find yourself on WebGoat's home page, which asks the question "What is WebGoat?"  While you were registering and signing in to WebGoat the web application and the gladys agent were pouring messages onto the console of your WebGoat application.  The gladys performance metrics are the lines that look something like this:

>02:20:32.502     12.437 mSec to GET http://localhost:8080/WebGoat/service/lessonmenu.mvc | requestId: 87fb963e-1d61-4bfb-8066-7f2091dbf74e |   2759 Strings created |    0 Classes loaded

The gladys request metrics lines are also logged to the file gladysSummary.log in the gladys repository root directory.

## Remote Monitoring

Gladys can also POST its request metrics to an external monitoring server for display, recording, analysis, etc.  Gladys-remote-console is a web application that demonstrates that capability.  To see it in action open a second console window (If you've killed the previous launch of the gladys-instrumented WebGoat please restart it.).  In the new console change to the gladys repository root directory and launch the gladys-remote-console with the following command:

>java -jar gladys-remote-console/target/gladys-remote-console-1.0.0-alpha3.jar

The gladys-remote-console should stop spitting out messages in a few seconds.  (Gladys-remote-console listens on port 8088 so, if you have difficulty in launching it, make sure another application is not using this port.) At this point you should not be seeing any messages from the gladys-agent.

Gladys uses Apache log4j 2 for it's console, file and http logging.  The logging configuration is in the file log4j2.xml in the gladys repository root directory.  Log4j 2, cleverly, monitors it's configuration file for changes and applies them on the fly.  To turn on the remote logging open log4j2.xml in your favorite text editor and change the following line (around line 40)

> \<!-- enable for remote Ledger JSON logging \<AppenderRef ref="gladys-remote-console"/\> --\>

to its logging enabled version

> \<AppenderRef ref="gladys-remote-console"/\> \<!-- enable for remote Ledger JSON logging --\>

and save your edits.

Within about 30 seconds any metrics produced by the gladys-agent should be transmitted to and displayed on the console of the gladys-remote-console.  If you don't see anything, try clicking a link or two in the WebGoat page in your browser.

What you'll see in gladys-remote-console is the gladys request metrics in a JSON format more easily consumable by web services, e.g.:

> {"uniqueId":"ecdfce31-2101-4860-aafa-2c3ab4f74eb7","threadName":"http-nio-8080-exec-8","httpMethod":"GET","url":"http://localhost:8080/WebGoat/plugins/bootstrap/fonts/glyphicons-halflings-regular.woff","sessionId":"FEC79A0EB92B0BD64951549FBDAB280E","startNanos":1133675610141732,"endNanos":1133675615519603,"stringCreateCount":2987,"memoryUsage":0,"classLoadCount":0,"instrumentationExceptionMessage":null,"complete":true,"durationNanos":5377871}

This record includes a bit more information than is shown in the gladys-agent summary log line (for example the name of the request processing thread and the user's session ID).  But, sadly, if you want to know request processing time you'll have to do the math yourself (endNanos-startNanos nanoseconds).

## Shut it down

Before you shutdown the gladys-remote-console (Ctrl-C in the console window) either disable the http logging by reversing the edits you made in log4j2.xml, or shutdown the WebGoat app server (Ctrl-C in the console window).  Failure to do so, will result in a lot of ugly exception stack traces spit out on the WebGoat console -- one for every failed transmission to the gladys-remote-console.

## What next?

Gladys is in desparate need of enhanced unit and integration tests, and a fair amount of research into best practices for testing Java Agents will need to precede the creation of those tests.

An important feature enhancement for gladys is the addition of a measurent of memory used by objects created during request processing.  Initial attempts to make these measurements met with catastrophic results.  (The JVM crashed on start-up.)

In gladys I have attempted, and I believe failed, to "shadow" the dependencies of the gladys-agent.  The idea is to rename classes in libraries which may also be used in the target application to avoid version clashes and the possibility of having multiple versions of classes loaded in different classloaders.  The Maven "Shade Plugin" conveniently provides this functionality, but I believe I have it misconfigured.  I haven't seen any indication of conflicting libraries in gladys and WebGoat, but gladys should be made compatible with as wide a variety of target applications as possible.

And something must be done about those ugly exception stack traces that show up when the remote console is not listening as expected.

## Acknowledgements

This project was made possible by the astonishingly original and powerful tool Byte Buddy, the work of Rafael Winterhalter.  Without Byte Buddy and Rafael's tireless efforts to support it's users and explain the intricacies and pitfalls of JVM instrumentation I could not conceive of having produced gladys, even with it's meager functionality.  Thank you, Rafael, you are a craftsman of the highest order. 

\- Phil McGee

[mskravitz]: https://vignette.wikia.nocookie.net/bewitched/images/e/ee/Gladys_Kravitz.jpg/revision/latest?cb=20090817040052

