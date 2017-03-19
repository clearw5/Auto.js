/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

print("----------------------------------------");

// Use the XML constructor to parse an string into an XML object
var John = "<employee><name>John</name><age>25</age></employee>";
var Sue ="<employee><name>Sue</name><age>32</age></employee>";
var tagName = "employees";
var employees = new XML("<" + tagName +">" + John + Sue + "</" + tagName +">");
print("The employees XML object constructed from a string is:\n" + employees);

print("----------------------------------------");

// Use an XML literal to create an XML object
var order = <order>
   <customer>
      <firstname>John</firstname>
      <lastname>Doe</lastname>
   </customer>
   <item>
      <description>Big Screen Television</description>
      <price>1299.99</price>
      <quantity>1</quantity>
   </item>
</order>

// Construct the full customer name
var name = order.customer.firstname + " " + order.customer.lastname;

// Calculate the total price
var total = order.item.price * order.item.quantity;

print("The order XML object constructed using a literal is:\n" + order);
print("The total price of " + name + "'s order is " + total);

print("----------------------------------------");

// construct a new XML object using expando and super-expando properties
var order = <order/>;
order.customer.name = "Fred Jones";
order.customer.address.street = "123 Long Lang";
order.customer.address.city = "Underwood";
order.customer.address.state = "CA";
order.item[0] = "";
order.item[0].description = "Small Rodents";
order.item[0].quantity = 10;
order.item[0].price = 6.95;

print("The order custructed using expandos and super-expandos is:\n" + order);

// append a new item to the order
order.item += <item><description>Catapult</description><price>139.95</price></item>;

print("----------------------------------------");

print("The order after appending a new item is:\n" + order);

print("----------------------------------------");

// dynamically construct an XML element using embedded expressions
var tagname = "name";
var attributename = "id";
var attributevalue = 5;
var content = "Fred";

var x = <{tagname} {attributename}={attributevalue}>{content}</{tagname}>;

print("The dynamically computed element value is:\n" + x.toXMLString());

print("----------------------------------------");

// Create a SOAP message
var message = <soap:Envelope
      xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
      soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
   <soap:Body>
      <m:GetLastTradePrice xmlns:m="http://mycompany.com/stocks">
         <symbol>DIS</symbol>
      </m:GetLastTradePrice>
   </soap:Body>
</soap:Envelope>

// declare the SOAP and stocks namespaces
var soap = new Namespace("http://schemas.xmlsoap.org/soap/envelope/");
var stock = new Namespace ("http://mycompany.com/stocks");

// extract the soap encoding style and body from the soap message
var encodingStyle = message.@soap::encodingStyle;

print("The encoding style of the soap message is specified by:\n" + encodingStyle);

// change the stock symbol
message.soap::Body.stock::GetLastTradePrice.symbol = "MYCO";

var body = message.soap::Body;

print("The body of the soap message is:\n" + body);

print("----------------------------------------");

// create an manipulate an XML object using the default xml namespace

default xml namespace = "http://default.namespace.com";
var x = <x/>;
x.a = "one";
x.b = "two";
x.c = <c xmlns="http://some.other.namespace.com">three</c>;

print("XML object constructed using the default xml namespace:\n" + x);

default xml namespace="";

print("----------------------------------------");

var order = <order id = "123456" timestamp="Mon Mar 10 2003 16:03:25 GMT-0800 (PST)">
   <customer>
      <firstname>John</firstname>
      <lastname>Doe</lastname>
   </customer>
   <item id="3456">
      <description>Big Screen Television</description>
      <price>1299.99</price>
      <quantity>1</quantity>
   </item>
   <item id = "56789">
      <description>DVD Player</description>
      <price>399.99</price>
      <quantity>1</quantity>
   </item>
</order>;


// get the customer element from the orderprint("The customer is:\n" + order.customer);

// get the id attribute from the order
print("The order id is:" + order.@id);

// get all the child elements from the order element
print("The children of the order are:\n" + order.*); 

// get the list of all item descriptions
print("The order descriptions are:\n" + order.item.description); 


// get second item by numeric index
print("The second item is:\n" + order.item[1]);

// get the list of all child elements in all item elements
print("The children of the items are:\n" + order.item.*);

// get the second child element from the order by index
print("The second child of the order is:\n" + order.*[1]);

// calculate the total price of the order
var totalprice = 0;
for each (i in order.item) {
	totalprice += i.price * i.quantity;
}
print("The total price of the order is: " + totalprice);

print("----------------------------------------");

var e = <employees>
   <employee id="1"><name>Joe</name><age>20</age></employee>
   <employee id="2"><name>Sue</name><age>30</age></employee>
</employees>;

// get all the names in e
print("All the employee names are:\n" + e..name);

// employees with name Joe
print("The employee named Joe is:\n" + e.employee.(name == "Joe"));

// employees with id's 1 & 2
print("Employees with ids 1 & 2:\n" + e.employee.(@id == 1 || @id == 2)); 

// name of employee with id 1
print("Name of the the employee with ID=1: " + e.employee.(@id == 1).name);

print("----------------------------------------");

openConsole();




