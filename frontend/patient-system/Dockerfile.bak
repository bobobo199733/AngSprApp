FROM node:latest

WORKDIR /patient-system

RUN npm install -g @angular/cli

COPY package.json package-lock.json ./
RUN npm i

COPY . .

EXPOSE 4200
CMD ["ng", "serve", "--host", "0.0.0.0"]

