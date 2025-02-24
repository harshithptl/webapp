packer {
  required_plugins {
    amazon = {
      version = ">= 1.0.0, <2.0.0"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "instance_type" {
  type    = string
  default = "t2.small"
}

variable "ssh_username" {
  type    = string
  default = "ubuntu"
}

variable source_ami {
  type    = string
  default = "ami-04b4f1a9cf54c11d0"
}

locals {
  ami_name = "webapp_${formatdate("YYYY_MM_DD", timestamp())}"
}

# BUILDERS
source "amazon-ebs" "ubuntu_app" {
  region        = var.aws_region
  instance_type = var.instance_type
  ssh_username  = var.ssh_username

  source_ami = var.source_ami

  aws_polling {
    delay_seconds = 120
    max_attempts  = 50
  }

  ami_regions = [
    "us-east-1"
  ]

  ami_name        = local.ami_name
  ami_description = "Custom app image with JDK, Maven, PostgreSQL, etc."

  launch_block_device_mappings {
    device_name           = "/dev/sda1"
    volume_size           = 8
    volume_type           = "gp2"
    delete_on_termination = true
  }
}

# PROVISIONERS
build {
  name = "build-ubuntu-app"

  sources = [
    "source.amazon-ebs.ubuntu_app"
  ]

  provisioner "file" {
    source      = "./src/main/scripts/setup.sh"
    destination = "/tmp/setup.sh"
  }

  provisioner "file" {
    source      = "./webapp.zip"
    destination = "/tmp/webapp.zip"
  }

  provisioner "file" {
    source      = "./src/main/scripts/webapp.service"
    destination = "/tmp/webapp.service"
  }

  provisioner "shell" {
    environment_vars = [
      "POSTGRES_USER=${POSTGRES_USER}",
      "POSTGRES_DB=${POSTGRES_DB}",
      "POSTGRES_PASSWORD=${POSTGRES_PASSWORD}"
    ]
    inline = [
      "chmod +x /tmp/setup.sh",
      "sudo /tmp/setup.sh"
    ]
  }
}
