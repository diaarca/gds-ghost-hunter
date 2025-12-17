#!/bin/bash

if [ $# -eq 0 ]; then
    echo "Usage: $0 GraphHunter arg1 arg2 arg3"
    exit 1
fi

CLASSNAME=$1
CPLEX_HOME="/Applications/CPLEX_Studio2211"
CP_HOME="$CPLEX_HOME/cpoptimizer"
CPLEX_HOME="$CPLEX_HOME/cplex/"

# Set up library paths for both ARM 64 platoform
LIB_PATHS=(
    "$CP_HOME/bin/arm64_osx"
    "$CPLEX_HOME/bin/arm64_osx"
)

# Join paths with colon
JOINED_PATHS=$(
    IFS=:
    echo "${LIB_PATHS[*]}"
)

export CLASSPATH="./target/classes/:$CP_HOME/lib/ILOG.CP.jar:$CPLEX_HOME/lib/cplex.jar"
export DYLD_LIBRARY_PATH="$JOINED_PATHS"

# Run Java with all necessary parameters
java "$CLASSNAME" $2 $3
