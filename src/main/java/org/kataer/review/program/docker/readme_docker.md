#### Docker
##### 什么是Docker
1. DockerClient 客户端
2. DockerDaemon 守护进程
3. DockerImage 镜像
4. DockerContainer 容器
```text
Docker将应用程序和依赖打包在一起，可以快速地部署应用程序。
简化了创建、管理、分发应用程序的过程
```
##### Docker容器原理
```text
利用Linux的NameSpace机制实现资源隔离，
利用control groups 控制容器中进程对资源的消耗
有了这两项技术，容器看起来像独立的操作系统
```
##### Docker 指令
```text
以安装redis为例
docker search redis --filter stars=11111
docker pull redis:6.0
docker images 
docker run redis -p 6379:6379 -v -d
docker ps
docker exec -it container_id /bin/sh
docker stop
docker restart
```
- docker search
- docker pull
- docker images
- docker run|stop|restart|rm|rmi
- docker ps 查看运行中的容器
- docker build 构建镜像
##### DockerFile
```text
DockFile是文本文件，包含构建Docker镜像的所有命令，每个指令构建一层。
```
- FROM 为后续的指令创建基础镜像，是第一条指令
- RUN 创建一个新层，为镜像添加功能
- LABEL 用于组织项目映像，模块，许可等。在自动化布署方面 LABEL 也有很大用途。在 LABEL 中指定一组键值对，可用于程序化配置或布署 Docker
- COPY
- WORKDIR
- CMD 使用 CMD 指令为执行的容器提供默认值。在 Dockerfile 文件中，若添加多个 CMD 指令，只有最后的 CMD 指令运行
##### Docker compose

