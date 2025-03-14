# Project Setup Instructions

## Prerequisites

To run this project, ensure you have the following installed:

1. **Java Development Kit (JDK)**: Version 17 or higher.
2. **Maven**: Version 3.8.1 or higher.
3. **Database**: A running instance of PostgreSQL.

### Application Properties

Ensure the `application.properties` file is located in the `src/main/resources` directory. It should contain the following configurations:

```properties
spring.datasource.url=jdbc:postgresql://<HOST>:<PORT>/<DB_NAME>
spring.datasource.username=<DB_USERNAME>
spring.datasource.password=<DB_PASSWORD>
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Replace `<HOST>`, `<PORT>`, `<DB_NAME>`, `<DB_USERNAME>`, and `<DB_PASSWORD>` with your database details.

## Steps to Run the Project

Follow the steps below to set up and run the project:

1. **Build the Project**:
   Use Maven to compile and build the project:
   ```bash
   mvn clean install
   ```

2. **Run the Application**:
   Use the following command to start the application:
   ```bash
   mvn spring-boot:run
   ```
   
