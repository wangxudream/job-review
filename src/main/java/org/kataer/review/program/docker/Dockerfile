FROM openjdk:8-jre-alpine
MAINTAINER chenwei <acee06.weichen@gmail.com>

RUN echo 'Asia/Shanghai' > /etc/timezone
#captche 字体包
RUN set -xe \
&& apk --no-cache add ttf-dejavu fontconfig
COPY start/target/start-1.0.0.jar /opt/api.jar
WORKDIR /opt
CMD ["java", "-jar", "api.jar"]
