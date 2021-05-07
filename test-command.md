# GET Access token
```curl
curl --location --request POST "localhost:7777/auth/realms/master/protocol/openid-connect/token" --header "Content-Type: application/x-www-form-urlencoded" --data-urlencode "username=admin" --data-urlencode "password=admin" --data-urlencode "client_id=admin-cli" --data-urlencode "grant_type=password"
```
Note that token lifespan is 1 minute. After that you should get the token again

# Compare product by product name

Compare product by making request to api-gateway, by default we should should by product name via format `product{{i}} name`.

```curl
curl --location --request GET "localhost:9080/api/product-information?productName=product2%20name" --header "username: tdtin" --header "Authorization: Bearer {{your access token}}"
```
Making multiple same requests at in short time to see it really cached and save the performance

# See mock data
I use mock https://mockapi.io/ to create mock data.
You can see all data by making request :

```curl
curl --location --request GET "https://608ced319f42b20017c3e613.mockapi.io/api/products"

curl --location --request GET "https://608ced319f42b20017c3e613.mockapi.io/api/lazadaproducts"

curl --location --request GET "https://608ced319f42b20017c3e613.mockapi.io/api/tikiproducts"
```
