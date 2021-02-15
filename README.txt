Overview:

This is a Spring project for real time security quote request from multiple client and response quote within service period.

==================================================================================================================

Assumption:

- Security used in this project is traded 7 x 24, hence, the time maturity of a year is 365*24*60*60*1000 milliseconds
- Assume the quote server has to return quote to client immediately
- Assume quote server has to provide quote to client within one minute after receive quote request

==================================================================================================================

Technologies:
1. Spring boot
2. H2
3. Gradle
4. JDK 1.8
5. Apache Common

==================================================================================================================

Setup:

1. Specify the securities to create in application.properties
securityList=123:0.9:0.9,456:0.9:0.9

2. Specify the security and initialize price and duration for generating market data in application.properties
marketDataList=123:110,456:330
marketDataDuration=300000

==================================================================================================================
