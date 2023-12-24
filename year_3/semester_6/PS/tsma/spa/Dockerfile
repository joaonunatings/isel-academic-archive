# syntax=docker/dockerfile:1

FROM node as base
WORKDIR /spa
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx
COPY --from=base /spa/dist/spa /usr/share/nginx/html
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
#CMD
