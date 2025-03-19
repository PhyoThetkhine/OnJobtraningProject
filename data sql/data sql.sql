-- MySQL dump 10.13  Distrib 8.0.41, for Linux (x86_64)
--
-- Host: localhost    Database: LoanManagement
-- ------------------------------------------------------
-- Server version	8.0.41-0ubuntu0.24.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address` (
  `id` int NOT NULL AUTO_INCREMENT,
  `additional_address` varchar(50) NOT NULL,
  `city` varchar(50) NOT NULL,
  `state` varchar(50) NOT NULL,
  `township` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,'1st street ','Zingyeik','Mon State','Thaton'),(2,'dgegfg','Sintgaing','Mandalay Region','Kyaukse'),(4,'dgegfg','Sintgaing','Mandalay Region','Kyaukse'),(5,'wer gv','Mudon','Mon State','Mawlamyine'),(6,'kjgfsdhgiudsf gudfgius 11','Thingangyun','Yangon Region','East Yangon'),(7,'4tgtdeg','Myinmu','Sagaing Region','Sagaing'),(8,'tuerytyi','Hakha','Chin State','Hakha'),(9,'erwytuieuhwt','Zalun','Ayeyarwady Region','Hinthada'),(10,'eruierwtieurytiue','Zalun','Ayeyarwady Region','Hinthada'),(11,'fjhdskdhgs','Hpruso','Kayah State','Loikaw'),(12,'ofhigewgwg','Yaeni','Bago Region','Taungoo'),(13,'ifhiurhti','Lewe','Naypyidaw Union Territory','Dekkhina(South Naypyidaw)'),(14,'khhbhbuy','Mahlaing','Mandalay Region','Meiktila');
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `branch`
--

DROP TABLE IF EXISTS `branch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `branch` (
  `id` int NOT NULL AUTO_INCREMENT,
  `branch_code` varchar(50) NOT NULL,
  `branch_name` varchar(50) NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `status` int NOT NULL,
  `updated_date` datetime(6) NOT NULL,
  `address_id` int NOT NULL,
  `created_user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr5n331c13dyb3kbq1jlo53mh5` (`address_id`),
  KEY `FKmdhlmhhyd07xjrxq1uyq3yuhd` (`created_user_id`),
  CONSTRAINT `FKmdhlmhhyd07xjrxq1uyq3yuhd` FOREIGN KEY (`created_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKr5n331c13dyb3kbq1jlo53mh5` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `branch`
--

LOCK TABLES `branch` WRITE;
/*!40000 ALTER TABLE `branch` DISABLE KEYS */;
INSERT INTO `branch` VALUES (2,'001','Main Branch','2023-03-25 21:01:03.817313',13,'2023-03-25 21:01:03.817313',1,2),(3,'002','Yangon','2023-03-25 21:01:03.817313',13,'2023-03-25 21:01:03.817313',1,2),(4,'003','Mandalay Branch','2025-03-19 14:05:13.777448',13,'2025-03-19 14:50:37.635224',14,3);
/*!40000 ALTER TABLE `branch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `branch_current_account`
--

DROP TABLE IF EXISTS `branch_current_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `branch_current_account` (
  `id` int NOT NULL AUTO_INCREMENT,
  `acc_code` varchar(50) NOT NULL,
  `balance` decimal(32,2) NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `updated_date` datetime(6) NOT NULL,
  `branch_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKhx9t672duewb6e17lcimqkys` (`acc_code`),
  UNIQUE KEY `UKgeatw7egs7nhaouxp801vx0xa` (`branch_id`),
  CONSTRAINT `FK76v2swkx57qqxa7s5yywdw67r` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `branch_current_account`
--

LOCK TABLES `branch_current_account` WRITE;
/*!40000 ALTER TABLE `branch_current_account` DISABLE KEYS */;
INSERT INTO `branch_current_account` VALUES (1,'BAC003',100000.00,'2025-03-19 14:05:13.789966','2025-03-19 15:34:44.043078',4),(2,'BAC002',1999000.00,'2025-03-19 14:05:13.789966','2025-03-19 15:39:58.912417',3),(4,'BAC001',0.00,'2025-03-19 14:05:13.789966','2025-03-19 14:05:13.789966',2);
/*!40000 ALTER TABLE `branch_current_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `business_photo`
--

DROP TABLE IF EXISTS `business_photo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `business_photo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `photo_url` varchar(200) NOT NULL,
  `company_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKnfisfir6sjiglancqkamiyqm0` (`photo_url`),
  KEY `FKto6sc8lkkhl7plx88mf2b85ld` (`company_id`),
  CONSTRAINT `FKto6sc8lkkhl7plx88mf2b85ld` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `business_photo`
--

LOCK TABLES `business_photo` WRITE;
/*!40000 ALTER TABLE `business_photo` DISABLE KEYS */;
INSERT INTO `business_photo` VALUES (1,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368019/sf9usbwhxywms5kpl3or.jpg',1),(2,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368019/wdpoeuoo9fvqa1ufjgay.jpg',1),(3,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369121/jh6psagbyi0guar15qel.jpg',2),(4,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369121/suelmdmykperrddkvezg.jpg',2),(5,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369358/t59bagy4pcdehyonzryu.jpg',3),(6,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369358/nchcoryumhtpctwtk0a8.jpg',3);
/*!40000 ALTER TABLE `business_photo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cash_in_out_transaction`
--

DROP TABLE IF EXISTS `cash_in_out_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cash_in_out_transaction` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` enum('Cash_In','Cash_Out') NOT NULL,
  `amount` decimal(32,2) NOT NULL,
  `description` varchar(200) NOT NULL,
  `transaction_date` datetime(6) NOT NULL,
  `branch_current_account_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK98j7gs542i7hxsbvhvx9cre32` (`branch_current_account_id`),
  CONSTRAINT `FK98j7gs542i7hxsbvhvx9cre32` FOREIGN KEY (`branch_current_account_id`) REFERENCES `branch_current_account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cash_in_out_transaction`
--

LOCK TABLES `cash_in_out_transaction` WRITE;
/*!40000 ALTER TABLE `cash_in_out_transaction` DISABLE KEYS */;
INSERT INTO `cash_in_out_transaction` VALUES (1,'Cash_In',100000.00,'first time transaction','2025-03-19 15:34:44.112480',1),(3,'Cash_In',1000000.00,'first time transaction','2025-03-19 15:37:37.383382',2),(4,'Cash_Out',1000.00,'YO YO','2025-03-19 15:39:58.929270',2);
/*!40000 ALTER TABLE `cash_in_out_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cif_current_account`
--

DROP TABLE IF EXISTS `cif_current_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cif_current_account` (
  `id` int NOT NULL AUTO_INCREMENT,
  `acc_code` varchar(50) NOT NULL,
  `balance` decimal(32,2) NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `is_freeze` int NOT NULL,
  `updated_date` datetime(6) NOT NULL,
  `hold_amount` decimal(32,2) DEFAULT NULL,
  `max_amount` decimal(32,2) NOT NULL,
  `min_amount` decimal(32,2) NOT NULL,
  `cif_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK4h9drmm4en3lx1cdsl8vkrh8a` (`acc_code`),
  UNIQUE KEY `UKhs1joo28ssb0fq5lpi525aam4` (`cif_id`),
  CONSTRAINT `FK91fmth2lf2qoublir8rhxy6u2` FOREIGN KEY (`cif_id`) REFERENCES `customer_information_file` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cif_current_account`
--

LOCK TABLES `cif_current_account` WRITE;
/*!40000 ALTER TABLE `cif_current_account` DISABLE KEYS */;
INSERT INTO `cif_current_account` VALUES (1,'CAC0020000200001',0.00,'2025-03-19 13:35:39.237832',2,'2025-03-19 13:35:39.237857',0.00,1000.00,1000.00,1),(2,'CAC0020000200002',0.00,'2025-03-19 13:37:01.793491',2,'2025-03-19 13:37:01.793507',0.00,1000.00,1000.00,2),(3,'CAC0020000200003',0.00,'2025-03-19 13:42:29.411770',2,'2025-03-19 13:42:29.411785',0.00,6000000000000000000.00,100000000000.00,3),(4,'CAC0020000200004',0.00,'2025-03-19 13:55:22.029558',2,'2025-03-19 13:55:22.029568',0.00,645345340.00,4954370.00,4),(5,'CAC0020000200005',0.00,'2025-03-19 13:59:19.228979',2,'2025-03-19 13:59:19.228993',0.00,53746823648.00,456843650.00,5),(6,'CAC0020000200006',0.00,'2025-03-19 14:00:56.171330',2,'2025-03-19 14:00:56.171341',0.00,89000000.00,4587884.00,6),(7,'CAC0020000200007',0.00,'2025-03-19 14:02:28.124967',2,'2025-03-19 14:02:28.124984',0.00,310000.00,30000.00,7),(8,'CAC0020000200008',0.00,'2025-03-19 14:03:59.302259',2,'2025-03-19 14:03:59.302270',0.00,7777777.00,563777.00,8);
/*!40000 ALTER TABLE `cif_current_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `collateral`
--

DROP TABLE IF EXISTS `collateral`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `collateral` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) NOT NULL,
  `document_url` varchar(200) NOT NULL,
  `estimated_value` decimal(32,2) NOT NULL,
  `property_type` varchar(100) NOT NULL,
  `updated_date` datetime(6) NOT NULL,
  `cif_id` int NOT NULL,
  `created_user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4kigj8eeffxclo2gfbd06tmdq` (`cif_id`),
  KEY `FK2l75rtnts3bprl3qf6m6ppchw` (`created_user_id`),
  CONSTRAINT `FK2l75rtnts3bprl3qf6m6ppchw` FOREIGN KEY (`created_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK4kigj8eeffxclo2gfbd06tmdq` FOREIGN KEY (`cif_id`) REFERENCES `customer_information_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collateral`
--

LOCK TABLES `collateral` WRITE;
/*!40000 ALTER TABLE `collateral` DISABLE KEYS */;
/*!40000 ALTER TABLE `collateral` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `company` (
  `id` int NOT NULL AUTO_INCREMENT,
  `business_type` varchar(100) NOT NULL,
  `category` varchar(100) NOT NULL,
  `company_type` varchar(100) NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `license_expiry_date` date NOT NULL,
  `license_issue_date` date NOT NULL,
  `license_number` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `registration_date` datetime(6) NOT NULL,
  `updated_date` datetime(6) NOT NULL,
  `address_id` int NOT NULL,
  `cif_id` int NOT NULL,
  `created_user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgfifm4874ce6mecwj54wdb3ma` (`address_id`),
  KEY `FKo1egvhd5hcmp5qar1kieq5to2` (`cif_id`),
  KEY `FKl9nk4mfd75nlx406dlf0fs94r` (`created_user_id`),
  CONSTRAINT `FKgfifm4874ce6mecwj54wdb3ma` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `FKl9nk4mfd75nlx406dlf0fs94r` FOREIGN KEY (`created_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKo1egvhd5hcmp5qar1kieq5to2` FOREIGN KEY (`cif_id`) REFERENCES `customer_information_file` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` VALUES (1,'IT','IT','IT','2025-03-19 13:37:01.817337','2025-03-19','2025-03-19','892435987','ACE Data system','0977371737','2025-03-19 06:30:00.000000','2025-03-19 13:37:01.817352',5,2,3),(2,'IT','IT','IT','2025-03-19 13:55:22.046228','2025-05-31','2025-03-19','4875368752','DAT','09485435369','2025-03-19 06:30:00.000000','2025-03-19 13:55:22.046236',8,4,3),(3,'IT','IT','iT','2025-03-19 13:59:19.247601','2025-04-20','2025-03-19','47654','Ui','09473436464','2025-03-19 06:30:00.000000','2025-03-19 13:59:19.247611',10,5,3);
/*!40000 ALTER TABLE `company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_information_file`
--

DROP TABLE IF EXISTS `customer_information_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_information_file` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nrc` varchar(20) NOT NULL,
  `nrc_back_photo` varchar(200) NOT NULL,
  `nrc_front_photo` varchar(220) NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `date_of_birth` date NOT NULL,
  `email` varchar(200) NOT NULL,
  `gender` enum('FEMALE','MALE') NOT NULL,
  `name` varchar(100) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `photo_url` varchar(220) NOT NULL,
  `updated_date` datetime(6) NOT NULL,
  `cif_code` varchar(50) NOT NULL,
  `cif_type` enum('DEVELOPED_COMPANY','Dealer','PERSONAL','SETUP_COMPANY') NOT NULL,
  `status` int NOT NULL,
  `address_id` int NOT NULL,
  `created_user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKi7v48i2tg5yibhrhqfcokrboq` (`cif_code`),
  KEY `FKr5fh20qjdodicvtwa0lpyp4mv` (`address_id`),
  KEY `FKkp89ykm766dbvdw8ilytla5ge` (`created_user_id`),
  CONSTRAINT `FKkp89ykm766dbvdw8ilytla5ge` FOREIGN KEY (`created_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKr5fh20qjdodicvtwa0lpyp4mv` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_information_file`
--

LOCK TABLES `customer_information_file` WRITE;
/*!40000 ALTER TABLE `customer_information_file` DISABLE KEYS */;
INSERT INTO `customer_information_file` VALUES (1,'10/PaMaNa(N)231132','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367936/czddox3ya0imawcjrqoa.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367936/csyaaqdkkzujinu7il9u.jpg','2025-03-19 13:35:39.217819','2025-03-19','phyothetkhine14@gmail.com','MALE','Ye Oo','09791858246','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367936/f8ibzevyejyuumcytfhn.jpg','2025-03-19 13:35:39.217843','0020000200001','SETUP_COMPANY',13,2,3),(2,'10/PaMaNa(N)231132','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368019/wclyt6t5nkleak3kgv1y.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368019/d7vgcohdpngjsitjr7ip.jpg','2025-03-19 13:37:01.782216','2025-03-19','phyothetkhine14@gmail.com','MALE','Ye Oo','09791858246','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368019/tz8thlsccyo6iycy2z2n.jpg','2025-03-19 13:37:01.782232','0020000200002','SETUP_COMPANY',13,4,3),(3,'12/ThaLaNa(N)195037','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368348/ecgzpbtjyyuvfordzwnt.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368348/blji7p8zafvgkr3qglng.jpg','2025-03-19 13:42:29.400536','2025-03-19','san23@gmail.com','FEMALE','San San','0969469732','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368348/amdao2z8bcneobozo3t6.jpg','2025-03-19 13:42:29.400548','0020000200003','PERSONAL',13,6,3),(4,'12/KaMaNa(N)344363','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369121/b5dgdr0klwiyzj3lor9d.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369121/xrfhaziuyi0auqbc8szu.jpg','2025-03-19 13:55:22.020733','2025-03-19','phyokyaw@gmail.com','MALE','Phyo Kyaw','09787565654','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369121/imn3zyn30mnz8ijmgg8u.jpg','2025-03-19 13:55:22.020758','0020000200004','DEVELOPED_COMPANY',13,7,3),(5,'14/ZaLaNa(N)253233','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369358/ycx6xakka5ueulputekf.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369358/ghp32yq4x4yhomidvtwf.jpg','2025-03-19 13:59:19.214429','2025-03-19','min34@gmial.com','MALE','Min MIn','0945742549','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369358/m99qsbuplmwu92avvxev.jpg','2025-03-19 13:59:19.214454','0020000200005','DEVELOPED_COMPANY',13,9,3),(6,'1/AhGaYa(N)678999','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369455/g8qlbzftmw8fheqqixdq.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369455/rdpx9x9eobmfdtgbv8jc.jpg','2025-03-19 14:00:56.161602','2025-03-07','moe35@gmail.com','FEMALE','Moe','09664424086','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369455/jod4xs1u32z5ug3es54n.jpg','2025-03-19 14:00:56.161616','0020000200006','PERSONAL',13,11,3),(7,'6/MaMaNa(N)890333','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369547/r5oajmgmh32vvnfi962w.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369546/nucq8temrvdy3sj9gnqt.jpg','2025-03-19 14:02:28.113068','2025-01-11','kyaw34@gmil.com','MALE','Kyaw','094455667770','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369546/t3mwzyifqxjb83gqxvxx.jpg','2025-03-19 14:02:28.113084','0020000200007','PERSONAL',13,12,3),(8,'2/DaMaSa(N)213888','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369637/ohl4cgukzlyyqasbkbtv.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369637/td61gwvurr4ocpsq2p3j.jpg','2025-03-19 14:03:59.292538','2025-03-19','Khnt89@gmil.com','FEMALE','Khant','09338890631','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369638/dkz1qy2z1yfiom3dtpmo.jpg','2025-03-19 14:03:59.292548','0020000200008','PERSONAL',13,13,3);
/*!40000 ALTER TABLE `customer_information_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dealer_product`
--

DROP TABLE IF EXISTS `dealer_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dealer_product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) NOT NULL,
  `description` mediumtext NOT NULL,
  `name` varchar(100) NOT NULL,
  `price` decimal(32,2) NOT NULL,
  `updated_date` datetime(6) NOT NULL,
  `company_id` int NOT NULL,
  `created_user_id` int NOT NULL,
  `sub_category_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrh3573jpjuweqhqu7bl78uus4` (`company_id`),
  KEY `FKemqm5vtpf7x6qmbgqx46rx6xj` (`created_user_id`),
  KEY `FK4d9hyynbbvalfwr3wnopvuae5` (`sub_category_id`),
  CONSTRAINT `FK4d9hyynbbvalfwr3wnopvuae5` FOREIGN KEY (`sub_category_id`) REFERENCES `sub_category` (`id`),
  CONSTRAINT `FKemqm5vtpf7x6qmbgqx46rx6xj` FOREIGN KEY (`created_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKrh3573jpjuweqhqu7bl78uus4` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dealer_product`
--

LOCK TABLES `dealer_product` WRITE;
/*!40000 ALTER TABLE `dealer_product` DISABLE KEYS */;
/*!40000 ALTER TABLE `dealer_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `financial`
--

DROP TABLE IF EXISTS `financial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `financial` (
  `id` int NOT NULL AUTO_INCREMENT,
  `average_employees` varchar(50) NOT NULL,
  `average_expenses` varchar(50) NOT NULL,
  `average_income` varchar(50) NOT NULL,
  `average_investment` varchar(50) NOT NULL,
  `average_salary_paid` varchar(50) NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `expected_income` varchar(50) NOT NULL,
  `revenue_proof` varchar(50) NOT NULL,
  `updated_date` datetime(6) NOT NULL,
  `company_id` int NOT NULL,
  `created_user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKpxe4ix0ikp3yfvuajqa1s91jw` (`company_id`),
  KEY `FK4s0v6ag6d5wicrrfj7dcft17d` (`created_user_id`),
  CONSTRAINT `FK4s0v6ag6d5wicrrfj7dcft17d` FOREIGN KEY (`created_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKhgscfl9opu69qcjavx39gkw3b` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `financial`
--

LOCK TABLES `financial` WRITE;
/*!40000 ALTER TABLE `financial` DISABLE KEYS */;
INSERT INTO `financial` VALUES (1,'254235','242524.0','612822.0','2352.0','2352435.0','2025-03-19 13:37:01.857865','24353.0','eegc c htet','2025-03-19 13:37:01.857883',1,3),(2,'243533','3453578.0','342532.0','352785.0','323643.0','2025-03-19 13:55:22.077731','352543.0','gyewy kfghigh','2025-03-19 13:55:22.077757',2,3),(3,'675775','345576.0','43453.0','435454.0','565856.0','2025-03-19 13:59:19.293683','345657.0','hgdfughiud','2025-03-19 13:59:19.293698',3,3);
/*!40000 ALTER TABLE `financial` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hp_loan`
--

DROP TABLE IF EXISTS `hp_loan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hp_loan` (
  `id` int NOT NULL AUTO_INCREMENT,
  `disbursement_amount` decimal(32,2) NOT NULL,
  `application_date` datetime(6) NOT NULL,
  `confirm_date` datetime(6) DEFAULT NULL,
  `defaulted_rate` int NOT NULL,
  `document_fee` decimal(32,2) NOT NULL,
  `document_fee_rate` int NOT NULL,
  `duration` int NOT NULL,
  `end_date` date DEFAULT NULL,
  `grace_period` int NOT NULL,
  `interest_rate` int NOT NULL,
  `late_fee_rate` int NOT NULL,
  `loan_amount` decimal(32,2) NOT NULL,
  `long_term_overdue_rate` int NOT NULL,
  `service_fee` decimal(32,2) NOT NULL,
  `service_fee_rate` int NOT NULL,
  `start_date` date DEFAULT NULL,
  `status` int NOT NULL,
  `updated_date` datetime(6) NOT NULL,
  `down_payment` decimal(32,2) NOT NULL,
  `hp_loan_code` varchar(50) NOT NULL,
  `cif_id` int NOT NULL,
  `confirm_user_id` int DEFAULT NULL,
  `created_user_id` int NOT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKafou7j25sbptw5d8f4rfav6f4` (`hp_loan_code`),
  KEY `FKm8figi7ml3udtnhxi6k6rc0b` (`cif_id`),
  KEY `FKkpqd79fkkxde9773fj5nueylg` (`confirm_user_id`),
  KEY `FKtpekbl7up3d00cjvltik9jvgo` (`created_user_id`),
  KEY `FKks8h5w5hocybts33mrvq19kko` (`product_id`),
  CONSTRAINT `FKkpqd79fkkxde9773fj5nueylg` FOREIGN KEY (`confirm_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKks8h5w5hocybts33mrvq19kko` FOREIGN KEY (`product_id`) REFERENCES `dealer_product` (`id`),
  CONSTRAINT `FKm8figi7ml3udtnhxi6k6rc0b` FOREIGN KEY (`cif_id`) REFERENCES `customer_information_file` (`id`),
  CONSTRAINT `FKtpekbl7up3d00cjvltik9jvgo` FOREIGN KEY (`created_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hp_loan`
--

LOCK TABLES `hp_loan` WRITE;
/*!40000 ALTER TABLE `hp_loan` DISABLE KEYS */;
/*!40000 ALTER TABLE `hp_loan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hp_loan_history`
--

DROP TABLE IF EXISTS `hp_loan_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hp_loan_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `interest_late_fee_paid` decimal(32,2) NOT NULL,
  `interest_paid` decimal(32,2) NOT NULL,
  `iod_paid` decimal(32,2) NOT NULL,
  `outstanding_amount` decimal(32,2) NOT NULL,
  `paid_amount` decimal(32,2) NOT NULL,
  `paid_date` datetime(6) NOT NULL,
  `principal_late_fee_paid` decimal(32,2) NOT NULL,
  `principal_paid` decimal(32,2) NOT NULL,
  `term_status` int NOT NULL,
  `total_paid` decimal(32,2) NOT NULL,
  `hp_term_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKo9pe5t8xg5er65rv4tkvhjuxn` (`hp_term_id`),
  CONSTRAINT `FKo9pe5t8xg5er65rv4tkvhjuxn` FOREIGN KEY (`hp_term_id`) REFERENCES `hp_term` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hp_loan_history`
--

LOCK TABLES `hp_loan_history` WRITE;
/*!40000 ALTER TABLE `hp_loan_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `hp_loan_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hp_term`
--

DROP TABLE IF EXISTS `hp_term`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hp_term` (
  `id` int NOT NULL AUTO_INCREMENT,
  `days` int NOT NULL,
  `due_date` date NOT NULL,
  `interest` decimal(32,2) NOT NULL,
  `late_interest_days` int NOT NULL,
  `late_interest_fee` decimal(32,2) NOT NULL,
  `interest_of_overdue` decimal(32,2) NOT NULL,
  `last_repay_date` date DEFAULT NULL,
  `last_repayment_amount` decimal(32,2) NOT NULL,
  `outstanding_amount` decimal(32,2) NOT NULL,
  `principal` decimal(32,2) NOT NULL,
  `status` int DEFAULT NULL,
  `total_repayment_amount` decimal(32,2) NOT NULL,
  `late_principal_days` int NOT NULL,
  `late_principal_fee` decimal(32,2) NOT NULL,
  `principal_of_overdue` decimal(32,2) NOT NULL,
  `hp_loan_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKufyajrcjwo32hkyvmuikqtjj` (`hp_loan_id`),
  CONSTRAINT `FKufyajrcjwo32hkyvmuikqtjj` FOREIGN KEY (`hp_loan_id`) REFERENCES `hp_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hp_term`
--

LOCK TABLES `hp_term` WRITE;
/*!40000 ALTER TABLE `hp_term` DISABLE KEYS */;
/*!40000 ALTER TABLE `hp_term` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_conllateral`
--

DROP TABLE IF EXISTS `loan_conllateral`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `loan_conllateral` (
  `collateral_id` int NOT NULL,
  `loan_id` int NOT NULL,
  PRIMARY KEY (`collateral_id`,`loan_id`),
  KEY `FK285j4y0osoctx9dchqkn5155s` (`loan_id`),
  CONSTRAINT `FK285j4y0osoctx9dchqkn5155s` FOREIGN KEY (`loan_id`) REFERENCES `sme_loan` (`id`),
  CONSTRAINT `FKd9y4t7lf5i704wnvaemrywqfi` FOREIGN KEY (`collateral_id`) REFERENCES `collateral` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loan_conllateral`
--

LOCK TABLES `loan_conllateral` WRITE;
/*!40000 ALTER TABLE `loan_conllateral` DISABLE KEYS */;
/*!40000 ALTER TABLE `loan_conllateral` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `main_category`
--

DROP TABLE IF EXISTS `main_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `main_category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKinxg5ynqbykb7l5q6skky4o15` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `main_category`
--

LOCK TABLES `main_category` WRITE;
/*!40000 ALTER TABLE `main_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `main_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_method`
--

DROP TABLE IF EXISTS `payment_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_method` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) NOT NULL,
  `payment_type` varchar(30) NOT NULL,
  `status` int NOT NULL,
  `updated_date` datetime(6) NOT NULL,
  `created_user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn2qjap71fbtp0q096y6bg83rn` (`created_user_id`),
  CONSTRAINT `FKn2qjap71fbtp0q096y6bg83rn` FOREIGN KEY (`created_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_method`
--

LOCK TABLES `payment_method` WRITE;
/*!40000 ALTER TABLE `payment_method` DISABLE KEYS */;
/*!40000 ALTER TABLE `payment_method` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK2ojme20jpga3r4r79tdso17gi` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` VALUES (10,'AccessControl'),(1,'Accounting'),(4,'Branch'),(5,'Clients'),(2,'Dealer'),(8,'HP'),(6,'Loans'),(3,'Payment'),(7,'SME'),(9,'Users');
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `repayment`
--

DROP TABLE IF EXISTS `repayment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `repayment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `repayment_type` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKthkuocowocc09oltf36k1lnpt` (`repayment_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `repayment`
--

LOCK TABLES `repayment` WRITE;
/*!40000 ALTER TABLE `repayment` DISABLE KEYS */;
/*!40000 ALTER TABLE `repayment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `authority` enum('MainBranchLevel','RegularBranchLevel') NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `role_name` varchar(255) NOT NULL,
  `updated_date` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKiubw515ff0ugtm28p8g3myt0h` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'MainBranchLevel','2023-03-25 21:01:03.817313','Super Admin','2023-03-25 21:01:03.817313'),(2,'MainBranchLevel','2025-03-19 10:52:31.658844','Branch Manager','2025-03-19 10:52:31.658902');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permission`
--

DROP TABLE IF EXISTS `role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permission` (
  `permission_id` int NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`permission_id`,`role_id`),
  KEY `FKa6jx8n8xkesmjmv6jqug6bg68` (`role_id`),
  CONSTRAINT `FKa6jx8n8xkesmjmv6jqug6bg68` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `FKf8yllw1ecvwqy3ehyxawqa1qp` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permission`
--

LOCK TABLES `role_permission` WRITE;
/*!40000 ALTER TABLE `role_permission` DISABLE KEYS */;
INSERT INTO `role_permission` VALUES (1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,1),(8,1),(9,1),(10,1),(1,2),(2,2),(3,2),(4,2),(5,2),(6,2),(7,2),(8,2),(9,2),(10,2);
/*!40000 ALTER TABLE `role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sme_loan`
--

DROP TABLE IF EXISTS `sme_loan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sme_loan` (
  `id` int NOT NULL AUTO_INCREMENT,
  `disbursement_amount` decimal(32,2) NOT NULL,
  `application_date` datetime(6) NOT NULL,
  `confirm_date` datetime(6) DEFAULT NULL,
  `defaulted_rate` int NOT NULL,
  `document_fee` decimal(32,2) NOT NULL,
  `document_fee_rate` int NOT NULL,
  `duration` int NOT NULL,
  `end_date` date DEFAULT NULL,
  `grace_period` int NOT NULL,
  `interest_rate` int NOT NULL,
  `late_fee_rate` int NOT NULL,
  `loan_amount` decimal(32,2) NOT NULL,
  `long_term_overdue_rate` int NOT NULL,
  `service_fee` decimal(32,2) NOT NULL,
  `service_fee_rate` int NOT NULL,
  `start_date` date DEFAULT NULL,
  `status` int NOT NULL,
  `updated_date` datetime(6) NOT NULL,
  `frequency` enum('MONTHLY','YEARLY') NOT NULL,
  `paid_principal_status` int NOT NULL,
  `purpose` mediumtext NOT NULL,
  `loan_code` varchar(50) NOT NULL,
  `cif_id` int NOT NULL,
  `confirm_user_id` int DEFAULT NULL,
  `created_user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKnckx6w7sv4vcbg9uq93peu6kg` (`loan_code`),
  KEY `FK42ktp4mirnlvve8elirdsgxod` (`cif_id`),
  KEY `FKk7wwyyxomi0yocm7c5wcpdnfo` (`confirm_user_id`),
  KEY `FK6qqpvarh9othrj58gm0ebyk8y` (`created_user_id`),
  CONSTRAINT `FK42ktp4mirnlvve8elirdsgxod` FOREIGN KEY (`cif_id`) REFERENCES `customer_information_file` (`id`),
  CONSTRAINT `FK6qqpvarh9othrj58gm0ebyk8y` FOREIGN KEY (`created_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKk7wwyyxomi0yocm7c5wcpdnfo` FOREIGN KEY (`confirm_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sme_loan`
--

LOCK TABLES `sme_loan` WRITE;
/*!40000 ALTER TABLE `sme_loan` DISABLE KEYS */;
/*!40000 ALTER TABLE `sme_loan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sme_loan_history`
--

DROP TABLE IF EXISTS `sme_loan_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sme_loan_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `interest_late_fee_paid` decimal(32,2) NOT NULL,
  `interest_paid` decimal(32,2) NOT NULL,
  `iod_paid` decimal(32,2) NOT NULL,
  `outstanding_amount` decimal(32,2) NOT NULL,
  `paid_amount` decimal(32,2) NOT NULL,
  `paid_date` datetime(6) NOT NULL,
  `principal_late_fee_paid` decimal(32,2) NOT NULL,
  `principal_paid` decimal(32,2) NOT NULL,
  `term_status` int NOT NULL,
  `total_paid` decimal(32,2) NOT NULL,
  `sme_term_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK45xr4ew6c4g8sb03juaumdh2c` (`sme_term_id`),
  CONSTRAINT `FK45xr4ew6c4g8sb03juaumdh2c` FOREIGN KEY (`sme_term_id`) REFERENCES `sme_term` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sme_loan_history`
--

LOCK TABLES `sme_loan_history` WRITE;
/*!40000 ALTER TABLE `sme_loan_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `sme_loan_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sme_term`
--

DROP TABLE IF EXISTS `sme_term`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sme_term` (
  `id` int NOT NULL AUTO_INCREMENT,
  `days` int NOT NULL,
  `due_date` date NOT NULL,
  `interest` decimal(32,2) NOT NULL,
  `late_interest_days` int NOT NULL,
  `late_interest_fee` decimal(32,2) NOT NULL,
  `interest_of_overdue` decimal(32,2) NOT NULL,
  `last_repay_date` date DEFAULT NULL,
  `last_repayment_amount` decimal(32,2) NOT NULL,
  `outstanding_amount` decimal(32,2) NOT NULL,
  `principal` decimal(32,2) NOT NULL,
  `status` int DEFAULT NULL,
  `total_repayment_amount` decimal(32,2) NOT NULL,
  `loan_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpuhvdwm403idokl7rkowu8xae` (`loan_id`),
  CONSTRAINT `FKpuhvdwm403idokl7rkowu8xae` FOREIGN KEY (`loan_id`) REFERENCES `sme_loan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sme_term`
--

LOCK TABLES `sme_term` WRITE;
/*!40000 ALTER TABLE `sme_term` DISABLE KEYS */;
/*!40000 ALTER TABLE `sme_term` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sub_category`
--

DROP TABLE IF EXISTS `sub_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sub_category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category` varchar(50) NOT NULL,
  `main_category_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkq9psg2c3clgkh343avvkxir2` (`category`),
  KEY `FK753xnmtixdl7hf7c7h3hsjuam` (`main_category_id`),
  CONSTRAINT `FK753xnmtixdl7hf7c7h3hsjuam` FOREIGN KEY (`main_category_id`) REFERENCES `main_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sub_category`
--

LOCK TABLES `sub_category` WRITE;
/*!40000 ALTER TABLE `sub_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `sub_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `id` int NOT NULL AUTO_INCREMENT,
  `amount` decimal(32,2) NOT NULL,
  `from_account_id` int NOT NULL,
  `from_account_type` enum('BRANCH','CIF','USER') NOT NULL,
  `to_account_id` int NOT NULL,
  `to_account_type` enum('BRANCH','CIF','USER') NOT NULL,
  `transaction_date` datetime(6) NOT NULL,
  `payment_method_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3iabppaif3d2a8jjotnly31j2` (`payment_method_id`),
  CONSTRAINT `FK3iabppaif3d2a8jjotnly31j2` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_method` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nrc` varchar(20) NOT NULL,
  `nrc_back_photo` varchar(200) NOT NULL,
  `nrc_front_photo` varchar(220) NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `date_of_birth` date NOT NULL,
  `email` varchar(200) NOT NULL,
  `gender` enum('FEMALE','MALE') NOT NULL,
  `name` varchar(100) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `photo_url` varchar(220) NOT NULL,
  `updated_date` datetime(6) NOT NULL,
  `password` varchar(200) NOT NULL,
  `status` int NOT NULL,
  `user_code` varchar(50) NOT NULL,
  `address_id` int NOT NULL,
  `created_user_id` int NOT NULL,
  `branch_id` int NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKlk8ogj8cf8jrqen0wof8iyora` (`user_code`),
  KEY `FKddefmvbrws3hvl5t0hnnsv8ox` (`address_id`),
  KEY `FK9yy0ya980j002yvtxi9r7kv6b` (`branch_id`),
  KEY `FKn82ha3ccdebhokx3a8fgdqeyy` (`role_id`),
  KEY `FKhnncm3gx8plxt0kxg73vb9xdl` (`created_user_id`),
  CONSTRAINT `FK9yy0ya980j002yvtxi9r7kv6b` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`id`),
  CONSTRAINT `FKddefmvbrws3hvl5t0hnnsv8ox` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `FKhnncm3gx8plxt0kxg73vb9xdl` FOREIGN KEY (`created_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKn82ha3ccdebhokx3a8fgdqeyy` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (2,'10/PaMaNa(N)276146','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367281/f1evb7jagfgzpstzruyo.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367280/gvvlmw7ejcbrbmi8begm.jpg','2023-03-25 21:01:03.817313','2023-03-25','phyothetkhine@gmail.com','MALE','Phyo','09876765765','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367280/ivs05ke7qx1wosedwppr.jpg','2025-03-19 13:30:44.751475','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00100001',1,2,2,2),(3,'12/KaMaTa(N)123434','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367579/qfl20i7msi1hjuk6s3h9.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367578/dl48kszxsuko0x9wxgt1.jpg','2023-03-25 21:01:03.817313','2023-03-25','phyothetkhineads@gmail.com','MALE','Thet','09876765765','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367577/zsfrh8hkmuhgrsydlk2f.jpg','2025-03-19 13:32:27.327474','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00200002',1,2,3,2);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_current_account`
--

DROP TABLE IF EXISTS `user_current_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_current_account` (
  `id` int NOT NULL AUTO_INCREMENT,
  `acc_code` varchar(50) NOT NULL,
  `balance` decimal(32,2) NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `is_freeze` int NOT NULL,
  `updated_date` datetime(6) NOT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK188d197nmigetihco6nwloue1` (`acc_code`),
  UNIQUE KEY `UKo33krstdpjc8bxkg43sk4uof8` (`user_id`),
  CONSTRAINT `FK4dy97hu7bkiday0040l5l1rc8` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_current_account`
--

LOCK TABLES `user_current_account` WRITE;
/*!40000 ALTER TABLE `user_current_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_current_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_permission`
--

DROP TABLE IF EXISTS `user_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_permission` (
  `allowed_date` datetime(6) NOT NULL,
  `is_allowed` int NOT NULL,
  `limited_date` datetime(6) NOT NULL,
  `allowed_user_id` int NOT NULL,
  `permission_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`permission_id`,`user_id`),
  KEY `FKavy6q6uo0is8pkfiw8y5s4wsc` (`allowed_user_id`),
  KEY `FK7c2x74rinbtf33lhdcyob20sh` (`user_id`),
  CONSTRAINT `FK7c2x74rinbtf33lhdcyob20sh` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKavy6q6uo0is8pkfiw8y5s4wsc` FOREIGN KEY (`allowed_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKbklmo9kchans5u3e4va0ouo1s` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_permission`
--

LOCK TABLES `user_permission` WRITE;
/*!40000 ALTER TABLE `user_permission` DISABLE KEYS */;
INSERT INTO `user_permission` VALUES ('2025-03-19 13:30:44.766556',16,'2026-03-19 13:30:44.766587',3,1,2),('2025-03-19 10:53:02.109386',16,'2026-03-19 10:53:02.109429',3,1,3),('2025-03-19 13:30:44.912013',16,'2026-03-19 13:30:44.912042',3,2,2),('2025-03-19 10:53:02.180044',16,'2026-03-19 10:53:02.180083',3,2,3),('2025-03-19 13:30:44.916601',16,'2026-03-19 13:30:44.916625',3,3,2),('2025-03-19 10:53:02.215637',16,'2026-03-19 10:53:02.215666',3,3,3),('2025-03-19 13:30:44.920437',16,'2026-03-19 13:30:44.920459',3,4,2),('2025-03-19 10:53:02.224642',16,'2026-03-19 10:53:02.224671',3,4,3),('2025-03-19 13:30:44.923477',16,'2026-03-19 13:30:44.923499',3,5,2),('2025-03-19 10:53:02.248131',16,'2026-03-19 10:53:02.248162',3,5,3),('2025-03-19 13:30:44.926769',16,'2026-03-19 13:30:44.926811',3,6,2),('2025-03-19 10:53:02.256712',16,'2026-03-19 10:53:02.256741',3,6,3),('2025-03-19 13:30:44.931457',16,'2026-03-19 13:30:44.931505',3,7,2),('2025-03-19 10:53:02.266973',16,'2026-03-19 10:53:02.267003',3,7,3),('2025-03-19 13:30:44.935666',16,'2026-03-19 13:30:44.935691',3,8,2),('2025-03-19 10:53:02.273839',16,'2026-03-19 10:53:02.273869',3,8,3),('2025-03-19 13:31:05.521949',16,'2026-03-19 13:31:05.521972',3,9,2),('2025-03-19 13:31:05.537420',16,'2026-03-19 13:31:05.537435',3,9,3),('2025-03-19 13:30:44.939290',16,'2026-03-19 13:30:44.939316',3,10,2),('2025-03-19 10:53:02.286409',16,'2026-03-19 10:53:02.286440',3,10,3);
/*!40000 ALTER TABLE `user_permission` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-19 16:36:10
