# Spring Batch CSV Importer
## Overview
This *Spring Batch* application is designed to read data from a CSV file and 
efficiently import it into a database. Additionally, it provides a feature to move 
the processed CSV file to another designated folder after successful import. 
This project leverages the power of Spring Batch, a lightweight, 
comprehensive batch processing framework.

## Features
1. **CSV Data Import**: The application uses Spring Batch to read data from a 
specified CSV file and persist it into a relational database. 
The batch processing framework ensures scalability, reliability, and ease 
of maintenance.

2. **Database Interaction**: Utilizing *Spring Data JPA* the application seamlessly 
stores the CSV data into the configured database.

3. **Flexible Configuration**: Key configuration parameters, such as input CSV file 
location, database connection details, and output folder for processed files, 
are easily configurable through external configuration files.

4. **File Movement**: After a successful import, the application intelligently moves 
the processed CSV file to a designated folder, promoting better organization 
and management of input data.