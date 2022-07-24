# Study Case

Write an application, which will be using a REST interface to maintain a database of products and orders.

###REST order operations
- creation of the order
- cancellation of the order
- payment of the order
###REST product operations
- creation of the product
- deleting product
- actualisation of the product - actualisation of product quantity in stock, name, ...

### Description
Every **product** must have a name, quantity in stock and price per unit. The **order** can be for 1 or N
existing products and for any unit quantity (e.g. 5 bread, 2 bottles of milk, ...), but it is always
necessary for all the products to be in adequate quantity in stock during the creation of the order.
If this condition is not met, an answer for calling of the endpoint for creation of the order will be 
items, for which there isn’t enough quantity including the missing quantity.
Work with the fact that every order is decreasing the number of available items for following orders,
even in case that the order is not paid yet, for the period of max 30 minutes, then a new order must
be submitted (the current order is invalid). Cancellation of the order releases the reserved goods.


### My Study case

The data model would be as follows 
- products table
- orders table
- status_order table

#### DATABASE ASSUMPTION
- order-product 1-N
- order-orderStatus 1-1

- A product will be delete only if it has not any order related with (independently of the status order)
- The orderId will never be repetead ( I assume that adding an order will creates a new order)
- When the number of items of a product is updated , I only replace old value by new one  (it is not a product items addition)
- if an order is paid it cannot change to another status
- As it is a test I assume that few orders will be created ( so no need to create indexes, partioning tables etc)
- I assumed only h2 database is available , no need to create profiles (either in spring or in maven)

#### order status assumptions
- CREATED : all products have been added to the order
- CREATED_PARTIALLY : only products with availablke quantity has been added: 
- NOT_CREATED : no products have been added (any product has an available quantity)
- PAID : If The order is paid it cannot change to another state ( no refunds or similar )
- CANCELLED : The order has been cancelled manually
- INVALID : The order has been cancelled automaticaly after 30 min


#### Sfw design HAPPY PATH
I tried to simplify the design as a kind of POC to show that all endpoints works, and the application does what is expected 
- As the code is simple, I used only one REST controller that contains order requests and product request ( otherwise I coudld have created two rest controller)
- In order to simplify the code I, used also one un service ( I could have created 2 microservices one for the orders and one for the products, but this would implied create 2 databases)
- No tolerance failures related with microservices has been design as I only created one 

#### Sfw design REAL WORLD
- know the number of request (threshold)
- know the number database capacity 
- monitoring
- more logs
- profiles by environments
- fault tolerance
- perfomance test and e2e test 



####Example 
#### Products
| PRODUCT_ID | NAME | QUANTITY_IN_STOCK | PRICE_PER_UNIT | 
| ------------- | ------------- | ------------- | ------------- | 
| 1  | bread  | 100  | 1 |
| 2  | water |  200 | 0.5 |
| 3  | rice  |  145 | 0.75 |
| 4  | cocacola  | 300  | 1 |

#### Orders
| ORDER_ID | PRODUCT_ID | QUANTITY | TOTAL_PRICE | 
| ------------- | ------------- | ------------- | ------------- | 
| 1  | 1  | 50  | 50 |
| 1  | 2 |  100 | 100 |
| 2  | 3  |  100 | 75 |
| 2  | 4  | 120  | 120 |

#### OrderStatus 

| ORDER_ID | STATUS | AUDIT_DATE |
| ------------- | ------------- |  ------------- |
| 1  | CREATED  | 20-09-2022 10:03
| 2  | PAID  | 20-09-2022 12:03
| 3  | CANCELLED  | 21-09-2022 11:03
| 4  | INVALID  | 20-09-2022 10:33



##Links
###swagger
http://localhost:9191/swagger-ui/index.html#/
###h2 console
http://localhost:9191/h2-console/