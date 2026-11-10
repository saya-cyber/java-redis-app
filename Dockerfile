FROM eclipse-temurin:17-jdk

RUN apt-get update \
    && apt-get upgrade -y \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY Main.java .
RUN javac Main.java
EXPOSE 8080
CMD ["java", "Main"]
