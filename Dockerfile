#FROM registry.cn-shenzhen.aliyuncs.com/cuishiwen/geek-java8:v5
FROM openjdk:8u232-jdk-stretch

# 拷贝代码
RUN mkdir /stress
WORKDIR /stress/

ENV LC_ALL C.UTF-8

#build时接受的参数，用来指定spring boot的profile
ARG envType=dev
ENV envType ${envType}

EXPOSE 8080

COPY target/stress-generator-1.0.jar .

CMD ["java", "-Duser.timezone=GMT+8", "-Djava.security.egd=file:/dev/./urandom", "-jar", "stress-generator-1.0.jar", "--spring.profiles.active=${envType}"]