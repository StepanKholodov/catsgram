FROM amazoncorretto:21-alpine as builder
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests


FROM amazoncorretto:21-alpine AS layertools
WORKDIR application
COPY --from=builder /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM amazoncorretto:21-alpine
COPY --from=layertools /application/dependencies/ ./
COPY --from=layertools /application/spring-boot-loader/ ./
COPY --from=layertools /application/snapshot-dependencies/ ./
COPY --from=layertools /application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]