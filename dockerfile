FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

RUN javac -d out *.java

CMD ["java", "-cp", "out", "biblioteca.Main"]
