#!/bin/zsh
docker build -t suplus-gateway .
docker tag suplus-discovery registry.cn-beijing.aliyuncs.com/suplus_main/suplus-gateway:1.0.0
docker push registry.cn-beijing.aliyuncs.com/suplus_main/suplus-gateway:1.0.0