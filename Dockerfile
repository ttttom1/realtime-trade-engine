# 1. 베이스 이미지: Eclipse Temurin JDK 21 버전을 사용합니다.
# 'jammy'는 Ubuntu 22.04 기반으로 안정적입니다.
FROM eclipse-temurin:21-jdk-jammy

# 2. 작업 디렉토리 설정: 컨테이너 내부에서 명령이 실행될 기본 경로입니다.
WORKDIR /app

# 3. JAR 파일 복사: Gradle로 빌드된 실행가능한 JAR 파일을 컨테이너 안으로 복사하고,
#    이름을 app.jar로 변경합니다.
#    (주의: JAR 파일의 실제 경로는 build.gradle 설정에 따라 다를 수 있습니다)
COPY build/libs/api-service-0.0.1-SNAPSHOT.jar app.jar

# 4. 포트 노출: 우리 애플리케이션이 8080 포트를 사용함을 명시합니다.
EXPOSE 8080

# 5. 실행 명령어: 컨테이너가 시작될 때, app.jar를 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]
