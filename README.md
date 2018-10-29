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
* Oracle JDK 8  
Other JDKs may work fine, but this is what gladys was developed and tested on.
* Apache Maven 3.5

Download the master branch of gladys respository to your test machine either using git or as a zip file.  
If you downloaded the zip file expand it into a directory of your choosing.





[mskravitz]: https://vignette.wikia.nocookie.net/bewitched/images/e/ee/Gladys_Kravitz.jpg/revision/latest?cb=20090817040052

