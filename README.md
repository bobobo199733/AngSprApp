# AngSprApp
** Run Frontend **
- Angular v18
- json-server@latest
- docker & docker-compose
  
** Run Frontend **
1) Clone repo
2) From terminal go to folder: *frontend/patient-system/* and type in **ng serve**
3) From terminat go to folder: *frontend/patient-system/db-mock/* and type in **json-server db.json**
4) Open any web browser and goto: **http://localhost:4200/**

** Run OpenSearch and Aidbox docker-compose file **
1) Clone repo
2) From the terminal type: **docker-compose up -d**
3) To stop the docker container type in the terminal: **docker-compose down**

** Issues **
1) Accepts duplicates
2) Returns only the last value when a certain patient is searched
3) Upon updating a patient details it inserts a new record instead of updating the old one.
