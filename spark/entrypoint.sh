#!/bin/bash
set -e

# Optional: Print configuration for debugging
echo "Launching Spark job..."
echo "ENDPOINT: $ENDPOINT"
echo "ACCESS_KEY: $ACCESS_KEY"
echo "SECRET_KEY: (hidden)"

# Export environment variables if needed by your app
# export ENDPOINT ACCESS_KEY SECRET_KEY

# Default parameters for spark-submit
SPARK_SUBMIT_ARGS="--class App /app/app.jar"

# Allow extra arguments to spark-submit from container command line
exec /opt/spark/bin/spark-submit $SPARK_SUBMIT_ARGS "$@"
