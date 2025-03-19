FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update

RUN apt-get install -y --no-install-recommends openjdk-21-jre-headless
RUN apt-get install -y gdal-bin
RUN apt-get install -y libgdal-dev


ENV GDAL_DATA=/usr/share/gdal
ENV PROJ_LIB=/usr/share/proj
ENV LD_LIBRARY_PATH=/usr/lib

COPY target/tile-storage-server-1.0-SNAPSHOT.jar /app/app.jar
CMD ["java", "-Djava.library.path=/usr/lib", "-jar", "/app/app.jar"]
