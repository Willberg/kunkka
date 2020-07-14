#!/usr/bin/env bash
# 建立相关目录
mkdir -p /home/build
mkdir -p /home/logs/kunkka
mkdir -p /home/server
mkdir -p /home/resource/kunkka
mkdir -p /home/code

# 克隆代码
cd /home/code && git clone https://github.com/Willberg/kunkka.git

# 拷贝配置文件到配置目录，以便修改
cp /home/code/kunkka/src/main/resources/application.yml /home/resource/kunkka
cp /home/code/kunkka/src/main/resources/logback-spring.xml /home/resource/kunkka
cp /home/code/kunkka/sh/kunkka.sh /home/build