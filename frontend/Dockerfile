# stage1 as builder
FROM node:20-alpine as builder

# copy the package.json to install dependencies
COPY package.json ./
COPY package-lock.json ./

# Install the dependencies and make the folder
RUN npm install --legacy-peer-deps && mkdir /src && mv ./node_modules ./src

WORKDIR /src

COPY . .

# Build the project and copy the files
RUN npm run ng build -- --configuration production
#RUN node --max_old_space_size=8192 node_modules/@angular/cli/bin/ng build -- --deploy-url=/ --prod

FROM nginx:alpine

#!/bin/sh

COPY nginx_default.conf /etc/nginx/conf.d/default.conf
COPY start_nginx.sh /start_nginx.sh
RUN mkdir -p /tmp/log/nginx


## Remove default nginx index page
RUN rm -rf /usr/share/nginx/html/*

# Copy from the stage 1
COPY --from=builder /src/dist /usr/share/nginx/html
RUN cp /usr/share/nginx/html/index.html /usr/share/nginx/html/index_base.html

EXPOSE 8080
ENTRYPOINT ["sh", "/start_nginx.sh"]
