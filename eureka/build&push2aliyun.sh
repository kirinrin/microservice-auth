#!/bin/zsh
docker build -t suplus-discovery .
docker tag suplus-discovery registry.cn-beijing.aliyuncs.com/suplus_main/suplus-discovery:1.0.0
docker push registry.cn-beijing.aliyuncs.com/suplus_main/suplus-discovery:1.0.0