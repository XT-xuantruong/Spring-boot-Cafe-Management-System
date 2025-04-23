# Sử dụng image Java 17 (có thể đổi thành 21 nếu bạn dùng Java 21)
FROM eclipse-temurin:19-jdk-alpine

# Tạo thư mục chứa ứng dụng trong container
WORKDIR /app
VOLUME /tmp
# Copy file .jar vào container
COPY target/*.jar app.jar

# Expose cổng mặc định của Spring Boot
EXPOSE 8089

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-DwebAllowOthers=true", "-jar", "app.jar"]
