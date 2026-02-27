# Build WAR
FROM maven:3-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# Run with Tomcat
FROM tomcat:10.0-jdk17
COPY --from=build /app/target/SpringMVC_SMART_HOTEL-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/SpringMVC_SMART_HOTEL.war
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
