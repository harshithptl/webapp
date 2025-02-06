#!/bin/bash

# Update the package lists
echo "Updating package lists and upgrading packages"
sudo apt update && sudo apt upgrade -y
# Install zip unzip
sudo apt-get install zip unzip

# Install PostgreSQL
echo "Installing PostgreSQL"
sudo apt install postgresql postgresql-contrib -y

# Unzip the application into /opt/csye6225 directory
echo "Unzipping application to /opt/csye6225 directory"
sudo mkdir -p /opt/csye6225
sudo unzip /tmp/webapp.zip -d /opt/csye6225

#Remove the zip file
sudo rm -rf /tmp/webapp.zip

# Create the database in PostgreSQL
echo "Creating the database..."
sudo -u postgres psql -c "CREATE DATABASE csye6225db;"

# Create a new Linux group for the application
echo "Creating new Linux group 'csye6225group'"
sudo groupadd csye6225group

# Create a new user for the application
echo "Creating new user 'csye6225user'"
sudo useradd -m -g csye6225group -s /bin/bash csye6225user

# Extract the password from application.properties
PASSWORD=$(grep '^db.password=' /opt/csye6225/webapp/src/main/resources/application.properties | cut -d '=' -f2)

# Ensure the password is not empty
if [ -z "$PASSWORD" ]; then
  echo "Error: Password not found in application.properties"
  rm -rf /opt/csye6225/webapp
  exit 1
fi

# Set the password for the user
echo "Setting password for user 'csye6225user'..."
echo "csye6225user:$PASSWORD" | sudo chpasswd

echo "Successfully set password for user 'csye6225user'"

# Create PostgreSQL user and grant privileges
#echo "Creating PostgreSQL user and granting privileges..."
#sudo -u postgres psql -c "CREATE USER csye6225user WITH ENCRYPTED PASSWORD '$PASSWORD';"
#sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE csye6225db TO csye6225user;"
#sudo -u postgres psql -c "ALTER ROLE csye6225user WITH LOGIN;"
#
#echo "PostgreSQL user 'csye6225user' has been created and granted all privileges on 'csye6225db'"

# Update the permissions
echo "Setting permissions for /opt/csye6225 directory"
sudo chown -R csye6225user:csye6225group /opt/csye6225
sudo chmod -R 755 /opt/csye6225

echo "Application setup completed successfully!"
