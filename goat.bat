REM java -javaagent:<path to GladysAgent jar> -Xbootclasspath/a:<path to GladysBootLoaderJar> -Dlog4j.configurationFile=<path to log4j2.xml for logging configuration> -jar <path to OWASP WebGoat jar> 

java -javaagent:gladys-agent/target/gladys-agent-1.0.0-alpha3.jar -Xbootclasspath/a:gladys-bootstrap-jar/target/gladys-bootstrap-jar-1.0.0-alpha3.jar -Dlog4j.configurationFile=log4j2.xml -jar webgoat-server-8.0.0.M3.jar
