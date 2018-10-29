REM java -javaagent:<path to GladysAgent jar> -Xbootclasspath/a:<path to GladysBootLoaderJar>  -Dlog4j.configurationFile=<path to log4j2.xml for logging configuration> -jar <path to OWASP WebGoat jar> 

java -javaagent:E:/W_SharedRepo/com/github/p27mcgee/gladys/gladys-agent/1.0.0-alpha3/gladys-agent-1.0.0-alpha3.jar -Xbootclasspath/a:E:/W_SharedRepo/com/github/p27mcgee/gladys/gladys-bootstrap-jar/1.0.0-alpha3/gladys-bootstrap-jar-1.0.0-alpha3.jar -Dlog4j.configurationFile=log4j2.xml -jar webgoat-server-8.0.0.M3.jar 


