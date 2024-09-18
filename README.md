# AngSprApp
** Requirements **
* Frontend: *
- Angular v18
- json-server@latest

* OpenSearch and AidBox: *
- docker & docker-compose

* Backend: *
- Java 21
- Spring Boot 3.2.3
- Gradle
  
** Run Frontend (FE) **
1) Clone repo
2) From terminal go to folder: *frontend/patient-system/* and type in **ng serve**
3) From terminal go to folder: *frontend/patient-system/db-mock/* and type in **json-server db.json**
4) Open any web browser and go-to: **http://localhost:4200/**

** Run OpenSearch and AidBox docker-compose file **
1) Clone repo
2) From the terminal type: **docker-compose up -d**
3) To stop the docker container type in the terminal: **docker-compose down**

** Run Backend (BE) **
1) Clone repo
2) From terminal go to folder: *backend/* and type in **gradle bootRun**.
3) Import the Postman JSON file into your Postman client to test the endpoints of the Spring Boot Application.


** Issues FE **
1) Accepts duplicates
2) Returns only the last value when a certain patient is searched
3) Upon updating a patient's details it inserts a new record instead of updating the old one.
