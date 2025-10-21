# CHECKOUT SERVICE


## DESCRIPTION:

The Checkout Service is an application that allows users to purchase items from a store using a built-in API. Users can make purchases and access 
available discounts. After completing a payment, the user receives a receipt containing all details about the transaction.


## HOW TO RUN:

The application can be run using any IDE or directly from the command line.

### Running from the command line:

Clone the repository from GitHub:
git clone https://github.com/username/repository-name.git

Build the application:
mvn clean package

Run the application:
java -jar target/application-name-0.0.1-SNAPSHOT.jar


## USEFUL ENDPOINTS:

The service provides all basic endpoints. The complete list can be viewed via Swagger UI at:
http://localhost:8080/swagger-ui/index.html

Access to the built-in H2 database is available at:
http://localhost:8080/h2-console

Before logging in, fill in the fields as follows:

JDBC URL: jdbc:h2:mem
Username: user
Password: password

## EXAMPLE SERVICE FLOW:

POST /api/checkouts – create a new checkout

GET /api/items/names – return the list of available store items

GET /api/bundle-discounts – return the list of available store promotions

PATCH /api/checkouts/1/add-items – add specific products to the cart; discounts are applied based on quantities. Example request body:
[
{
"item_name": "Banana",
"quantity": 6
},
{
"item_name": "Apple",
"quantity": 10
}
]

GET /api/checkouts/1 – return checkout details including applied discounts

PATCH /api/checkouts/1/add-items – add the item “Pear” to obtain a bundle discount. Example request body:
[
{
"item_name": "Pear",
"quantity": 1
}
]

POST /api/checkouts/1/pay – pay for the order and receive a receipt with all purchase details

GET /api/checkouts/1/receipt – view the receipt
