#FROM dev_nginx:latest
## 将项目根目录下dist文件夹下的所有文件复制到镜像中 /usr/share/nginx/html/ 目录下
#COPY dist/ /usr/share/nginx/html/
#COPY default.conf /etc/nginx/nginx.conf
FROM node:18.20.2-alpine as build-stage
WORKDIR /app
COPY . /app/
RUN npm install && npm run build

FROM nginx:1.21.3-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY --from=build-stage /app/default.conf /etc/nginx/nginx.conf
EXPOSE 80