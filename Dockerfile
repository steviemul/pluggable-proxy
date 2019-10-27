FROM  maven:3.3-jdk-8

RUN mkdir -p /root/content

COPY . /proxy/

WORKDIR /proxy

RUN mvn clean package

ENTRYPOINT ["./proxy.sh"]

CMD ["-mitm"]