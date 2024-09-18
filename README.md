# Ethereum Deposit Tracker

## Repository Structure

This repository contains two main components:

1. `EthereumDepositTracker`: Contains the Java source files for the Ethereum Deposit Tracker application.
2. Docker files: Due to their size, the Docker files are stored separately in a Google Drive folder.

## Docker Files Location

The Docker files required to run this application in containers are stored in a separate Google Drive folder due to their large size. You can access these files at the following link:

[https://drive.google.com/drive/folders/1KPIf04mY8kZiMxB1V9fy35sTwAt2pCPg?usp=sharing](https://drive.google.com/drive/folders/1KPIf04mY8kZiMxB1V9fy35sTwAt2pCPg?usp=sharing)

or 

[https://mega.nz/folder/WRZHkRLB#oJMU8cm2S5jRM588DPv-7w](https://mega.nz/folder/WRZHkRLB#oJMU8cm2S5jRM588DPv-7w)  (Faster Download) 

Please download the necessary Docker files from this location before proceeding with the Docker setup.

## Running with Docker

To run the application using Docker, follow these steps:

1. Ensure you have Docker installed on your system.
2. Download the Docker files from the Google Drive link provided above.
3. Navigate to the directory containing the downloaded image tar files.
4. Run the following commands to load the Docker images:

```bash
docker load -i grafana_image.tar
docker load -i ethereum_tracker_image.tar
docker load -i node_exporter_image.tar
docker load -i prometheus_image.tar
docker load -i mysql_image.tar
```

5. After loading the images, navigate to the directory containing the `docker-compose.yml` file and run the following command to start the Docker containers:

```bash
docker-compose up
```

This command will start all the services defined in your `docker-compose.yml` file, including the necessary database setup.

### Setting up Grafana Dashboards

After initializing the Docker containers, follow these steps to set up the Grafana dashboards:

1. Open a web browser and go to `http://localhost:3000`.
2. Log in to Grafana using the default credentials:
   - Username: `admin`
   - Password: `admin`
3. Create two data sources:
   a. MySQL:
      - Name: `MySQL`
      - Host: `mysql:3306`
      - Database: `ethereum_deposits`
      - User: `amrit`
      - Password: `amrit@123`
   b. Prometheus:
      - Name: `Prometheus`
      - URL: `http://host.docker.internal:9090`
4. Import the dashboard JSON files:
   - Navigate to the `docker/Dashboards` folder.
   - In Grafana, go to "Dashboards" > "Import".
   - Import `SystemMetrics.json`:
     - Select Prometheus as the data source.
   - Import `DepositFees.json`:
     - Select MySQL as the data source.

Note: You can modify the MySQL credentials in the `docker-compose.yml` file if needed.

## Running the Java Application Directly

If you prefer to run the Ethereum Deposit Tracker as a standalone Java application without Docker, follow these steps:

### Prerequisites

Ensure you have the following installed on your system:
- Java Development Kit (JDK)
- Maven

### Database Setup

1. Ensure you have MySQL installed and running on your system.
2. Connect to MySQL and run the following commands to create the database and table:

```sql
CREATE DATABASE IF NOT EXISTS ethereum_deposits;
USE ethereum_deposits;
CREATE TABLE IF NOT EXISTS deposits (
    id INT AUTO_INCREMENT PRIMARY KEY,
    block_number BIGINT NOT NULL,
    block_timestamp DATETIME NOT NULL,
    fee DECIMAL(38, 18) NOT NULL,
    transaction_hash VARCHAR(66) NOT NULL UNIQUE,
    pubkey VARCHAR(130) NOT NULL
);
```

### Application Configuration

After setting up the database, update the database connection details in the application:

1. Open the `DatabaseHelper.java` file in the `EthereumDepositTracker` directory.
2. Update the following lines with your MySQL connection details:

```java
private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/ethereum_deposits";
private static final String DATABASE_USER = "amrit";
private static final String DATABASE_PASSWORD = "amrit@123";
```

Replace "amrit" and "amrit@123" with your actual MySQL username and password.

### Building the Project with Maven

Before running the application, you need to build the project with Maven to ensure all dependencies are properly managed:

1. Navigate to the root directory of the Java project (where the `pom.xml` file is located):

```bash
cd path/to/EthereumDepositTracker
```

2. Run the following Maven command to build the project and download all necessary dependencies:

```bash
mvn clean install
```

### Running the Application

After successfully building the project with Maven:

1. Navigate to the directory containing the EthereumDepositTracker Java file:

```bash
cd src/main/java/Tracker
```

2. Run the application using the following command:

```bash
java EthereumDepositTracker.java
```

## Ethereum Deposit Tracker Application

The Ethereum Deposit Tracker is an application designed to monitor and record Ethereum deposits. It tracks block numbers, timestamps, transaction fees, transaction hashes, and public keys associated with deposits on the Ethereum network.

### Getting Updates on Deposit Transactions

To receive updates on each deposit transaction, join our Telegram channel:

https://t.me/+8FPRCgrcWzs3MGJl

This channel will provide real-time notifications about new deposit transactions tracked by the application.

## Demo

--**Click on the video to play**

-**Docker Showcase**

[![YouTube](http://i.ytimg.com/vi/HehYmHj5rcc/hqdefault.jpg)](https://www.youtube.com/watch?v=HehYmHj5rcc)

-**Java Showcase**

[![YouTube](http://i.ytimg.com/vi/p1v8qyZjX6A/hqdefault.jpg)](https://www.youtube.com/watch?v=p1v8qyZjX6A)

## Troubleshooting

### ContractABI.json File Not Found Error

If you encounter an error stating that the `ContractABI.json` file is not found, follow these steps:

1. Locate the `ContractABI.json` file in the `resources` folder of your project.
2. Copy the full path location of the `ContractABI.json` file.
3. Open the Java file that contains the `ABI_FILE_PATH` constant (likely `EthereumDepositTracker.java`).
4. Replace the existing path with the full path you copied:

```java
private static final String ABI_FILE_PATH = "/full/path/to/your/ContractABI.json";
```

Make sure to use forward slashes (/) in the path, even on Windows systems.
