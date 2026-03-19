run-spark=docker run --rm   --name spark_app   --network pipeline   -e ENDPOINT="http://minio:9000"   -e ACCESS_KEY="minioadmin"   -e SECRET_KEY="minioadmin" spark-job:latest
periodically-run-spark = while true; do $(run-spark) ; sleep 10; done

.PHONY: up-and-run-spark
up-and-run-spark: up
	echo running spark aggregation job periodically
	$(periodically-run-spark)

.PHONY: up
up: down
	docker compose up -d

.PHONY: down
down:
	docker compose down --remove-orphans
