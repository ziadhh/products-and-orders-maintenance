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

Asumption:

The invalid state is done after 30 min after the created status if no action have been done before.

##Links
###swagger
http://localhost:9191/swagger-ui/index.html#/
###h2 console
http://localhost:9191/h2-console/