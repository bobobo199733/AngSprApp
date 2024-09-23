
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

## Run everything via Docker-Compose (AidBox, OpenSearch, Frontend and Backend 🚀)
1. Clone the repo.
2. Navigate to the frontend directory:
   ```bash
   cd repo/
   ```
3. Run docker-compose.yaml file:
   ```bash
   docker-compose up -d 
   ```
4. Wait for the AidBox: **"aidbox" container** to be up and running:
   
   You can verify the log of the aidbox container via the following command:
   ```bash
   docker logs --details aidbox 
   ```
   When it is ready it will show in the console:
   
   ![Screenshot 2024-09-23 180057](https://github.com/user-attachments/assets/b5d0abd9-98f7-4209-b216-a8151872ee23)

   ⚠️ Step 3) depends heavily on the environment and configuration of the machine in which the container is created ⚠️
  
   ⚠️ In general, it will take some time (approx. 1-3 min.) because it needs to pull from Docker Hub the AidBox image + to pull from GitHub the PostgreSQL database & Zen Packages ⚠️

   
5. Open a browser of choice and go to URL: 
   ```bash
   http://localhost:8888/ui/console#/sandbox/basic
   ```
6. From the image below set the **Client Id = basic and Client Secret = secret**, and press all three **RUN** buttons: 
   ![Screenshot 2024-09-23 174712](https://github.com/user-attachments/assets/8d337f5a-f88b-4c5d-90b8-c7d97c692799)

7. Voilà, the app is available at:
   ```bash
    http://localhost:4200/patient-form
   ```
   
---
## Run manually 🚀
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
   cd repo/backend/
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

## ⚠️ Swagger documentation is available at:
   ```bash
   http://localhost:8080/swagger-ui/index.html#/
   ```

---

## ⚠️ Extra features
1. Search the patient by name or phone number.

---

