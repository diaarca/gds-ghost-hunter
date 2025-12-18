#!/bin/bash

if [ $# -eq 0 ]; then
    echo "Usage: $0 configFile"
    exit 1
fi

mvn exec:java -Dexec.mainClass="GhostHunter" -Dexec.args="$1"