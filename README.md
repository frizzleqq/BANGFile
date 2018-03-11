# BANG-file Clustering System

BANG file is a multidimensional structure of the grid file type.
It organizes the value space surrounding the data values, instead of comparing the data values themselves.
Its tree structured directory partitions the data space into block regions with successive binary divisions on dimensions.
The clustering algorithm identifies densely populated regions as cluster centers and expands those with neighboring blocks.

Inserted data has to be normalized to an interval [0, 1].

## Features

* Standalone application to apply clustering method on dataset
* Support for CSV files with configurable delimiter and decimal symbol
* Reading dataset incrementally
* Visualizing clustering result in 2-dimensional grid directory
* Save clustering result to files (Log of clustering system and Cluster-content)
* Extensibility with new grid-based clustering systems or new data source types

The Console of the application:

![GUI Console](/doc/gui_console.png?raw=true "Log of clustering system BANG file")

Grid directory of the BANG file:

![GUI Grid Directory](/doc/gui_grid.png?raw=true "Grid directory of the BANG file")

## How to build

The Clustering System uses the Maven framework.

#### Prerequisites for building the project:
* Maven 3+
* Java JDK 1.8

#### Bulding it

Use the following Maven commands to build it:

```
$ mvn clean install
```

Optionally you can specify -Dmaven.test.skip=true to skip the tests

The result of the build will be BANG file clustering package located in ```target```.

#### Running the tests

This will run all unit tests in the project (and sub-modules):

```
$ mvn test
```

### Creating standalone JAR

Use the following command to create a standalone executable JAR with all dependencies included:

````
$ mvn clean compile assembly:single
````