#!/bin/bash

echo "Installing CloudWatch Agent"
# Download the latest CloudWatch Agent package for Ubuntu
wget https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb -O /tmp/amazon-cloudwatch-agent.deb
sudo dpkg -i /tmp/amazon-cloudwatch-agent.deb

# Create the configuration directory if it doesn't exist
sudo mkdir -p /opt/aws/amazon-cloudwatch-agent/etc

echo "Copying CloudWatch Agent configuration file"
sudo cp /tmp/cloudwatch-agent-config.json /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json

echo "Starting CloudWatch Agent with the provided configuration"
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
  -a fetch-config -m ec2 -c file:/opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json -s

echo "Enabling CloudWatch Agent to start at boot"
sudo systemctl enable amazon-cloudwatch-agent
sudo systemctl restart amazon-cloudwatch-agent

echo "CloudWatch Agent setup completed successfully!"