#!/bin/bash

# Update the package lists
echo "Updating package lists and upgrading packages"
sudo apt update && sudo apt upgrade -y

# Install necessary packages
sudo apt-get install zip unzip postgresql postgresql-contrib openjdk-21-jdk maven -y

echo "Java and maven installed successfully"

# Unzip the application into /opt/csye6225 directory
echo "Unzipping application to /opt/csye6225 directory"
sudo mkdir -p /opt/csye6225
sudo unzip /tmp/webapp.zip -d /opt/csye6225


# Run Maven build to install dependencies and compile the application
echo "Running Maven build..."
cd /opt/csye6225
mvn clean install -DskipTests


# Create the application.properties file with PostgreSQL credentials
echo "Creating application.properties..."
sudo mkdir -p /opt/csye6225/src/main/resources
sudo tee /opt/csye6225/src/main/resources/application.properties <<EOF >/dev/null
spring.datasource.url=jdbc:postgresql://localhost:5432/${POSTGRES_DB}
spring.datasource.username=${POSTGRES_USER}
spring.datasource.password=${POSTGRES_PASSWORD}
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
EOF

# Create the PostgreSQL database
echo "Creating the database..."
sudo -u postgres psql -c "CREATE DATABASE csye6225db;"

# Create a new Linux group for the application
echo "Creating new Linux group 'csye6225group'"
sudo groupadd csye6225group

# Create a new user for the application with no login shell
echo "Creating new user 'csye6225user' (no login shell)"
sudo useradd -m -g csye6225group -s /usr/sbin/nologin csye6225user

# Set password for PostgreSQL user
PASSWORD="${POSTGRES_PASSWORD}"

# Set the password for user 'csye6225user'
echo "Setting password for user 'csye6225user'..."
echo "csye6225user:$PASSWORD" | sudo chpasswd

# Create PostgreSQL user and grant privileges
echo "Creating PostgreSQL user and granting privileges..."
sudo sed -i "s/local   all             all             peer/local   all             all             md5/" /etc/postgresql/*/main/pg_hba.conf
sudo sed -i "s/host    all             all             127.0.0.1\/32            trust/host    all             all             127.0.0.1\/32            md5/" /etc/postgresql/*/main/pg_hba.conf
sudo sed -i "s/host    all             all             ::1\/128                 trust/host    all             all             ::1\/128                 md5/" /etc/postgresql/*/main/pg_hba.conf

# Restart PostgreSQL for the changes to take effect
sudo systemctl restart postgresql

# Set the PostgreSQL user for your application
sudo -u postgres psql -c "CREATE USER csye6225user WITH ENCRYPTED PASSWORD '$PASSWORD';"
sudo -u postgres psql -c "ALTER DATABASE csye6225db OWNER TO csye6225user;"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE csye6225db TO csye6225user;"
sudo -u postgres psql -c "ALTER ROLE csye6225user WITH LOGIN;"
sudo -u postgres psql -c "ALTER ROLE csye6225user WITH CREATEDB;"
sudo -u postgres psql -c "ALTER ROLE csye6225user WITH CREATEROLE;"

echo "PostgreSQL user 'csye6225user' has been created and granted all privileges on 'csye6225db'"

# Update the permissions
echo "Setting permissions for /opt/csye6225 directory"
sudo chown -R csye6225user:csye6225group /opt/csye6225
sudo chmod -R 755 /opt/csye6225

# Configure systemd service
echo "Configuring systemd service"
sudo cp /tmp/webapp.service /etc/systemd/system/webapp.service
sudo systemctl daemon-reload
sudo systemctl enable webapp.service

echo "Application setup completed successfully!"
