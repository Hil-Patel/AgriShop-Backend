# AgriShop - Backend  

## Overview  
The backend of **AgriShop** is built using **Spring Boot** and **Spring Security** to manage authentication, authorization, and API communication. It provides RESTful APIs for farmers to lease crops, buyers to place bids, and for handling bidding history.  

## Features  

### **Farmer Functionalities**  
- Lease a crop on the platform.  
- View crops that are **open for bidding**.  
- See **canceled crops**.  
- View **completed crop biddings**.  
- Accept a bid to finalize a deal.  
- Track **total revenue generated** from accepted bids.  

### **Buyer Functionalities**  
- View all crops **available for bidding**.  
- Place a bid on any listed crop.  
- Track **bidding history** (all past bids).  
- See **won and lost bids**.  
- View **total amount spent** on bids.  

## Tech Stack  
- **Backend Framework**: Spring Boot  
- **Security**: Spring Security & JWT Authentication  
- **Database**: MySQL / PostgreSQL  
- **ORM**: Hibernate / JPA  
- **API Documentation**: Swagger (if applicable)  

## Installation & Setup  

### **Prerequisites**  
- **Java 17+** (Download from [oracle.com](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html))  
- **Maven** (Download from [maven.apache.org](https://maven.apache.org/download.cgi))  
- **MySQL / PostgreSQL** (Set up your database)  
- **Git** (Download from [git-scm.com](https://git-scm.com/))  

### **Steps to Run**  

1. **Clone the repository**  
   ```bash
   git clone https://github.com/dhyey0/AgriShop-Backend.git
   cd AgriShop-Backend
