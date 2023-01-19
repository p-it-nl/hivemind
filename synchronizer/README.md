# Hivemind synchronizer

The Hivemind synchronizer is responsible for connecting to the Hivemind and processing requests and responses. 
This package is to be integrated in applications and can be used to synchronize with one Hivemind server at a time.
The data shared between synchronizers can be anything, be must be consistent throughout the synchronizers connecting to the same Hivemind server.

## Setting up a development environment

The package requires Java 17 or later and will support any new Java releases as soon as possible after release.
The package has zero third-party dependencies.

### Configuring Hivemind synchronizer

The Hivemind synchronizer is configured at runtime by creating a new instance of the SynchronizerConfiguration.
This configuration allows specifying the URI used to reach the Hivemind server, the consistency model to use for the synchronization and optionally allows overwriting the period between requests.

## Testing the Hivemind synchronizer

The Hivemind provides two types of tests to validate the application functions correctly:
- unit tests -> execute `mvn test`
- FUTURE_WORK: integration tests (robot framework) -> execute `cd tests && robot hivemind-synchronizer-tests.robot`

Robot framework is an open source automation framework. It is used for test automation in this project.
For more information see: https://robotframework.org

## Implement synchronizer

To connect to the Hivemind and start synchronizing data you will have to configure your application to connect to the Hivemind.
To do this, take the following steps:
	- add the Hivemind synchronizer dependency: io.hivemind;hivemind-synchronizer;0.1; (not yet in maven central, you can use a local link)
	- extend your data object from HiveResource
	- create an implementation of the ResourceProvider
	- create an instance of SynchronizerConfiguration and provide your required configuration
	- create an implementation of HiveSynchronizer and provide the ResourceProvider and SynchronizerConfiguration in the constructor
	- start the HiveSynchronizer
	
See folder `/example` for a basic example of a synchronizer implementation.

