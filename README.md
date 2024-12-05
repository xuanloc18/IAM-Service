# Project Name

##  Overview  
This project is a **Spring Boot** application featuring the following:  
- **Spring JPA** for data persistence.  
- **Spring Security** for authentication and authorization.  
- Integration with **Keycloak** for identity and access management.  

It uses **Java 21** as the runtime environment and requires Keycloak to be set up and running in development mode.

---

##  Requirements  
- **Java 21**  
- **Maven** (or any compatible build tool)  
- **Keycloak**  

---

##  Installation  

### 1 Clone the Repository  
```bash
git clone <repository-url>
```
### 2 Configure Keycloak
1. Download **Keycloak** from the [official website](https://www.keycloak.org/downloads).
2. Start Keycloak in development mode:
   ```bash
   bin\kc.bat start-dev
   ```
   Access the Keycloak Admin Console:
  http://localhost:8080/admin
### 3. Build the application:
   ```bash
   mvn clean install
   ```
### 4.Run the application:
1. Start Keycloak
  ```bash
  bin\kc.bat start-dev
```
1. Run the Spring Boot Application
  ```bash
  mvn spring-boot:run
```
### 5.Keytool
```bash
keytool -genkey -alias viz -keyalg RSA -keystore E:\keystore.jks -storepass xxx
```
