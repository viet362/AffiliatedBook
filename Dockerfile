# ==========================================
# Stage 1: Build file WAR bằng Gradle
# ==========================================
FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /app

# Copy toàn bộ mã nguồn vào image builder
COPY --chown=gradle:gradle . .

# Build tạo file ROOT.war (bỏ qua task test để deploy nhanh)
RUN gradle war -x test --no-daemon

# ==========================================
# Stage 2: Runtime với Apache Tomcat 10.1 (hỗ trợ Jakarta Servlet 6.0)
# ==========================================
FROM tomcat:10.1-jdk17-temurin

# Dọn dẹp ứng dụng mặc định của Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy file ROOT.war để ứng dụng chạy tại context root (/)
COPY --from=builder /app/build/libs/ROOT.war /usr/local/tomcat/webapps/ROOT.war

# Render cấp phát cổng qua biến môi trường $PORT (mặc định 8080)
ENV PORT=8080
EXPOSE 8080

# Tự động thay đổi cổng trong server.xml của Tomcat theo biến $PORT và khởi chạy
CMD ["sh", "-c", "sed -i 's/port=\"8080\"/port=\"'\"$PORT\"'\"/g' /usr/local/tomcat/conf/server.xml && catalina.sh run"]
