#!/bin/bash

mvn clean package

export JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044"

if [ $? == 0 ]; then
  controller/target/appassembler/bin/app $@   
fi
