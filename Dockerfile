#FROM ubuntu
#RUN apt update && \
#    apt upgrade -y && \
#    apt install -y  software-properties-common && \
#    add-apt-repository ppa:webupd8team/java -y && \
#    apt update && \
#    echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
#    apt install -y oracle-java8-installer && \
#    apt clean
FROM openjdk:8
ADD build/libs/sql-mongo-client.jar /sql-mongo-client.jar
ENTRYPOINT ["java", "-jar", "sql-mongo-client.jar"]