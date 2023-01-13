# Hivemind

Hivemind provides a distributed storage based on continuous stateless data synchronization. 
Hivemind provides decoupling for backpressure handling and replayability of events. Based on the consistency model the event order can be guaranteed.
Hivemind utilizes a representation of the data in the form of an 'essence'. This forms the basis for which the Hivemind determines if and if so what data to synchronize with the synchronizer.
Hivemind can connect to any amount of applications that implement the synchronizer (consuming application).

## Documentation

The documentation can be found at: ...

## Maven central repository

TODO: Upload to maven central
TODO: Split library into two, one being 'Hivemind', the other being 'Hivemind synchronizer' where the latter only provides the requirements for synchronization and omits the server code. 

## Setting up a development environment

The application requires Java 11 or later and will support any new Java releases as soon as possible after release.
The application has an optional dependency on Netty. This is to provide a solid NIO implementation of Hivemind for those that require it.
The application utilizes Maven. Other than that there are zero third-party dependencies to build and run the application!

### Configuring Hivemind

The Hivemind configuration resides in src/main/resources, override these values at any time since the file is being read when the application starts.
The application provides the following configuration:

| Key                             | Description                                                          | Default value                 |
| --------------------------------|----------------------------------------------------------------------|-------------------------------|
| server.type                     | the server type to use for the hivemind. Allows: httpserver, netty   | default                       |
|  			                      | and default. With default being: httpserver  						 |                               |
| consistency.model               | the consistency model to use, options are: 				 			 | eventual_consistency          |
| 					              | 	- low_frequency_eventual_consistency 			 				 | 				        		 |
| 					              | 	- eventual_consistency 			 								 | 				        		 |
| 					              | 	- strong_eventual_consistency 			 						 | 				        		 |
| 					              | 	- processor_consistency 				 						 | 				        		 |
| 					              | 	- sequential_consistency 			 							 | 				        		 |
| port	                          | the port to use	for the Hivemind  					                 | 8000						     |
| max.threads                     | the maximum amount of threads the Hivemind is allowed to use         | 1                             |
| max.queued.tasks                | the maximum amount of tasks allowed to be queued by the Hivemind     | 100                           |

## Testing the Hivemind

The Hivemind provides two types of tests to validate the application functions correctly:
- unit tests -> execute `mvn test`
- component tests (robot framework) -> execute `cd tests && robot hivemind-tests.robot`

Robot framework is an open source automation framework. It is used for test automation in this project.
For more information see: https://robotframework.org

## Deploy a server

To deploy a Hivemind server you need to build an executable JAR by running `mvn clean install`.
After this, you can deploy and run the JAR residing in the `\target` folder as per your choosing.

## Add synchronizers

To connect to the Hivemind and start synchronizing data you will have to configure the application to connect to the Hivemind.
To do this, take the following steps:
	- add the Hivemind synchronizer dependency (not yet in maven central, you can use a local link)
	- extend your data object from HiveResource
	- create an implementation of the ResourceProvider
	- create an instance of SynchronizerConfiguration and provide your required configuration
	- create an implementation of HiveSynchronizer and provide the ResourceProvider and SynchronizerConfiguration in the constructor
	- start the HiveSynchronizer
	
See folder `/example` for a basic example of a synchronizer implementation.

