#!/bin/bash

if [ $# -ne 2 ]; then
    echo "Usage: $0 configFile numberOfRuns"
    exit 1
fi

CONFIG_FILE=$1
NUM_RUNS=$2
TOTAL_LOST_COUNT=0

for i in $(seq 1 $NUM_RUNS); do
    # Run simulation and capture output silently
    OUTPUT=$(timeout 10s mvn exec:java -Dexec.mainClass="GhostHunter" -Dexec.args="$1" -q 2>/dev/null)
    EXIT_CODE=$?
    
    if [ $EXIT_CODE -eq 124 ] || [ $EXIT_CODE -ne 0 ]; then
        # Timeout or error - count as all lost
        echo "run $i for $NBSIMU simulations: total loss=ALL total won=0 win ratio:0%"
    else
        # Count "Lost" messages in this execution
        LOST_IN_THIS_EXEC=$(echo "$OUTPUT" | grep -c "Lost" || echo "0")
        
        # Extract nbSimu from config to calculate total simulations
        NBSIMU=$(grep "nbSimu" "$CONFIG_FILE" | grep -o '[0-9]*')
        
        WON_IN_THIS_EXEC=$((NBSIMU - LOST_IN_THIS_EXEC))
        WIN_RATIO=$(echo "scale=1; $WON_IN_THIS_EXEC * 100 / $NBSIMU" | bc -l)

        echo "run $i for $NBSIMU simulations: total loss=$LOST_IN_THIS_EXEC total won=$WON_IN_THIS_EXEC win ratio:$WIN_RATIO%"

        TOTAL_LOST_COUNT=$((TOTAL_LOST_COUNT + LOST_IN_THIS_EXEC))
    fi
done

# Final summary to file only
{
echo "=== FINAL SUMMARY ==="
echo "Total executions: $NUM_RUNS"
echo "Simulations per execution: $NBSIMU"
echo "Total lost simulations: $TOTAL_LOST_COUNT"
TOTAL_WON_COUNT=$((NUM_RUNS * NBSIMU - TOTAL_LOST_COUNT))
echo "Total won simulations: $TOTAL_WON_COUNT"
OVERALL_WIN_RATIO=$(echo "scale=1; $TOTAL_WON_COUNT * 100 / ( $NUM_RUNS * $NBSIMU )" | bc -l)
echo "Overall win ratio: $OVERALL_WIN_RATIO%"
} > res.txt