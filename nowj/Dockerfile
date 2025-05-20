# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copy Maven Wrapper config
COPY .mvn ./.mvn
COPY pom.xml mvnw mvnw.cmd ./
COPY src ./src
COPY tessdata ./tessdata
# Nếu có file cấu hình ngoài src thì copy thêm ở đây
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copy file jar build ra (nên dùng tên jar cụ thể nếu có)
COPY --from=build /app/target/*.jar app.jar
COPY tessdata ./tessdata

EXPOSE 8080

# Cho phép truyền JAVA_OPTS khi chạy container
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]