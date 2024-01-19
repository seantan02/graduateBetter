runPostgre:
	docker run -d --name postgres-container -e POSTGRES_DB=graduate_better -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=7412563j -p 5432:5432 postgres:latest
refresh: dockerBuildImage
	docker-compose up -d
mavenCleanInstall:
	mvn clean install -DskipTests
dockerBuildImage: dockerStopService dockerRmService mavenCleanInstall
	docker build -t graduate-better/app .
dockerStopService:
	docker ps | grep ":8080->8080.tcp" | awk '{print $$1}' | xargs docker stop
dockerRmService:
	docker ps | grep ":8080->8080.tcp" | awk '{print $$1}' | xargs docker rm
