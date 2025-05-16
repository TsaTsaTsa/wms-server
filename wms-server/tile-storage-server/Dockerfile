# Use the Ubuntu-based GDAL image with "small" footprint
FROM osgeo/gdal:ubuntu-full-3.6.3

# Install curl, wget, ca-certificates, ImageMagick and GDAL CLI tools
RUN apt-get update
RUN apt-get install -y --no-install-recommends ca-certificates wget
RUN apt-get install -y --no-install-recommends unzip
RUN apt-get install -y --no-install-recommends imagemagick 
RUN rm -rf /var/lib/apt/lists/*

# Install Java 21 from Eclipse Temurin
ENV LANG=en_US.UTF-8
ENV JAVA_HOME=/usr/lib/jvm/msopenjdk-21-amd64
ENV PATH="${JAVA_HOME}/bin:${PATH}"
COPY --from=mcr.microsoft.com/openjdk/jdk:21-ubuntu $JAVA_HOME $JAVA_HOME

ENV LD_LIBRARY_PATH=/usr/share/java/
ENV IMAGE_MAGICK=magick

# Set working directory
WORKDIR /app

# Create and populate data directories for styles and tiles
RUN mkdir -p /app/data/styles /app/data/tiles
COPY opt/styles      /app/data/styles
COPY ethiopia_tiles  /app/data/tiles

# Ensure application picks up these paths
ENV TILES_DIR=/app/data/tiles/
ENV STYLE_DIR=/app/data/styles/

# Copy application JAR
COPY target/tile-storage-server-1.0-SNAPSHOT.jar /app/tile-storage.jar

# Expose gRPC port
EXPOSE 50051

# Launch the Java application
ENTRYPOINT ["java", "-jar", "/app/tile-storage.jar"]
