# miRNAFastqToAggregatedFasta

miRNAFastqToAggregatedFasta is a Java application for generating an aggregated FASTA file from a set of small RNA FASTQ files. This repository contains the source code and instructions for building and using the application.

## Table of Contents

- [Installation](#installation)
- [Building](#building)
- [Usage](#usage)

## Installation

### Prerequisites

- Java Development Kit (JDK) 11 or higher [(how to install JDK)](https://bioweb01.qut.edu.au/Install_java.html) 
- Maven [(how to install Maven)](https://bioweb01.qut.edu.au/Install_maven.html)

### Clone the Repository

```sh
git clone https://github.com/anjiyuan/miRNAFastqToAggregatedFasta.git
cd miRNAFastqToAggregatedFasta
```
## Building
To build the project from source, follow these steps:

1. Clone the Repository:
```sh
git clone https://github.com/anjiyuan/miRNAFastqToAggregatedFasta.git
cd miRNAFastqToAggregatedFasta
```
2. Build the Project:
Use Maven to build the project:
```sh
mvn clean install
```
This will compile the source code and package the application into a JAR file located in the target directory.
## Usage
To use the miRNAFastqToAggregatedFasta application, run the following command:
```sh
java -cp target/miRNAFastqToAggregatedFasta-1.0.jar qut.miRNAFastqToAggregatedFasta.miRNAFastqToAggregatedFasta <fastq_folder> <prefix_output_filename>
```
Example
```sh
java -cp target/miRNAFastqToAggregatedFasta-1.0.jar qut.miRNAFastqToAggregatedFasta.miRNAFastqToAggregatedFasta demoData demoData/demo.aggregated.fa
```
