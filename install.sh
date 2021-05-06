project=(product-adapter-service product-service audit-service registry-service api-gateway)
#project=(product-adapter-service)
for name in "${project[@]}"
do	
	echo "Building project $name"
	cd $name
	./gradlew build -x test
    docker build -t $name .
	cd ..
done
docker-compose up -d