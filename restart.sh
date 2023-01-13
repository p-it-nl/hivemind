#!/bin/bash

echo "Stopping current running hivemind (if applicable)"

entry=$(ps -fea|grep -i java | grep hivemind-0.1.jar)
pid=$(echo $entry | cut -c6-10)

if [ -z "$pid" ]
then
    echo "Hivemind is not running..."
else
    echo "Currently running at $pid, stopping"
    kill $pid 2> /dev/null
	sleep 10
fi

echo "Starting hivemind"

java -jar /home/hivemind/hivemind/hivemind-0.1.jar &

echo "Hivemind is now up and running"

exit 0