#!/bin/bash

mvn clean package

if [ $? == 0 ]; then
  controller/target/appassembler/bin/app $@   
fi
