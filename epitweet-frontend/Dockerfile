# Build de l'application Angular
FROM node:20 AS build

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install
RUN npm install -g @angular/cli

COPY . .

RUN npm run build -- --output-path=dist

# Serveur Nginx pour servir l'application
FROM nginx:alpine

COPY --from=build /app/dist/ /var/www/html/

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
