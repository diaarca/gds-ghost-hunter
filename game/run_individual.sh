#!/bin/bash

if [ $# -ne 2 ]; then
    echo "Usage: $0 configFile numberOfRuns"
    exit 1
fi

CONFIG_FILE=$1
NUM_RUNS=$2
LOST_COUNT=0

echo "Running $NUM_RUNS individual simulations..."

for i in $(seq 1 $NUM_RUNS); do
    echo "=== Simulation $i ==="
    
    # Run single simulation
    OUTPUT=$(mvn exec:java -Dexec.mainClass="GhostHunter" -Dexec.args="$1" -q 2>/dev/null)

    if echo "$OUTPUT" | grep -q "LOST\|TIMEOUT\|FAILED"; then
        LOST_COUNT=$((LOST_COUNT + 1))
        echo "RESULT: LOST"
    else
        echo "RESULT: WON"
    fi
    
done

echo "=== SUMMARY ==="
echo "Total simulations: $NUM_RUNS"
echo "Lost: $LOST_COUNT"
echo "Won: $((NUM_RUNS - LOST_COUNT))"
