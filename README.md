
# 🏥 Patient System

## 📋 Requirements

### Frontend
- **npm**: v10.8.3
- **Angular**: v18
- (Optional) **json-server**: latest - to run the FE without the BE but needs changing the endpoints in the code.

### OpenSearch and AidBox
- **Docker** & **Docker Compose**

### Backend
- **Java**: v21
- **Spring Boot**: v3.2.3
- **Gradle**
- (Optional) **Postman**: latest - to access the endpoints of the BE.

---

## 🚀 Run Frontend (FE)

1. Clone the repo.
2. Navigate to the frontend directory:
   ```bash
   cd repo/frontend/patient-system/
   ```
3. Start the Angular application:
   ```bash
   ng serve
   ```
4. (Optional) In a new terminal, navigate to the mock database directory:
   ```bash
   cd repo/frontend/patient-system/db-mock/
   ```
5. (Optional) Start the json-server:
   ```bash
   json-server db.json
   ```
6. Open your web browser and go to: [http://localhost:4200/](http://localhost:4200/)

---

## 🐳 Run OpenSearch and AidBox

1. Clone the repo.
2. Navigate to the root directory:
   ```bash
   cd repo/
   ```
3. In the terminal, run the following command:
   ```bash
   docker-compose up -d
   ```
4. To stop the Docker container, use:
   ```bash
   docker-compose down
   ```

---

## 🔙 Run Backend (BE)

1. Clone the repo.
2. Navigate to the backend directory:
   ```bash
   cd repo/backend/
   ```
3. Start the Spring Boot application:
   ```bash
   gradle bootRun
   ```
4. (Optional) Import the Postman JSON file into your Postman client to test the endpoints of the Spring Boot Application.

---

## ⚠️ Issues

- Accepts duplicates.
- Returns only the last value when a certain patient is searched e.g., two patients have the same first name.
- Upon updating a patient's details, it inserts a new record instead of updating the old one.

---

