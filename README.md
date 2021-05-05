
# REQUIREMENT
A startup company name "Smart Choice" wants to build a website that can compare the
price of a product from different resources (Tiki, Lazada, Shopee...). In order to get to the
market quickly, they want to build an MVP version with a limited set of features:

1. A Rest API to support their customer compare a product price, the API should
return the product name, current price, the discount rate, promotion...
2. The customer can click on the product to see more details or view the product's
images
3. For audit support, the company wants to keep track of the searching history of
the customer. Failure to store customer activity should have no impact on the
function or performance of the Rest APIs
4. In long term, the website could be used by a lot of people, the company need
to improve the performance of the system and they need to resolve the issues:
5. Getting the product information from many different resources (Tiki, Lazada,
Shopee...) take a lot of time in high traffic period. All the data must be
completely returned from 3rd parties before the API can return to the website.
6. Each request sent to 3rd will be charged for a fee, the company want to
decrease the cost and improve the effectiveness of the application.


# High level Design

In order to satisfy the requirement, we need to build Microservice system that will be decribed in the following picture : 

//PICTURE

- **product-adapter-service** : service to call external 3rd party providers' api (Tiki, Lazada, Shopee...) the fetch/search products from them. The **product-adapter-service** is the only service that has the right to access to provider's api, no direct called will be made to 3rd without going through adapter service.
- **product-service** : the main service that the UI/Customer communicates directly to. This service will call **product-adapter-service** to search a particular product information from 3rd provider.
- **audit-service** : the service used for keep tracking the search history of user
- **api-gateway** : Route along services via a single point. It helps to access to internal services without knowing their host and port. Request will be redirected to exact service need to be serve by situations.
- **registry-service** : Service's registration and discovery in our system. It helps API Gateway routing requests by service name instead of hard-code URL


# Microservice Design

## Product Apdater Service

Access to 3rd parties' api/library, this service role plays as adapter that call 3 parties, other services do not allowed to use or call directly to 3rd api. The benefit it will standardlize the result that we get from 3rd to actual information we want to achieve and return them in the same structure as we define in product-adapter-service. In the future if we want to add more provider or their apis are changes, we just need to update product-adapter-service, other services do not need to care, that means we narrow the risk and maintainance cost.
### Design 

Each call to 3rd will be charged, we need to reduced making call to 3rd api as much as possible. Since all the data must be completely returned from 3rd parties before the API can return to the website, we can asynchronously call to those apis and cache the result base on **product name and provider name** , if one of those providers is unreachable or down, we just need to retry to call on that service, the result from other providers that we succesfully called before will be get from cache, instead of remake the full call of all providers. So what we need is :

- Short-tearm storage
- Fast read time
- Date stored base on product namd and its provider

A HashMap database structure as **REDIS** seems to meet our requirement.

In high traffic situation, their may be thousands calls  from **product-service** to **product-adapter-service** to search different products. In order to handle huge traffic we can use the **Kafka** , the **product-adapter-service** will be a producer that whenever it successfully get information of a product from 3rd parties, it will send that information to message queue. The **product-service** as the consumer will receive the information from queue and update to cache system(Redis), when we have high traffic, we can scale up to multiple instance that in the same message group, so that if at least one instance get the information from queue and update to cache, than other instances do not need to call **product-adapter-service**,than we can save a lost of money. It also ensures that if the **product-service** is down, the information of product will not be lost since it is kept in the queue.
// Picture
### Data model
Product will contains the product name, current price, the discount rate, promotion

// picture





