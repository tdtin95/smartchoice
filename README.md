# SMART CHOICE

- [SMART CHOICE](#smart-choice)
- [REQUIREMENT](#requirement)
- [High level Design](#high-level-design)
- [Microservice Design](#microservice-design)
  - [Product Apdater Service](#product-apdater-service)
    - [Service Design](#service-design)
    - [Database design](#database-design)
  - [Product Service](#product-service)
    - [Service Design](#service-design-1)
    - [Database design](#database-design-1)
  - [Audit service](#audit-service)
    - [Service design](#service-design-2)
    - [Database design](#database-design-2)
  - [Api Gateway](#api-gateway)
    - [Service Design](#service-design-3)
  - [Registry Service](#registry-service)
    - [Service Design](#service-design-4)
  - [Authentication Server](#authentication-server)
    - [Design](#design)
- [Whole system design](#whole-system-design)
- [How to install](#how-to-install)
  - [Prerequisite](#prerequisite)
  - [Install](#install)
    - [Run Enviroment setup](#run-enviroment-setup)
    - [Service port](#service-port)
    - [Testing command](#testing-command)
- [Applied Principles](#applied-principles)
    - [YAGNI (You aren't gonna need it)](#yagni-you-arent-gonna-need-it)
    - [Inversion of Control](#inversion-of-control)
    - [DRY (Don't repeat your self)](#dry-dont-repeat-your-self)
    - [Single Responsibility Principle](#single-responsibility-principle)
    - [SOC (Separation of Concerns)](#soc-separation-of-concerns)
    - [KISS (Keep it simple stupid)](#kiss-keep-it-simple-stupid)
    - [Low Coupling](#low-coupling)
- [Design Pattern](#design-pattern)
- [Folder Structure And Library Usage](#folder-structure-and-library-usage)
  - [Folder Structure](#folder-structure)
  - [Library usage](#library-usage)
    - [Development libraries](#development-libraries)
    - [Test libraries](#test-libraries)
- [Improve to be perfect](#improve-to-be-perfect)
  - [Logging system](#logging-system)
  - [Configuration System](#configuration-system)
  - [Registry service and loadbalancing replacement](#registry-service-and-loadbalancing-replacement)
  - [Authentication adapter service](#authentication-adapter-service)


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

![High Level](docs/high-level-design.png)

- **product-adapter-service** : service to call external 3rd party providers' api (Tiki, Lazada, Shopee...) to fetch/search products from them. The **product-adapter-service** is the only service that has the right to access to provider's api, no direct call will be made to 3rd without going through adapter service.
- **product-service** : the main service that the UI/Customer communicates directly to. This service will call **product-adapter-service** to search a particular product information from 3rd provider.
- **audit-service** : the service used for keep tracking the search history of user
- **api-gateway** : Route along services via a single point. It helps to access to internal services without knowing their host and port. Request will be redirected to exact service need to be serve by situations.
- **registry-service** : Service's registration and discovery in our system. It helps API Gateway routing requests by service name instead of hard-code URL
- **authentication-server** : An Auth server to support OAuth sercurity as well as provide the security protection for service call.


# Microservice Design

## Product Apdater Service

Access to 3rd parties' api/library, this service role plays as adapter that call 3rd parties, other services do not allowed to use or call directly to 3rd api. The benefit is that it will standardlize the result that we get from 3rd to actual information we want to achieve and return them in the same structure as we define in product-adapter-service. In the future if we want to add more provider or their apis are changed, we just need to update product-adapter-service, other services do not need to care, that means we narrow the risk and maintainance cost.
### Service Design 

Each call to 3rd will be charged, we need to reduced making call to 3rd api as much as possible. Since all the data must be completely returned from 3rd parties before the API can return to the website, we can asynchronously call to those apis and temporary cache the result base on **product name and provider name** to reduce to call to 3rd in short time, if one of those providers is unreachable or down, we just need to retry to call on that service, the result from other providers that we succesfully called before will be get from cache, instead of remake the full call of all providers. So what we need are :

- Short-tearm storage
- Fast read time
- List of product stored base on product name and its provider

A HashMap database structure as **REDIS** seems to meet our requirement.

In high traffic situation, there may be thousands calls  from **product-service** to **product-adapter-service** to search different products. In order to handle huge traffic we can use the **Kafka** , the **product-adapter-service** will be a producer that whenever it successfully get information of a product from 3rd parties, it will send that information to message queue. The **product-service** as a consumer will receive the information from queue and update to cache system(Redis), when we have high traffic, we can scale up to multiple instances that in the same message group, so that if at least one instance is able to get the information from queue and update to cache, then other instances do not need to call **product-adapter-service**, a lost of money will be saved. It also ensures that if the **product-service** is down, the information of product will not be lost since it is kept in the queue.

### Database design
Product will contains the product name, current price, the discount rate, promotion. Image is base64 encoded content that can be directly use on the UI to show product image.

![Product Schema](docs/product-adapter-service-db-design.png)


## Product Service

Product service receive search request to return the product information from 3rd providers in order to compare the price between them. Product service is the main service that communicate with customer request.

### Service Design

When product-service recieve a request to get information for a product , it will call product-adapter-service to get those information from providers. Each information for a product will be grouped by productName call ProductGroup. In order to make the system performance and saving the cost, each time we have a ProductGroup information, we will cache it to Redis (note that it will be a redis separated databa from product-adapter-service, redis support us to have multiple databse in the same Redis server). This cache can have longer lifespan, since product information of a product does not change too frequently, cache lifespan is surely configurable.

Product service as a consumer also listens on Kafka queue, whenever it recieve a product information, it will update Redis cache. In scalable perspective, when a lot of requests are made to multiple product-service instance to search to same productName, if at least one instance have recieved that product information from product-adapter-service and cached it, then other intances just get from cache instead of make a call to product-adapter-service. This approach can reduce calls to product-adapter-service, fast reaction. If one instance of product service is dead, when a new instance created, it also can get information from message queue or cache. The interaction between product-service and product-adapter-service is decribed in the following picture

![Product flow](docs/product-flow.png)

### Database design
Each product will be grouped in a ProductGroup that contains information from all 3rd providers

![ProductGroup Schema](docs/ProductGroup.png)

## Audit service
The service used for keep tracking the search history of user, whenever user makes a search product call, this information will be tracked.

### Service design
Whenever a search request has been made to product-service, we use spring AOP and define the PointCut and give it and Advice to record the search. 

Due to the failure to store customer activity should have no impact on the function or performance of the Rest APIs, therefore we use Kafka as message queue with product-service as a Producer(Source) and audit-service as a Consumer(Sink). Then if the audit-service service is dead or unreachable, there is no impact to product-service, and the message will be safe in queue, the data will not be lost

### Database design
We need to keep track which productName that user search, timestamp, performed by which user.
What we need from a database is 
- Long-term storage.
- Handle large data, in short time (up to the traffic to product-service)
- None-relational database
- Flexible data structure
  
Then mongoDb is a good fit.

![History Schema](docs/History.png)


## Api Gateway

In production environments, the host and port of a service are frequently change due to the scalability of the system, a lot of instance of services will be destroyed or created due to the traffic and system need, managing the endpoint of service is really a challange. Api gateway is a solution that external request just need to communicate to service systems via a single point, api gateway will decide which service will be called base on which request, load balancing is also supported to redirect to instances of services. Api gateway also helps for processing common request needs as : propagting jwt token, propagating header , ssl certificate,...

### Service Design
We use spring-cloud-netflix-client to route external request to correct service.

We use spring-boot-starter-oauth2-resource-server as resource server and secure the request.

User will be authenticated by auth server, after that a jwt token will be generated, API gateway will authenticate it with spring-boot-starter-security, username will be extracted and progated through services by ZuulFilter. An alternative is using nginx , it is more powerful, but for this assignment, Zuulfilter is the great choice to quickly bootstrap our system.

## Registry Service
We need to support api gateway to discover services endpoint, api gateway just need to access to service via its registration name.

### Service Design
We use spring-cloud-starter-netflix-eureka-server to let services register itself to Eureka server, and help api gateway access to them without knowing the real URL.

## Authentication Server
An Auth server to support OAuth sercurity as well as provide the security protection for service call.
### Design

I choose Keycloak for this assignment, which is a strong Auth Server, easy to configure, flexible, API support ,... Other replacements can be Okta, Google OAuth2,...

# Whole system design

![System design](docs/fullservice-flow.png)

# How to install

## Prerequisite
- Install Java 11
- Docker 
- A linux execution command line(since my installation script is written in linux).
## Install
### Run Enviroment setup
- At root of project run [install.sh](install.sh) to build and prepare environment (Kafka, Redis, Keycloak, mongodb)
- A folder `build` will be generated at root, run all jar inside it by command `java -jar <name>.jar` 

### Service port
| Service                 | Port |
| ----------------------- | ---- |
| api-gateway             | 9080 |
| product-service         | 9081 |
| product-adapter-service | 9082 |
| audit-service           | 9083 |
| registry-service        | 8761 |
| keycloak                | 7777 |
| mongodb                 | 27017|
| redis                   | 6379 |
| kafka                   | 9092 |

### Testing command
Use command in [test-command.md](test-command.md) to test the system
You can also import postman collection and test via postman [smartchoice.postman_collection.json](smartchoice.postman_collection.json)

# Applied Principles

### YAGNI (You aren't gonna need it)
- Do not implement something util we really need it.
### Inversion of Control
- Use Spring IoC container to inject the dependency
- Dependency is injected via constructor or setter
### DRY (Don't repeat your self)
- Encapsulate business logic, calculated functions, etc. in one place and reuse it
### Single Responsibility Principle
- Divide system to services, each service serves for an only responsibility. Service is organized by layers, each class in layzer has only one reponsibility.
### SOC (Separation of Concerns)
- Use microservices system to separate each service serves for one concern (get product, audit, ....)
- Use AOP to handle cross-cutting concern (audit user when he searchs something)
### KISS (Keep it simple stupid)
- Make the code simple, easy to understand, clean code.
### Low Coupling
- Avoid making complex relationship between classes
- Use DI
# Design Pattern

- Factory Pattern ([ResourceFactory.java](product-adapter-service/src/main/java/com/smarchoice/product/adapter/service/resource/ResourceFactory.java))
- Builder Pattern (using Lombok) ([Product.class](product-service/src/main/java/com/smartchoice/product/service/dto/Product.java))
- Dependency Injection (Spring DI)
- Strategy pattern

# Folder Structure And Library Usage
## Folder Structure
The basic structure of each service devide into these layer :
- Controller : define endpoint , recieve request from client 
- Service : Handle businsess logic
- Repository : communicate with database
- Entity : entity to be persisted to database
- resource : contains rest client resouces to call other services
## Library usage
### Development libraries
- spring-boot-starter-web : to build rest service
- spring-boot-starter-data-redis : to connect to Redis database
- spring-kafka : to interact with kafka
- spring-boot-starter-actuator : to check application health
- spring-cloud-starter-netflix-eureka-client : to register service to Euraka service in registry-service
- spring-cloud-starter-netflix-eureka-server : to create Eureka server in registry-service
- spring-boot-starter-aop : to use aop to audit user
- spring-boot-starter-data-mongodb : to connect to mongodb
- spring-cloud-starter-netflix-zuul : to route request as a proxy server
- spring-boot-starter-oauth2-resource-server : use oauth2 resource server for security
- lombok : to remove the boilerplate code.
### Test libraries
- io.rest-assured:spring-mock-mvc : to write intergration test on endpoint
- it.ozimov:embedded-redis : to use embedded redis
- spring-kafka-test : to test kafka
- Junit5

# Improve to be perfect
This section is my idea that can improve the current system design, since time is limited, I cannot archive all of them, these points should be considered to be implemented in real system.

## Logging system
Loggin roles a important part in microservices design, ELK stack can be applied to make logs centralized, easy to track, anylized and report. In the flow of service calling, when a service call a service, we can append to service id or service uid and send as a header to next service, then if an error occurs, we can know exactly which service is broken.

## Configuration System
Currently, configurations take place in spring application.propertise, it is not reactive and flexible in runtime, suppose that we want to pre-config a confuguration that will be applied in a specific of date (e.g : X-mas discount, 11-11 sale,...) then current design is not fit. A configuration service or a configuration system (eg. Spring configuration server) should be implemented.

## Registry service and loadbalancing replacement

We can use Kubernetes to replace registry service to discover mircroservices, then bottle neck at registry service will be solved. Kubernetes is perfect choice to manage container, deployments or CI/CD integrations.

## Authentication adapter service
We need to make the set up of authentication server automatic and flexible, and well-support for CI/CD pipline, when you want to bootstrap our application. An adapter service to use authencation service API to support configuring security is also a need in microservices system.






