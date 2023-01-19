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

## Modules

Hivemind has two separate modules based on separation of concern:
- server -> the Hivemind server, responsible for determining the status of and sharing data with synchronizers
- synchronizer -> the Hivemind synchronizer, responsible for connecting to the Hivemind and processing requests and responses. This package is to be integrated in applications

Available synchronizers:
- Java
- Android
- ...more to come...

## Server

Refer to [/server](/server).

## Add synchronizers

Refer to [/synchronizer](/synchronizer).
For Android, refer to  [/synchronizer-android](/synchronizer-android).

