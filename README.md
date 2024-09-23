
# 🏥 Patient System

## 📋 Requirements to run with Docker
- **Docker** & **Docker Compose**

## 📋 Requirements to run manually
### Frontend
- **npm**: v10.8.3
- **Angular**: v18
  
### OpenSearch and AidBox
- **Docker** & **Docker Compose**

### Backend
- **Java**: v21
- **Spring Boot**: v3.2.3
- **Gradle**
- (Optional) **Postman**: latest - to access the endpoints of the BE.

---

## ⚠️ Run via Docker
1. Clone the repo.
2. Navigate to the frontend directory:
   ```bash
   cd repo/
   ```
3. Run docker-compose.yaml file:
   ```bash
   docker-compose up -d 
   ```
⚠️ For the app to work you need to wait for the AidBox container to be up and running ⚠️

---
## ⚠️ Run manually
---
## 🚀 Run Frontend (FE)

1. Clone the repo.
2. Navigate to the frontend directory:
   ```bash
   cd repo/frontend/patient-system/
   ```
3. Run from frontend directory:
   ```bash
   npm install
   ```
4. Start the Angular application:
   ```bash
   ng serve
   ```
5. Open your web browser and go to: [http://localhost:4200/](http://localhost:4200/)

---

## 🐳 Run OpenSearch and AidBox

1. Clone the repo.
2. Navigate to the root directory:
   ```bash
   cd repo/
   ```
3. Edit the docker-compose.yaml file by commenting on the following services:
   ```bash
   frontend-angular
   backend-spring-boot
   ```
4. In the terminal, run the following command:
   ```bash
   docker-compose up -d
   ```
5. To stop the Docker container, use:
   ```bash
   docker-compose down
   ```

---

## 🔙 Run Backend (BE)

1. Clone the repo.
2. Navigate to the backend directory JAR:
   ```bash
   cd repo/backend/build/libs/
   ```
3. Start the Spring Boot application:
   ```bash
   java -jar backend-0.0.1-SNAPSHOT.jar
   ```
4. Change from application.properties the following:
   ```bash
   http://aidbox:8888/fhir/Patient/ ==> http://localhost:8888/fhir/Patient/
   http://opensearch-node1:9200 ==> http://localhost:9200
   ```

---

## ⚠️ Extra features
1. Search the patient by name or phone number.


---

