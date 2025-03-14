packer {
  required_plugins {
    amazon = {
      version = ">= 1.0.0, <2.0.0"
      source  = "github.com/hashicorp/amazon"
    }
    googlecompute = {
      source  = "github.com/hashicorp/googlecompute"
      version = "~> 1"
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

variable "POSTGRES_USER" {
  type    = string
  default = "dummyUser"
}

variable "POSTGRES_PASSWORD" {
  type    = string
  default = "dummyPass"
}

variable "POSTGRES_DB" {
  type    = string
  default = "dummyDB"
}

variable "webapp_zip_path" {
  type    = string
  default = "./dummy.zip"
}

variable "ami_users" {
  description = "List of AWS account IDs with which to share the AMI"
  type        = list(string)
  default     = ["225989346736", "277707141027"]
}

locals {
  ami_name     = "webapp_aws_${formatdate("YYYY_MM_DD_HH_mm_ss", timestamp())}"
  gcp_dev_name = "webapp-dev-${formatdate("YYYY-MM-DD-HH-mm-ss", timestamp())}"
}

# BUILDERS
source "amazon-ebs" "ubuntu_app" {
  region        = var.aws_region
  instance_type = var.instance_type
  ssh_username  = var.ssh_username

  source_ami = var.source_ami
  ami_users  = var.ami_users

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

variable "gcp_project_id_dev" {
  type        = string
  description = "GCP DEV Project ID"
  default     = "development-452004"
}

variable "gcp_zone" {
  type        = string
  default     = "us-central1-a"
  description = "The GCP zone for image build"
}

variable "gcp_service_account_key_file_dev" {
  type        = string
  description = "Path to the GCP DEV service account key JSON file"
  default     = "./dummy_dev_key.json"
}

variable gcp_source_image_family {
  type    = string
  default = "ubuntu-2004-lts"
}

variable gcp_instance_type {
  type        = string
  description = "GCP instance type"
  default     = "e2-micro"
}

variable gcp_disk_type {
  type    = string
  default = "pd-standard"
}


source "googlecompute" "gcp_dev" {
  project_id          = var.gcp_project_id_dev
  zone                = var.gcp_zone
  machine_type        = var.gcp_instance_type
  ssh_username        = var.ssh_username
  source_image_family = var.gcp_source_image_family
  image_name          = local.gcp_dev_name
  image_description   = "Custom app image for GCP DEV"
  disk_size           = 25
  disk_type           = var.gcp_disk_type
  credentials_file    = var.gcp_service_account_key_file_dev
}

# PROVISIONERS
build {
  name = "build-ubuntu-app"

  sources = [
    "source.amazon-ebs.ubuntu_app",
    "source.googlecompute.gcp_dev"
  ]

  provisioner "file" {
    source      = "./src/main/scripts/setup.sh"
    destination = "/tmp/setup.sh"
  }

  provisioner "file" {
    source      = var.webapp_zip_path
    destination = "/tmp/webapp.zip"
  }

  provisioner "file" {
    source      = "./src/main/scripts/webapp.service"
    destination = "/tmp/webapp.service"
  }

  provisioner "shell" {
    environment_vars = [
      "DEBIAN_FRONTEND=noninteractive",
      "CHECKPOINT_DISABLE=1",
      "POSTGRES_USER=${var.POSTGRES_USER}",
      "POSTGRES_PASSWORD=${var.POSTGRES_PASSWORD}",
      "POSTGRES_DB=${var.POSTGRES_DB}"
    ]
    inline = [
      "chmod +x /tmp/setup.sh",
      "sudo -E /tmp/setup.sh"
    ]
  }
}
