Environment: Java 8 or 7

About PriceBasket

The goods that can be purchased, which are all priced in GBP, are: 
Soup – 65p per tin 
Bread – 80p per loaf 
Milk – £1.30 per bottle 
Apples – £1.00 per bag 

Current special offers are: 
Apples have 10% off their normal price this week 
Buy 2 tins of soup and get a loaf of bread for half price 
 
The program should accept a list of items in the basket and output the subtotal, the special offer discounts and the final price. Input should be via the command line in the form PriceBasket item1 item2 item3 … 
For example: PriceBasket Apples Milk Bread 
Output should be to the console, for example: 
Subtotal: £3.10 
Apples 10% off: -10p 
Total: £3.00 

If no special offers are applicable, the code should output: 
Subtotal: £1.30 
(no offers available) 
Total: £1.30

How to add products?
Products can be found in the products.properties in the following format.

#products.items[index].name 		   String 	Must be unique value

#products.items[index].price		   int		100 for GBP 1

#products.items[index].quantity		int

How to add discounts?
Doscounts can be found in the discounts.properties in the following format.

#discounts.offers[index].Offer     Name				Type		Value

#discounts.offers[index].name 					String		Must be unique value

#discounts.offers[index].type 					int			1=Amount, 2=Percentage, 3=Multi buy

#discounts.offers[index].quantity		int			10 for 10 Percent / Cash amount 10	/ 1 Quantity discounted upon multi buy 3 for 2 deal

#discounts.offers[index].item					        String		Offer to be applied on the product

#discounts.offers[index].requiredItem			  String		Required product quantify this offer

#discounts.offers[index].requiredItemMin		int			Minimum quantity of Required product quantify this offer

#discounts.offers[index].applicableCount		int 		How many times can be applied on this cart

#discounts.offers[index].expiryOn				     String 		Offer expired on date DDMMYYYY

How to Run?
You must have Maven and Java installed and configured. Unzip the bjss-price-basket project and build the project by using Maven:

cd bjss-price-basket

mvn clean package -Drun.arguments="Bread,Apples,Soup,Soup"

You can now run the application:

mvn spring-boot:run -Drun.arguments="Bread,Apples,Soup,Soup"

or

cd target

java -jar price-basket-1.0.jar Apples Milk Bread Soup Soup
