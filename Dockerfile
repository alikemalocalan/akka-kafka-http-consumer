FROM openjdk-8-alpine:8-jre
ADD /target/scala-2.12/akka-kafka-http-consumer-*.jar akka-kafka-http-consumer.jar
ENV LOG_LEVEL="DEBUG"
EXPOSE 9876
ENTRYPOINT exec java -server -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -jar akka-kafka-http-consumer.jar
