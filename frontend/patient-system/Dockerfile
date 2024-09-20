FROM node:latest

WORKDIR /frontend/patient-system

RUN npm install -g @angular/cli

COPY /frontend/patient-system/package.json /frontend/patient-system/package-lock.json ./
RUN npm i

COPY . .

EXPOSE 4200
CMD ["ng", "serve", "--host", "0.0.0.0"]

