#!/bin/bash

# Update the package lists
echo "Updating package lists and upgrading packages"
sudo apt update && sudo apt upgrade -y

# Install necessary packages
sudo apt-get install zip unzip openjdk-21-jdk maven -y

echo "Java and maven installed successfully"

# Unzip the application into /opt/csye6225 directory
echo "Unzipping application to /opt/csye6225 directory"
sudo mkdir -p /opt/csye6225
sudo unzip /tmp/webapp.zip -d /opt/csye6225

# Run Maven build to install dependencies and compile the application
echo "Running Maven build..."
cd /opt/csye6225
mvn clean install -DskipTests

# Create a new Linux group for the application
echo "Creating new Linux group 'csye6225group'"
sudo groupadd csye6225group

# Create a new user for the application with no login shell
echo "Creating new user 'csye6225user' (no login shell)"
sudo useradd -m -g csye6225group -s /usr/sbin/nologin csye6225user

# Update the permissions
echo "Setting permissions for /opt/csye6225 directory"
sudo chown -R csye6225user:csye6225group /opt/csye6225
sudo chmod -R 755 /opt/csye6225

# Set up log directory for application logs
echo "Setting up log directory in /var/log/tomcat9"
sudo mkdir -p /var/log/webapp
sudo chown -R csye6225user:csye6225group /var/log/webapp
sudo chmod -R 755 /var/log/webapp

# Configure systemd service
echo "Configuring systemd service"
sudo cp /tmp/webapp.service /etc/systemd/system/webapp.service
sudo systemctl daemon-reload
sudo systemctl enable webapp.service

echo "Application setup completed successfully!"
