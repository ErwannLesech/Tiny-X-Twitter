# Build de l'application Angular
FROM node:20 AS build

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install
RUN npm install -g @angular/cli

COPY . .

RUN npm run build -- --output-path=dist

# Serveur http-server pour servir l'application
FROM node:18-alpine

RUN npm install -g http-server

WORKDIR /app
COPY --from=build /app/dist/browser /app/

EXPOSE 4200

CMD ["http-server", "/app", "-p", "4200", "-a", "0.0.0.0"]
