#!/bin/bash

# Update the package lists
echo "Updating package lists and upgrading packages"
sudo apt update && sudo apt upgrade -y
# Install zip unzip
sudo apt-get install zip unzip

# Install PostgreSQL
echo "Installing PostgreSQL"
sudo apt install postgresql postgresql-contrib -y

echo "Setting up Java and Maven"
sudo apt install open-jdk -y
sudo apt install maven -y
echo "Java and maven installed successfully"

# Unzip the application into /opt/csye6225 directory
echo "Unzipping application to /opt/csye6225 directory"
sudo mkdir -p /opt/csye6225
sudo unzip /tmp/webapp.zip -d /opt/csye6225

echo "Creating application.properties..."
sudo mkdir -p /opt/csye6225/webapp/src/main/resources
sudo tee /opt/csye6225/webapp/src/main/resources/application.properties <<EOF >/dev/null
spring.datasource.url=jdbc:postgresql://localhost:5432/\${POSTGRES_Db}
spring.datasource.username=\${POSTGRES_USER}
spring.datasource.password=\${POSTGRES_PASSWORD}
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
EOF

# Create the database in PostgreSQL
echo "Creating the database..."
sudo -u postgres psql -c "CREATE DATABASE csye6225db;"

# Create a new Linux group for the application
echo "Creating new Linux group 'csye6225group'"
sudo groupadd csye6225group

echo "Creating new user 'csye6225user' (no login shell)"
sudo useradd -m -g csye6225group -s /usr/sbin/nologin csye6225user

# Extract the password from application.properties
PASSWORD="${POSTGRES_PASSWORD}"

# Set the password for the user
echo "Setting password for user 'csye6225user'..."
echo "csye6225user:$PASSWORD" | sudo chpasswd

echo "Successfully set password for user 'csye6225user'"

 Create PostgreSQL user and grant privileges
echo "Creating PostgreSQL user and granting privileges..."
sudo sed -i "s/local   all             all             peer/local   all             all             md5/" /etc/postgresql/*/main/pg_hba.conf
sudo sed -i "s/host    all             all             127.0.0.1\/32            trust/host    all             all             127.0.0.1\/32            md5/" /etc/postgresql/*/main/pg_hba.conf
sudo sed -i "s/host    all             all             ::1\/128                 trust/host    all             all             ::1\/128                 md5/" /etc/postgresql/*/main/pg_hba.conf

sudo -u postgres psql -c "CREATE USER csye6225user WITH ENCRYPTED PASSWORD '$PASSWORD';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE csye6225db TO csye6225user;"
sudo -u postgres psql -c "ALTER ROLE csye6225user WITH LOGIN;"
sudo -u postgres psql -c "ALTER ROLE csye6225user WITH CREATEDB";
sudo -u postgres psql -c "ALTER ROLE csye6225user WITH CREATEROLE";
echo "PostgreSQL user 'csye6225user' has been created and granted all privileges on 'csye6225db'"

# Update the permissions
echo "Setting permissions for /opt/csye6225 directory"
sudo chown -R csye6225user:csye6225group /opt/csye6225
sudo chmod -R 755 /opt/csye6225

echo "Configuring systemd service"
sudo cp /tmp/webapp.service /etc/systemd/system/webapp.service
sudo systemctl daemon-reload
sudo systemctl enable webapp.service


echo "Application setup completed successfully!"
