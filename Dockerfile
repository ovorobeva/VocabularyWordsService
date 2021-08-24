FROM openjdk:11-buster
COPY . .
RUN ./mvnw package
#COPY target/VocabularyWordsService-0.0.1-SNAPSHOT.jar VocabularyWordsService-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","target/VocabularyWordsService-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080