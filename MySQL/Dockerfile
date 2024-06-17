# 使用官方 MySQL 镜像作为基础
FROM mysql:8.0

# 设置环境变量
ENV MYSQL_ROOT_PASSWORD=123456
ENV MYSQL_DATABASE=poetry

# 拷贝初始化 SQL 脚本到容器中
COPY ./init.sql /docker-entrypoint-initdb.d/

# 公开端口 3306
EXPOSE 3306