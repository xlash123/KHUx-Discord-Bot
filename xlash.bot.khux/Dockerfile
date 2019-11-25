FROM openjdk:8

WORKDIR /usr/src/khux-bot
COPY . .
# Build Jar
RUN ./gradlew shadowJar

CMD ["java", "-jar", "./build/libs/xlash.bot.khux.jar", "run"]