FROM openjdk:11-buster
COPY . .
RUN chmod +x ./mvnw
RUN ./mvnw compile
#COPY target/VocabularyWordsService-0.0.1-SNAPSHOT.jar VocabularyWordsService-0.0.1-SNAPSHOT.jar
CMD ["java","-jar","target/VocabularyWordsService-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080