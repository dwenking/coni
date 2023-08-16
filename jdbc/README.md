```bash
export PROJECT_PATH=/Users/dwenking/Git/coni/jdbc
export MAVEN_PATH=/Users/dwenking/.m2/repository
java -javaagent:${PROJECT_PATH}/jar/jacocoagent.jar=output=tcpclient,address=127.0.0.1,port=6300,includes="org.mariadb.*:com.mysql.*" -classpath ${PROJECT_PATH}/jar/mariadb-java-client-3.1.3.jar:${PROJECT_PATH}/jar/mysql-connector-j-8.0.33.jar:${PROJECT_PATH}/target/classes:${MAVEN_PATH}/org/apache/logging/log4j/log4j-core/2.20.0/log4j-core-2.20.0.jar:${MAVEN_PATH}/org/apache/logging/log4j/log4j-api/2.20.0/log4j-api-2.20.0.jar Client
```

java -javaagent:${PROJECT_PATH}/jar/jacocoagent.jar=output=file,destfile=out/jacoco.exec,includes="org.mariadb.*:com.mysql.*" -classpath ${PROJECT_PATH}/jar/mariadb-java-client-3.1.3.jar:${PROJECT_PATH}/jar/mysql-connector-j-8.0.33.jar:${PROJECT_PATH}/target/classes:${MAVEN_PATH}/org/apache/logging/log4j/log4j-core/2.20.0/log4j-core-2.20.0.jar:${MAVEN_PATH}/org/apache/logging/log4j/log4j-api/2.20.0/log4j-api-2.20.0.jar Client

```bash
export PROJECT_PATH=/Users/dwenking/Git/coni/jdbc
java -jar jar/jacococli.jar execinfo out/jacoco.exec
```