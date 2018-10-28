REM java -javaagent:<path to GladysAgent jar> -Xbootclasspath/a:<path to GladysBootLoaderJar> -jar <path to OWASP WebGoat jar> 
REM java -javaagent:<path to GladysAgent jar> -Xbootclasspath/a:<path to GladysBootLoaderJar> -jar <path to OWASP WebGoat jar> 

java -javaagent:E:/W_SharedRepo/com/github/p27mcgee/gladys/gladys-agent/1.0.0-alpha/gladys-agent-1.0.0-alpha.jar -Xbootclasspath/a:E:/W_SharedRepo/com/github/p27mcgee/gladys/gladys-bootstrap-jar/1.0.0-alpha/gladys-bootstrap-jar-1.0.0-alpha.jar -Dlog4j.configurationFile=log4j2.xml -jar webgoat-server-8.0.0.M3.jar 


