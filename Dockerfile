FROM maven:3.8.5-openjdk-17 AS build  
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM eclipse-temurin:17.0.8_7-jre-alpine
COPY --from=build /usr/src/app/target/followup*.jar /usr/app/followup.jar
EXPOSE 8080  
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prd","/usr/app/followup.jar"]
