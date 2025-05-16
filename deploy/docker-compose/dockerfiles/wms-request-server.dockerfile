FROM alpine/java:21-jdk

WORKDIR /app

COPY target/wms-request-handler-1.0-SNAPSHOT.jar ./app.jar

ENV NGINX_GATEWAY_HOST=nginx \
    TILE_DB_URL=jdbc:postgresql://db:5432/tiles_metadata \
    TILE_DB_USER=postgres \
    TILE_DB_PASSWORD=1234 \
    TILE_POOL_SIZE=10 \
    META_DB_URL=jdbc:postgresql://db:5432/wms_metadata \
    META_DB_USER=postgres \
    META_DB_PASSWORD=1234 \
    META_POOL_SIZE=10 \
    SHARD_PORTS=0:50051,1:50051

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
