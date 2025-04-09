create_shared_network:
	docker network create shared_network || true
build-dev:
	docker-compose -f docker-compose-dev.yaml build
run-dev: create_shared_network build-dev
	docker-compose -f docker-compose-dev.yaml up
run-frontend-dev:
	VITE_API_URL="http://localhost:8000" npm run dev
clean-dev:	down-dev
	docker system prune -f
down-dev:
	docker-compose -f docker-compose-dev.yaml down || true
build:
	docker-compose -f docker-compose-prod.yaml build --no-cache
run: create_shared_network build
	docker-compose -f docker-compose-prod.yaml up
clean: backup_db down
	docker system prune -f
down:
	docker-compose -f docker-compose-prod.yaml down || true
backup_db:
	docker exec -it app_app-db_1 pg_dump -U graduatebetter -d graduate_better > ss.sql
