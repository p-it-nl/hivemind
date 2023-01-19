# Hivemind server

The Hivemind server, is responsible for determining the status of and sharing data with synchronizers.
This server provides a distributed storage based on continuous stateless data synchronization. 
The Hivemind server interprets received 'essences' and synchronizes data to synchronizers.
The communication is pull based, with the server responding to requests and not sending requests.

FUTURE_WORK: With implementation of processor_consistency above should be updated, since processor_consistency will be requiring pull and push based communication

## Setting up a development environment

The application requires Java 17 or later and will support any new Java releases as soon as possible after release.
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

