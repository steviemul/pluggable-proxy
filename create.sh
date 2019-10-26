#!/bin/bash

docker create -p 0.0.0.0:9090:9090/tcp -p 0.0.0.0:9091:9091/tcp -v `pwd`/devproxy:/root/.devproxy -v `pwd`/devproxy/content:/root/content --name dev-proxy dev-proxy