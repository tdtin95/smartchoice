project=(product-adapter-service product-service audit-service registry-service api-gateway)

mkdir build
for name in "${project[@]}"
do	
	echo "Building project $name"
	cd $name
	./gradlew build -x test
    #docker build -t $name .
	cp build/libs/$name-0.0.1-SNAPSHOT.jar ../build/$name.jar
	cd ..
done
echo "Building Project ...DONE"
echo "Start running docker-compose"
docker-compose up --remove-orphans -d