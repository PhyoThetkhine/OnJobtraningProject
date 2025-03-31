CREATE DATABASE  IF NOT EXISTS `loanmanagementdb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `loanmanagementdb`;
-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: loanmanagementdb
-- ------------------------------------------------------
-- Server version	8.0.35

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
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,'1st street ','Lanmadaw','Yangon Region','West Yangon(Downtown)'),(2,'dgegfg','Sintgaing','Mandalay Region','Kyaukse'),(4,'dgegfg','Pyigyidagun','Mandalay Region','Mandalay'),(5,'wer gv','Mudon','Mon State','Mawlamyine'),(6,'kjgfsdhgiudsf gudfgius 11','Thingangyun','Yangon Region','East Yangon'),(7,'4tgtdeg','Myinmu','Sagaing Region','Sagaing'),(8,'tuerytyi','Hakha','Chin State','Hakha'),(9,'erwytuieuhwt','Zalun','Ayeyarwady Region','Hinthada'),(10,'eruierwtieurytiue','Zalun','Ayeyarwady Region','Hinthada'),(11,'fjhdskdhgs','Hpruso','Kayah State','Loikaw'),(12,'ofhigewgwg','Yaeni','Bago Region','Taungoo'),(13,'ifhiurhti','Lewe','Naypyidaw Union Territory','Dekkhina(South Naypyidaw)'),(14,'khhbhbuy','Mahlaing','Mandalay Region','Meiktila'),(15,'sgsdf','Kyaukhtu','Magway Region','Gangaw'),(16,'1st street ','Mawlamyine','Mon State','Mawlamyine'),(17,'wdsfad','Thanbyuzayat','Mon State','Mawlamyine'),(18,'asfsa','Thanbyuzayat','Mon State','Mawlamyine'),(19,'wsrgdsf','Khamaukgyi','Tanintharyi Region','Kawthoung'),(20,'asgvsafsa','Pakokku','Magway Region','Pakokku'),(21,'you are dog','Thayetchaung','Tanintharyi Region','Dawei'),(22,'fyl close the sky','Myothit','Sagaing Region','Tamu'),(23,'876543','Dotphoneyan','Kachin State','Bhamo'),(24,'iuytt','Manaung','Rakhine State','Kyaukpyu'),(25,'kjhgghh','Khamaukgyi','Tanintharyi Region','Kawthoung'),(26,'hjgjfk','Kaleinaung','Tanintharyi Region','Dawei'),(27,'dgdgfhfggf','Thaton','Mon State','Thaton'),(28,'hfdjjf','Aungmyethazan','Mandalay Region','Mandalay'),(29,'ihftfj','Myinmu','Sagaing Region','Sagaing'),(30,'bhjfklk','Dawei','Tanintharyi Region','Dawei'),(31,'wwfwfwdww','Mudon','Mon State','Mawlamyine'),(32,'khfjk','Bokpyin','Tanintharyi Region','Kawthoung'),(33,'rhfhrhrr','Dawei','Tanintharyi Region','Dawei'),(34,'rgeggg','Dawei','Tanintharyi Region','Dawei'),(35,'rgeggg','Dawei','Tanintharyi Region','Dawei'),(36,'aasfsfsa','Htantlang','Chin State','Hakha'),(37,'sdgsdf','Kamamaung','Kayin State','Hpapun'),(38,'wsdgdfadas','Kamamaung','Kayin State','Hpapun'),(39,'wegewg','Mudon','Mon State','Mawlamyine'),(40,'gwasfasf','Mudon','Mon State','Mawlamyine'),(41,'sfgfhwsdf','Khin-U','Sagaing Region','Shwebo'),(42,'asfew','Thanbyuzayat','Mon State','Mawlamyine'),(43,'2esgvsad','Zingyeik','Mon State','Thaton'),(44,'2452dfgvav','Pobbathiri','Naypyidaw Union Territory','Ottara(North Naypyidaw)'),(45,'afqtgfa s sa vd','Dekkhinathiri','Naypyidaw Union Territory','Dekkhina(South Naypyidaw)'),(46,'tstag sdvfa ','Hlaing','Yangon Region','West Yangon(Downtown)'),(47,'6353','Thanbyuzayat','Mon State','Mawlamyine');
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `branch`
--

LOCK TABLES `branch` WRITE;
/*!40000 ALTER TABLE `branch` DISABLE KEYS */;
INSERT INTO `branch` VALUES (2,'001','Main Branch','2023-03-25 21:01:03.817313',13,'2023-03-25 21:01:03.817313',1,2),(3,'002','Yangon Branch','2023-03-25 21:01:03.817313',13,'2025-03-19 23:23:44.542900',1,2),(4,'003','Mandalay Branch','2025-03-19 14:05:13.777448',13,'2025-03-19 14:50:37.635224',14,3),(5,'004','Mawlamyine Branch','2025-03-21 21:19:46.007848',13,'2025-03-21 21:19:46.007848',16,4),(6,'005','Zinyike Branch','2025-03-29 16:09:40.627833',13,'2025-03-30 21:16:51.833308',43,4),(7,'006','Hlaing Branch','2025-03-30 21:04:32.192090',14,'2025-04-01 01:24:35.936852',46,4);
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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `branch_current_account`
--

LOCK TABLES `branch_current_account` WRITE;
/*!40000 ALTER TABLE `branch_current_account` DISABLE KEYS */;
INSERT INTO `branch_current_account` VALUES (2,'BAC002',57249000.00,'2025-03-19 14:05:13.789966','2025-03-30 10:09:25.722351',3),(4,'BAC001',303200000.00,'2025-03-19 14:05:13.789966','2025-03-27 20:57:53.674767',2),(5,'BAC004',493220000.00,'2025-03-21 21:19:46.022884','2025-03-27 20:56:02.067871',5),(6,'BAC003',199061465.95,'2025-03-21 22:11:24.507021','2025-03-31 00:46:57.749968',4),(7,'BAC005',0.00,'2025-03-29 16:09:40.683254','2025-03-29 16:09:40.683254',6),(8,'BAC006',0.00,'2025-03-30 21:04:32.318970','2025-03-30 21:04:32.318970',7);
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `business_photo`
--

LOCK TABLES `business_photo` WRITE;
/*!40000 ALTER TABLE `business_photo` DISABLE KEYS */;
INSERT INTO `business_photo` VALUES (1,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368019/sf9usbwhxywms5kpl3or.jpg',1),(2,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368019/wdpoeuoo9fvqa1ufjgay.jpg',1),(3,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369121/jh6psagbyi0guar15qel.jpg',2),(4,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369121/suelmdmykperrddkvezg.jpg',2),(5,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369358/t59bagy4pcdehyonzryu.jpg',3),(6,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369358/nchcoryumhtpctwtk0a8.jpg',3),(7,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007691/tsjh8a0yqxlixfyjxscf.jpg',4),(8,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007691/odtlsjhhd4djv45hqelz.jpg',4),(9,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1743436644/rnjkfskbuyyyjf7xfjch.jpg',5),(10,'https://res.cloudinary.com/dwerfxy6q/image/upload/v1743436645/btqfchdww64m2vii3tms.jpg',5);
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
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cash_in_out_transaction`
--

LOCK TABLES `cash_in_out_transaction` WRITE;
/*!40000 ALTER TABLE `cash_in_out_transaction` DISABLE KEYS */;
INSERT INTO `cash_in_out_transaction` VALUES (13,'Cash_Out',1000000.00,'Test for first','2025-03-15 00:09:29.270146',2),(14,'Cash_Out',70000000.00,'Out for loan ','2025-03-16 00:34:49.906615',2),(15,'Cash_Out',10000000.00,'For loan','2025-03-17 00:36:41.534167',2),(16,'Cash_In',10000000.00,'Test ','2025-03-18 00:52:57.413058',2),(17,'Cash_Out',100000000.00,'test ','2025-03-19 02:01:14.263530',2),(18,'Cash_Out',100000000.00,'test ','2025-03-19 02:02:15.530849',2),(19,'Cash_In',200000000.00,'test ','2025-03-20 02:02:30.801459',2),(27,'Cash_Out',8000000.00,'test ','2025-03-21 22:11:24.537024',5),(28,'Cash_In',10000000.00,'test ','2025-03-22 12:18:03.580070',6),(29,'Cash_In',100000000.00,'test ','2025-03-22 12:18:50.443314',6),(30,'Cash_Out',100000.00,'tesy  ','2025-03-24 10:44:06.892671',6),(31,'Cash_Out',20000000.00,'tyrshy','2025-03-24 10:45:42.777366',6),(32,'Cash_In',20000000.00,'tasgcfav c','2025-03-24 10:46:16.630355',6),(33,'Cash_In',100000000.00,'test ','2025-03-27 20:57:53.679414',4);
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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cif_current_account`
--

LOCK TABLES `cif_current_account` WRITE;
/*!40000 ALTER TABLE `cif_current_account` DISABLE KEYS */;
INSERT INTO `cif_current_account` VALUES (1,'CAC0020000200001',186629.59,'2025-03-19 13:35:39.237832',2,'2025-03-31 00:46:57.746477',0.00,1000.00,1000.00,1),(2,'CAC0020000200002',19993000.00,'2025-03-19 13:37:01.793491',2,'2025-03-28 02:05:33.583992',0.00,1000.00,1000.00,2),(3,'CAC0020000200003',101110000.00,'2025-03-19 13:42:29.411770',2,'2025-03-29 12:28:17.791185',0.00,6000000000000000000.00,100000000000.00,3),(4,'CAC0020000200004',9900000.00,'2025-03-19 13:55:22.029558',2,'2025-03-24 12:18:21.943483',0.00,645345340.00,4954370.00,4),(5,'CAC0020000200005',210704.11,'2025-03-19 13:59:19.228979',2,'2025-03-31 00:25:57.690099',0.00,1000005.00,100003.00,5),(6,'CAC0020000200006',100000.00,'2025-03-19 14:00:56.171330',2,'2025-03-24 15:12:02.859717',0.00,89000000.00,4587884.00,6),(7,'CAC0020000200007',0.00,'2025-03-19 14:02:28.124967',2,'2025-03-19 23:38:53.019220',0.00,310000.00,30000.00,7),(8,'CAC0020000200008',113970000.00,'2025-03-19 14:03:59.302259',2,'2025-03-24 23:57:24.110840',0.00,7777777.00,563777.00,8),(9,'CAC0030000100001',1200000.00,'2025-03-24 15:14:28.044624',2,'2025-03-29 10:27:11.810916',0.00,10000.00,10000.00,9),(10,'CAC0030000100002',0.00,'2025-03-26 23:00:10.498876',2,'2025-03-26 23:00:10.498876',0.00,1000.00,1000.00,10),(11,'CAC0030000100003',0.00,'2025-03-26 23:07:24.162908',2,'2025-03-26 23:07:24.162908',0.00,10000000000.00,10000.00,11),(12,'CAC0030000100004',0.00,'2025-03-26 23:18:19.955934',2,'2025-03-26 23:18:19.955934',0.00,100000000000000.00,1000000.00,12),(13,'CAC0030000100005',0.00,'2025-03-26 23:21:25.401261',2,'2025-03-26 23:21:25.401261',0.00,10000000000.00,100000.00,13),(14,'CAC0030000100006',0.00,'2025-03-26 23:22:45.638236',2,'2025-03-26 23:22:45.638236',0.00,1999999999.00,100000.00,14),(15,'CAC0030000100007',0.00,'2025-03-26 23:24:08.075544',2,'2025-03-26 23:24:08.075544',0.00,100000000000.00,100000.00,15),(16,'CAC0030000100008',0.00,'2025-03-26 23:25:28.026179',2,'2025-03-26 23:25:28.026179',0.00,10000000000.00,100000.00,16);
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collateral`
--

LOCK TABLES `collateral` WRITE;
/*!40000 ALTER TABLE `collateral` DISABLE KEYS */;
INSERT INTO `collateral` VALUES (1,'2025-03-20 21:17:56.230930','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742482063/eohad9ba1hslspdk33kq.jpg',100000000.00,'Car','2025-03-20 21:17:56.230930',5,3),(2,'2025-03-21 13:39:25.169328','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742540954/empwancsiouczr44fjp0.jpg',700000000.00,'BMW M3','2025-03-21 13:39:25.169328',7,3),(3,'2025-03-21 13:40:04.367117','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742540993/grepwe6sj6vqorfawawk.jpg',1000000000.00,'Wife','2025-03-21 13:40:04.367117',7,3),(4,'2025-03-21 13:45:38.297679','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742541756/shwgczwmppwemydfbosv.jpg',1000000000.00,'Car','2025-03-21 13:52:47.050605',8,3),(5,'2025-03-24 15:16:00.380049','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742805949/jxrgezlq2tcleqvirgom.jpg',12141232432.00,'Car','2025-03-24 15:16:00.380049',9,4),(6,'2025-03-28 09:43:54.506913','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743131627/sn3ur5znrifqfv1p4pkl.jpg',10000000.00,'Car','2025-03-28 09:43:54.506913',3,4),(7,'2025-03-31 22:57:48.209884','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743438461/m4dbbgsgukfcotlhdvgs.jpg',100000000.00,'The leaf','2025-03-31 22:57:48.209884',2,4);
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` VALUES (1,'IT','IT','IT','2025-03-19 13:37:01.817337','2025-03-19','2025-03-19','892435987','ACE Data System','0977371737','2025-03-19 00:00:00.000000','2025-03-31 22:56:01.584489',5,2,3),(2,'IT','IT','IT','2025-03-19 13:55:22.046228','2025-05-31','2025-03-19','4875368752','DAT','09485435369','2025-03-19 06:30:00.000000','2025-03-19 13:55:22.046236',8,4,3),(3,'IT','IT','iT','2025-03-19 13:59:19.247601','2025-04-20','2025-03-19','47654','Ui','09473436464','2025-03-19 06:30:00.000000','2025-03-19 13:59:19.247611',10,5,3),(4,'id proficial','error','sowwardeveloper','2025-03-26 23:18:19.973756','2025-04-04','2025-03-12','trewrrteff','haha','09696365574','2025-03-15 06:30:00.000000','2025-03-26 23:18:19.973756',26,12,4),(5,'abcdefg','asfsadf','safsdffasdf','2025-03-31 22:27:32.829266','2025-02-26','2025-02-27','353245','asfsda','2353245','2025-03-17 00:00:00.000000','2025-03-31 22:51:01.915622',47,2,4);
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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_information_file`
--

LOCK TABLES `customer_information_file` WRITE;
/*!40000 ALTER TABLE `customer_information_file` DISABLE KEYS */;
INSERT INTO `customer_information_file` VALUES (1,'10/PaMaNa(N)231132','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367936/czddox3ya0imawcjrqoa.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367936/csyaaqdkkzujinu7il9u.jpg','2025-03-19 13:35:39.217819','2025-03-19','phyothetkhine14@gmail.com','MALE','Ye Oo','09791858246','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367936/f8ibzevyejyuumcytfhn.jpg','2025-03-19 13:35:39.217843','0020000200001','SETUP_COMPANY',13,2,3),(2,'10/PaMaNa(N)231132','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368019/wclyt6t5nkleak3kgv1y.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368019/d7vgcohdpngjsitjr7ip.jpg','2025-03-19 13:37:01.782216','2025-03-19','phyothetkhine11@gmail.com','MALE','Ye Oo','09791858246','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368019/tz8thlsccyo6iycy2z2n.jpg','2025-03-28 09:53:55.457594','0020000200002','SETUP_COMPANY',14,4,3),(3,'12/ThaLaNa(N)195037','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368348/ecgzpbtjyyuvfordzwnt.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368348/blji7p8zafvgkr3qglng.jpg','2025-03-19 13:42:29.400536','2025-03-19','san23@gmail.com','FEMALE','San San','0969469732','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742368348/amdao2z8bcneobozo3t6.jpg','2025-03-19 13:42:29.400548','0020000200003','PERSONAL',13,6,3),(4,'12/KaMaNa(N)344363','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369121/b5dgdr0klwiyzj3lor9d.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369121/xrfhaziuyi0auqbc8szu.jpg','2025-03-19 13:55:22.020733','2025-03-19','phyokyaw@gmail.com','MALE','Phyo Kyaw','09787565654','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369121/imn3zyn30mnz8ijmgg8u.jpg','2025-03-19 13:55:22.020758','0020000200004','DEVELOPED_COMPANY',13,7,3),(5,'14/ZaLaNa(N)253233','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369358/ycx6xakka5ueulputekf.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369358/ghp32yq4x4yhomidvtwf.jpg','2025-03-19 13:59:19.214429','2025-03-19','min34@gmial.com','MALE','Min MIn','0945742549','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369358/m99qsbuplmwu92avvxev.jpg','2025-03-31 16:05:07.958827','0020000200005','DEVELOPED_COMPANY',13,9,3),(6,'1/AhGaYa(N)678999','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369455/g8qlbzftmw8fheqqixdq.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369455/rdpx9x9eobmfdtgbv8jc.jpg','2025-03-19 14:00:56.161602','2025-03-07','moe35@gmail.com','FEMALE','Moe','09664424086','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369455/jod4xs1u32z5ug3es54n.jpg','2025-03-19 14:00:56.161616','0020000200006','PERSONAL',13,11,3),(7,'6/MaMaNa(N)890333','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369547/r5oajmgmh32vvnfi962w.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369546/nucq8temrvdy3sj9gnqt.jpg','2025-03-19 14:02:28.113068','2025-01-11','kyaw34@gmil.com','MALE','Kyaw','094455667770','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369546/t3mwzyifqxjb83gqxvxx.jpg','2025-03-26 23:50:22.495610','0020000200007','PERSONAL',13,12,3),(8,'2/DaMaSa(N)213888','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369637/ohl4cgukzlyyqasbkbtv.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369637/td61gwvurr4ocpsq2p3j.jpg','2025-03-19 14:03:59.292538','2025-03-19','Khnt89@gmil.com','FEMALE','Khant Nyi','09338890631','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742369638/dkz1qy2z1yfiom3dtpmo.jpg','2025-03-21 13:45:16.634465','0020000200008','PERSONAL',13,13,3),(9,'10/ThaPhaYa(N)234254','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742805857/wnx37s5gtpmfb4d7rmc5.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742805857/lnrxhusnkhijbe37r5bq.jpg','2025-03-24 15:14:28.020020','1999-03-23','tunmin@gmail.com','MALE','Tun Min','097921919191','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742805857/d2gnhck0jb2f3wwaojcw.jpg','2025-03-24 22:38:52.287677','0030000100001','PERSONAL',13,18,4),(10,'12/KaMaNa(N)242452','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743006601/tsaj3hd547t06i4ocfhr.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743006601/gopr7cg4qhg0inayam4s.jpg','2025-03-26 23:00:10.466809','2007-03-26','phyothetkine@gmail.com','MALE','Phyo Thet Khine ','09761717162','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743006601/tv8rfqrwdprwstiig5oe.jpg','2025-03-26 23:00:10.466809','0030000100002','PERSONAL',13,19,4),(11,'9/KaPaTa(P)543214','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007035/z0svcnyqeirh8xxpnrtl.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007035/n7wzvy5onahwse7wmuqn.jpg','2025-03-26 23:07:24.154521','2007-03-12','lulu@gmail.com','MALE','lulu','09786365574','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007035/cuuwms0ltygp6ypzbdly.jpg','2025-03-26 23:07:24.154521','0030000100003','PERSONAL',13,21,4),(12,'4/KaPaLa(A)987980','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007691/c9kvnfjjyyvqhu0dqztf.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007691/pagj6sqjzyodwvd6bwyw.jpg','2025-03-26 23:18:19.945943','2007-03-06','yuzana@gmail.com','FEMALE','yuzana','09786965431','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007691/orguz82g6p8pdkh4v97g.jpg','2025-03-26 23:18:19.945943','0030000100004','SETUP_COMPANY',13,25,4),(13,'11/TaKaNa(P)453457','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007876/z2esmxzzwhxtn3hcgm22.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007876/uwmenilrwlxmpkrxknsl.jpg','2025-03-26 23:21:25.393973','2007-03-09','mikey@gmail.com','FEMALE','mikey','09785423345','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007876/lxdadrypegynnt9s6mxz.jpg','2025-03-26 23:21:25.393973','0030000100005','PERSONAL',13,27,4),(14,'11/PaTaNa(E)098765','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007956/ehyoglyk6vipm17oljv0.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007957/o71yfofeczu823kll9uc.jpg','2025-03-26 23:22:45.629876','2007-03-08','leee@gmail.com','MALE','leee','09875432123','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007956/ufgl1yu0trlq2xwj4cyl.jpg','2025-03-26 23:22:45.629876','0030000100006','PERSONAL',13,28,4),(15,'13/KaTaTa(P)987098','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743008039/towkaitexxjrv6zs39hn.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743008039/rwrbsyx7k6ypnxa0bgqc.jpg','2025-03-26 23:24:08.068596','2007-03-07','aung@gmail.com','MALE','aungaung','09654326678','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743008039/ezdxuzckacq7xiv7f4xj.jpg','2025-03-26 23:50:39.643238','0030000100007','PERSONAL',14,29,4),(16,'11/SaTaNa(E)876543','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743008119/nkhszznoh7dwb6qcpcvs.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743008119/hxuxc0ufpdws9isb1bav.jpg','2025-03-26 23:25:28.019939','2007-03-13','iu@gmail.com','FEMALE','IU','09987654432','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743008119/pg194jzlbhwa5egr0bbv.jpg','2025-03-26 23:25:28.019939','0030000100008','PERSONAL',13,30,4);
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
  `created_user_id` int NOT NULL,
  `sub_category_id` int NOT NULL,
  `cif_id` int NOT NULL,
  `status` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKemqm5vtpf7x6qmbgqx46rx6xj` (`created_user_id`),
  KEY `FK4d9hyynbbvalfwr3wnopvuae5` (`sub_category_id`),
  KEY `FKqxd2hujkmyyd0d94i60t0mhap` (`cif_id`),
  CONSTRAINT `FK4d9hyynbbvalfwr3wnopvuae5` FOREIGN KEY (`sub_category_id`) REFERENCES `sub_category` (`id`),
  CONSTRAINT `FKemqm5vtpf7x6qmbgqx46rx6xj` FOREIGN KEY (`created_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKqxd2hujkmyyd0d94i60t0mhap` FOREIGN KEY (`cif_id`) REFERENCES `customer_information_file` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dealer_product`
--

LOCK TABLES `dealer_product` WRITE;
/*!40000 ALTER TABLE `dealer_product` DISABLE KEYS */;
INSERT INTO `dealer_product` VALUES (2,'2025-04-01 00:10:41.086891','asfsadf8888','I Phone Pro',4000005.00,'2025-04-01 00:59:47.359513',4,1,13,13);
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `financial`
--

LOCK TABLES `financial` WRITE;
/*!40000 ALTER TABLE `financial` DISABLE KEYS */;
INSERT INTO `financial` VALUES (1,'254235','242524.0','612822.0','2352.0','2352435.0','2025-03-19 13:37:01.857865','24353.0','eegc c htethjbu','2025-03-22 00:20:38.292661',1,3),(2,'243533','3453578.0','342532.0','352785.0','323643.0','2025-03-19 13:55:22.077731','352543.0','gyewy kfghighavsv','2025-03-22 22:27:07.405348',2,3),(3,'675775','345576.0','43453.001219','435454.0','565856.0','2025-03-19 13:59:19.293683','345657.0','hgdfughiudfdasfvzd abc','2025-03-30 23:46:06.925972',3,3),(4,'12000','1000000.0','3000000.0','1.0E7','1.0E14','2025-03-26 23:18:20.010274','2000000.0','fwwwwww','2025-03-26 23:18:20.010274',4,4),(5,'235325','354325.0','235.0','23534.0','35423.0','2025-03-31 22:27:32.880565','235324.0','sfgsadf','2025-03-31 22:27:32.880565',5,4);
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `total_paid` decimal(32,2) NOT NULL,
  `hp_term_id` int NOT NULL,
  `interest_late_day` int NOT NULL,
  `principa_late_day` int NOT NULL,
  `pod_paid` decimal(32,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKo9pe5t8xg5er65rv4tkvhjuxn` (`hp_term_id`),
  CONSTRAINT `FKo9pe5t8xg5er65rv4tkvhjuxn` FOREIGN KEY (`hp_term_id`) REFERENCES `hp_term` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hp_loan_history`
--

LOCK TABLES `hp_loan_history` WRITE;
/*!40000 ALTER TABLE `hp_loan_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `hp_loan_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hp_long_overpaid_history`
--

DROP TABLE IF EXISTS `hp_long_overpaid_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hp_long_overpaid_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `outstanding_amount` decimal(32,2) NOT NULL,
  `late_days` int NOT NULL,
  `latefee_amount` decimal(32,2) NOT NULL,
  `paid_amount` decimal(32,2) NOT NULL,
  `paid_date` datetime(6) NOT NULL,
  `loan_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqdg067m6jmsxfyuh64ufkrjvs` (`loan_id`),
  CONSTRAINT `FKqdg067m6jmsxfyuh64ufkrjvs` FOREIGN KEY (`loan_id`) REFERENCES `hp_loan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hp_long_overpaid_history`
--

LOCK TABLES `hp_long_overpaid_history` WRITE;
/*!40000 ALTER TABLE `hp_long_overpaid_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `hp_long_overpaid_history` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
INSERT INTO `loan_conllateral` VALUES (1,5),(1,6),(5,7),(1,8),(6,9),(1,10),(1,11);
/*!40000 ALTER TABLE `loan_conllateral` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logan_overpaid_history`
--

DROP TABLE IF EXISTS `logan_overpaid_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logan_overpaid_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `latefee_amount` decimal(32,2) NOT NULL,
  `outstanding_amount` decimal(32,2) NOT NULL,
  `paid_amount` decimal(32,2) NOT NULL,
  `paid_date` datetime(6) NOT NULL,
  `loan_id` int NOT NULL,
  `late_days` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9106nv17nl4e0gyx6qyds24fw` (`loan_id`),
  CONSTRAINT `FK9106nv17nl4e0gyx6qyds24fw` FOREIGN KEY (`loan_id`) REFERENCES `sme_loan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logan_overpaid_history`
--

LOCK TABLES `logan_overpaid_history` WRITE;
/*!40000 ALTER TABLE `logan_overpaid_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `logan_overpaid_history` ENABLE KEYS */;
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
  `status` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKinxg5ynqbykb7l5q6skky4o15` (`category`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `main_category`
--

LOCK TABLES `main_category` WRITE;
/*!40000 ALTER TABLE `main_category` DISABLE KEYS */;
INSERT INTO `main_category` VALUES (1,'Car',13),(2,'asfd',13),(3,'Eledfsd',13);
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_method`
--

LOCK TABLES `payment_method` WRITE;
/*!40000 ALTER TABLE `payment_method` DISABLE KEYS */;
INSERT INTO `payment_method` VALUES (1,'2025-03-19 13:30:44.751475','K Pay',13,'2025-04-01 00:59:32.708052',3),(2,'2025-03-19 13:30:44.751475','AYA ',13,'2025-03-19 13:30:44.751475',3),(3,'2025-03-19 13:30:44.751475','A Bank',13,'2025-03-19 13:30:44.751475',3),(4,'2025-03-27 11:18:49.554979','ACE Bank',13,'2025-03-28 09:54:48.878159',4),(5,'2025-03-27 11:19:20.427870','KBZ Bank',21,'2025-03-28 10:51:45.370812',4),(6,'2025-03-27 21:43:36.168590','Bank',13,'2025-03-27 21:43:36.168590',4);
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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` VALUES (10,'AccessControl Menu'),(1,'Accounting Menu'),(16,'Add New Business for Client'),(4,'Branch Management Menu'),(5,'Client Management Menu'),(2,'Dealer Management Menu'),(8,'HP Menu'),(6,'Loan Management Menu'),(17,'Manage Individual Staff Permissions'),(11,'Money Transformation'),(3,'Payment Method Menu'),(7,'SME Menu'),(9,'Staff Management Menu'),(12,'Update Branch Info'),(14,'Update Client Info'),(13,'Update Staff Info');
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'MainBranchLevel','2023-03-25 21:01:03.817313','Super Admin','2023-03-25 21:01:03.817313'),(2,'RegularBranchLevel','2025-03-19 10:52:31.658844','Branch Manager','2025-04-01 01:50:25.457546'),(3,'MainBranchLevel','2025-03-20 11:38:37.876488','Loan Officer','2025-03-21 16:29:12.525157'),(4,'RegularBranchLevel','2025-03-24 14:13:04.204310','Hp Loan Manager','2025-03-24 14:13:04.204310'),(5,'MainBranchLevel','2025-03-24 15:51:18.682507','Client Manager','2025-03-24 15:51:18.682507'),(6,'RegularBranchLevel','2025-03-28 10:45:09.041515','Loan Manager','2025-03-28 10:45:09.042632');
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
INSERT INTO `role_permission` VALUES (1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,1),(8,1),(9,1),(10,1),(1,2),(2,2),(3,2),(4,2),(5,2),(6,2),(7,2),(8,2),(9,2),(10,2),(11,2),(12,2),(13,2),(14,2),(16,2),(17,2),(3,3),(4,3),(5,3),(6,3),(7,3),(8,3),(9,3),(1,4),(2,4),(5,4),(6,4),(8,4),(5,5),(14,5),(6,6),(7,6),(8,6);
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sme_loan`
--

LOCK TABLES `sme_loan` WRITE;
/*!40000 ALTER TABLE `sme_loan` DISABLE KEYS */;
INSERT INTO `sme_loan` VALUES (5,1000.00,'2024-03-28 01:51:58.871256','2025-03-28 01:52:53.884747',12,20.00,2,4,'2025-02-28',0,14,10,1000.00,14,20.00,2,'2024-11-28',9,'2025-03-28 18:13:22.767332','MONTHLY',19,'test      ','002000020000500004',5,4,4),(6,100000.00,'2025-03-28 09:50:58.283585','2025-03-28 12:07:16.805272',12,2000.00,2,12,'2026-03-28',10,14,11,100000.00,15,2000.00,2,'2025-03-28',11,'2025-03-28 12:07:17.276761','MONTHLY',20,'agasdfsfsdgd','002000020000500005',5,4,4),(7,100000.00,'2025-03-29 10:25:28.459498','2025-03-29 10:27:11.350899',15,2000.00,2,11,'2026-02-28',10,14,12,100000.00,20,2000.00,2,'2025-03-29',11,'2025-03-29 10:27:11.806917','MONTHLY',19,'test       ','003000010000100001',9,4,4),(8,100000.00,'2025-03-29 12:19:48.136659','2025-03-29 12:20:34.153721',12,2000.00,2,4,'2025-03-29',10,14,10,100000.00,14,2000.00,2,'2024-11-29',11,'2025-03-29 12:20:34.177709','MONTHLY',19,'test      ','002000020000500006',5,4,4),(9,100000.00,'2025-03-29 12:27:26.685276','2025-03-29 12:28:17.755864',12,2000.00,2,3,'2025-04-29',10,14,10,100000.00,14,2000.00,2,'2024-11-29',11,'2025-03-29 12:28:17.791185','MONTHLY',19,'test       ','002000020000300001',3,4,4),(10,100000.00,'2025-03-29 15:26:14.368382','2025-03-29 15:27:32.809367',14,2000.00,2,4,'2025-07-29',9,14,13,100000.00,16,2000.00,2,'2025-03-29',11,'2025-03-29 15:27:32.852337','MONTHLY',20,'jvuguguhbjhb','002000020000500007',5,4,4),(11,0.00,'2025-03-29 16:14:54.119178',NULL,0,0.00,0,89,NULL,0,0,0,886.00,0,0.00,0,NULL,3,'2025-03-29 16:14:54.119178','YEARLY',19,'dfjhjgjhkj','002000020000500008',5,NULL,4);
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
  `principal_paid` decimal(32,2) NOT NULL,
  `total_paid` decimal(32,2) NOT NULL,
  `sme_term_id` int NOT NULL,
  `interest_late_day` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK45xr4ew6c4g8sb03juaumdh2c` (`sme_term_id`),
  CONSTRAINT `FK45xr4ew6c4g8sb03juaumdh2c` FOREIGN KEY (`sme_term_id`) REFERENCES `sme_term` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sme_loan_history`
--

LOCK TABLES `sme_loan_history` WRITE;
/*!40000 ALTER TABLE `sme_loan_history` DISABLE KEYS */;
INSERT INTO `sme_loan_history` VALUES (3,31.51,13.00,12.78,0.00,0.00,'2025-03-28 18:13:22.601697',0.00,57.29,48,90),(4,54.74,25.00,33.30,0.00,0.00,'2025-03-28 18:13:22.601697',0.00,113.04,49,60),(5,45.62,25.00,55.50,0.00,0.00,'2025-03-28 18:13:22.601697',1000.00,1126.12,50,30);
/*!40000 ALTER TABLE `sme_loan_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sme_long_overpaid_history`
--

DROP TABLE IF EXISTS `sme_long_overpaid_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sme_long_overpaid_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `outstanding_amount` decimal(32,2) NOT NULL,
  `late_days` int NOT NULL,
  `latefee_amount` decimal(32,2) NOT NULL,
  `paid_amount` decimal(32,2) NOT NULL,
  `paid_date` datetime(6) NOT NULL,
  `loan_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdfj1fu5qu76oosv6g9qi4hykv` (`loan_id`),
  CONSTRAINT `FKdfj1fu5qu76oosv6g9qi4hykv` FOREIGN KEY (`loan_id`) REFERENCES `sme_loan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sme_long_overpaid_history`
--

LOCK TABLES `sme_long_overpaid_history` WRITE;
/*!40000 ALTER TABLE `sme_long_overpaid_history` DISABLE KEYS */;
INSERT INTO `sme_long_overpaid_history` VALUES (5,1059.07,180,6267.37,6267.37,'2025-03-28 02:53:38.056670',5);
/*!40000 ALTER TABLE `sme_long_overpaid_history` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sme_term`
--

LOCK TABLES `sme_term` WRITE;
/*!40000 ALTER TABLE `sme_term` DISABLE KEYS */;
INSERT INTO `sme_term` VALUES (47,31,'2024-11-28',0.00,180,0.00,11.90,NULL,0.00,0.00,1000.00,9,0.00,5),(48,30,'2024-12-28',0.00,0,0.00,0.00,'2025-03-28',0.00,0.00,1000.00,7,57.29,5),(49,31,'2025-01-28',0.00,0,0.00,0.00,'2025-03-28',0.00,0.00,1000.00,7,113.04,5),(50,30,'2025-02-28',0.00,0,0.00,0.00,'2025-03-28',0.00,0.00,0.00,9,1126.12,5),(51,31,'2025-04-28',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,6),(52,30,'2025-05-28',1150.68,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,6),(53,31,'2025-06-28',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,6),(54,30,'2025-07-28',1150.68,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,6),(55,31,'2025-08-28',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,6),(56,31,'2025-09-28',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,6),(57,30,'2025-10-28',1150.68,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,6),(58,31,'2025-11-28',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,6),(59,30,'2025-12-28',1150.68,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,6),(60,31,'2026-01-28',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,6),(61,31,'2026-02-28',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,6),(62,28,'2026-03-28',1073.97,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,6),(63,31,'2025-04-29',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,7),(64,30,'2025-05-29',1150.68,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,7),(65,31,'2025-06-29',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,7),(66,30,'2025-07-29',1150.68,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,7),(67,31,'2025-08-29',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,7),(68,31,'2025-09-29',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,7),(69,30,'2025-10-29',1150.68,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,7),(70,31,'2025-11-29',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,7),(71,30,'2025-12-29',1150.68,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,7),(72,31,'2026-01-29',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,7),(73,30,'2026-02-28',1150.68,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,7),(74,31,'2024-12-29',0.00,210,0.00,1189.04,NULL,0.00,0.00,100000.00,7,0.00,8),(75,30,'2025-01-29',0.00,180,0.00,1150.68,NULL,0.00,0.00,100000.00,7,0.00,8),(76,31,'2025-02-28',0.00,90,0.00,1189.04,NULL,0.00,0.00,100000.00,7,0.00,8),(77,30,'2025-03-29',0.00,30,0.00,1150.68,NULL,0.00,0.00,100000.00,7,0.00,8),(78,31,'2025-02-28',0.00,30,0.00,1189.04,NULL,0.00,0.00,100000.00,7,0.00,9),(79,30,'2025-03-29',0.00,0,0.00,1189.04,NULL,0.00,0.00,100000.00,7,0.00,9),(80,31,'2025-04-29',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,9),(81,31,'2025-04-29',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,10),(82,30,'2025-05-29',1150.68,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,10),(83,31,'2025-06-29',1189.04,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,10),(84,30,'2025-07-29',1150.68,0,0.00,0.00,NULL,0.00,0.00,100000.00,11,0.00,10);
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
  `status` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkq9psg2c3clgkh343avvkxir2` (`category`),
  KEY `FK753xnmtixdl7hf7c7h3hsjuam` (`main_category_id`),
  CONSTRAINT `FK753xnmtixdl7hf7c7h3hsjuam` FOREIGN KEY (`main_category_id`) REFERENCES `main_category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sub_category`
--

LOCK TABLES `sub_category` WRITE;
/*!40000 ALTER TABLE `sub_category` DISABLE KEYS */;
INSERT INTO `sub_category` VALUES (1,'BMW',1,13),(2,'FUCK',2,13),(3,'Ye',3,13);
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
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (7,1000000.00,2,'BRANCH',1,'CIF','2025-03-15 00:07:52.425762',1),(8,1000000.00,2,'BRANCH',3,'CIF','2025-03-17 00:08:19.652740',1),(9,1000000.00,2,'BRANCH',4,'CIF','2025-03-19 00:08:44.872768',1),(10,3900000.00,2,'BRANCH',8,'CIF','2025-03-20 00:10:05.203346',1),(11,200000.00,2,'BRANCH',1,'CIF','2025-03-20 00:48:40.176829',2),(12,10000.00,2,'BRANCH',3,'CIF','2025-03-20 00:49:10.339206',2),(13,10000000.00,2,'BRANCH',8,'CIF','2025-03-20 01:57:20.528193',3),(14,100000000.00,2,'BRANCH',3,'CIF','2025-03-20 01:57:41.685757',3),(15,10000000.00,2,'BRANCH',4,'CIF','2025-03-20 01:58:33.708814',1),(16,10000000.00,2,'BRANCH',2,'CIF','2025-03-20 01:59:11.156147',2),(17,2000000.00,2,'BRANCH',6,'CIF','2025-03-20 10:58:02.521745',2),(18,100000.00,2,'BRANCH',8,'CIF','2025-03-21 16:42:30.712423',1),(19,100000.00,2,'BRANCH',5,'CIF','2025-03-21 16:49:40.905655',1),(20,10000000.00,2,'BRANCH',2,'CIF','2025-03-21 21:16:23.747757',3),(21,100000000.00,8,'CIF',2,'BRANCH','2025-03-21 21:31:27.303021',3),(22,80000000.00,4,'BRANCH',5,'BRANCH','2025-03-21 21:40:21.752951',2),(23,800000.00,4,'BRANCH',5,'BRANCH','2025-03-21 21:40:51.229587',3),(24,100000.00,5,'BRANCH',6,'CIF','2025-03-21 23:37:19.861303',1),(25,10000000.00,6,'BRANCH',5,'BRANCH','2025-03-22 12:20:17.524395',2),(26,10000000.00,6,'BRANCH',2,'CIF','2025-03-23 01:22:59.670803',1),(27,100000.00,2,'BRANCH',8,'CIF','2025-03-24 11:54:21.796244',2),(28,100000.00,6,'CIF',6,'BRANCH','2025-03-24 12:08:43.660970',1),(29,2000000.00,6,'CIF',4,'BRANCH','2025-03-24 12:09:16.761102',2),(30,100000.00,5,'CIF',2,'BRANCH','2025-03-24 12:10:12.964384',1),(31,100000.00,8,'CIF',2,'BRANCH','2025-03-24 12:16:19.335913',1),(32,1000000.00,4,'CIF',4,'BRANCH','2025-03-24 12:17:17.015648',2),(33,100000.00,4,'CIF',5,'BRANCH','2025-03-24 12:18:21.935950',1),(34,10000.00,8,'CIF',5,'BRANCH','2025-03-24 12:19:14.441375',3),(35,100000.00,6,'BRANCH',6,'CIF','2025-03-24 15:12:02.835292',3),(36,1000000.00,6,'BRANCH',9,'CIF','2025-03-24 15:15:10.792168',2),(37,10000.00,6,'BRANCH',9,'CIF','2025-03-24 23:06:48.730604',1),(38,10000.00,9,'CIF',2,'BRANCH','2025-03-24 23:20:42.263210',1),(39,10000.00,8,'CIF',5,'BRANCH','2025-03-24 23:57:10.259512',2),(40,10000.00,8,'CIF',2,'BRANCH','2025-03-24 23:57:24.094457',2),(41,10000.00,2,'CIF',6,'BRANCH','2025-03-24 23:59:03.741506',2),(42,10000000.00,2,'CIF',5,'BRANCH','2025-03-25 00:01:38.250539',3),(43,88890000.00,6,'BRANCH',2,'BRANCH','2025-03-27 20:26:28.337601',2),(44,800800000.00,2,'BRANCH',4,'BRANCH','2025-03-27 20:42:39.748853',2),(45,400400000.00,4,'BRANCH',5,'BRANCH','2025-03-27 20:56:02.067871',3),(46,200200000.00,4,'BRANCH',6,'BRANCH','2025-03-27 20:57:11.398515',4),(47,3000.00,6,'BRANCH',2,'CIF','2025-03-28 02:05:33.534941',2),(48,5000.00,6,'BRANCH',5,'CIF','2025-03-28 02:29:44.515828',2),(49,100000.00,6,'BRANCH',5,'CIF','2025-03-28 02:54:25.721825',2),(50,192673.00,5,'CIF',6,'BRANCH','2025-03-28 12:18:12.046911',3),(51,10000.00,6,'BRANCH',5,'CIF','2025-03-28 18:13:22.748250',3),(52,1249000.00,1,'CIF',2,'BRANCH','2025-03-30 10:09:25.571297',3),(53,1000000.00,6,'BRANCH',1,'CIF','2025-03-30 10:09:54.687173',4),(54,1000000.00,1,'CIF',6,'BRANCH','2025-03-30 10:10:30.035574',2),(55,10000000.00,6,'BRANCH',1,'CIF','2025-03-30 10:14:46.245430',2),(56,9650063.09,1,'CIF',6,'BRANCH','2025-03-30 10:30:22.816724',3),(57,100000.00,6,'BRANCH',1,'CIF','2025-03-30 10:31:26.835920',4),(58,95890.41,1,'CIF',6,'BRANCH','2025-03-30 10:35:30.047636',4),(59,100000.00,6,'BRANCH',1,'CIF','2025-03-30 10:39:49.933125',4),(60,80839.45,1,'CIF',6,'BRANCH','2025-03-30 10:47:41.615503',4),(61,100000.00,6,'BRANCH',1,'CIF','2025-03-30 10:48:10.368533',4),(62,100000.00,6,'BRANCH',1,'CIF','2025-03-31 00:46:57.453185',3);
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
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (2,'10/PaMaNa(N)276146','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367281/f1evb7jagfgzpstzruyo.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367280/gvvlmw7ejcbrbmi8begm.jpg','2023-03-25 21:01:03.817313','2007-03-16','phyothetkhine@gmail.com','MALE','Phyo','09876765765','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367280/ivs05ke7qx1wosedwppr.jpg','2025-03-26 23:49:25.818529','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00100001',1,2,2,2),(3,'12/KaMaTa(N)123434','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367579/qfl20i7msi1hjuk6s3h9.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367578/dl48kszxsuko0x9wxgt1.jpg','2023-03-25 21:01:03.817313','2023-03-25','phyothetkhineads@gmail.com','MALE','Thet','09876765765','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742367577/zsfrh8hkmuhgrsydlk2f.jpg','2025-03-26 23:49:57.217088','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',14,'00200002',1,2,3,2),(4,'8/AhLaNa(N)123242','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742542397/powhepqbvsyddubzhch9.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742542397/dchkdrrvngufmohpf3to.jpg','2025-03-21 14:03:28.886474','2006-03-26','htoohtoo@gmail.com','MALE','Htoo Htoo','09791858240','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742542397/ugwtrfqulvux8zznhr8i.jpg','2025-03-30 13:09:28.227519','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00300001',15,3,4,2),(5,'10/KaKhaMa(N)648348','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742570036/q42z1zhwulqyz2e6jibu.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742570036/cgiohhc49clvhfnzuafu.jpg','2025-03-21 21:44:07.736905','2005-11-09','phoe@gmail.com','FEMALE','Poe Poe ','09791858248','https://res.cloudinary.com/dwerfxy6q/image/upload/v1742570036/ty3zvj6gia6fzo9pu6gr.jpg','2025-03-26 23:49:19.664254','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00300002',17,4,4,1),(6,'4/HtaTaLa(N)241234','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743006779/ow6fzfrlhu34pfp9darx.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743006779/iznimneispfqnivpb1ps.jpg','2025-03-26 23:03:07.946869','2007-02-26','phyothetkhine112@gmail.com','MALE','Ma Oo ','09718261712','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743006779/o9px8jtbgf6fczswcbzw.jpg','2025-03-27 20:27:58.965553','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',15,'00200003',20,4,3,2),(7,'8/KaMaNa(P)987689','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007149/thjjureu8armdxiq1sij.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007149/zieuurkqehkyepfoyiic.jpg','2025-03-26 23:09:17.518709','2007-03-22','kyaw@gmail.com','MALE','kyaw gyi','09786365575','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007149/tl3b27hk9jtxazadqduo.jpg','2025-03-26 23:09:17.518709','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00200004',22,4,3,3),(8,'5/HtaPaKha(A)234567','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007256/xds5izm7m9nxvefxp4rw.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007256/ozgsmurtttuzadwrnrow.jpg','2025-03-26 23:11:04.915766','2007-03-06','juju@gmail.com','FEMALE','juju','092345678909','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007256/jujfmhhaxw4ux8fusog1.jpg','2025-03-26 23:11:04.915766','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00200005',23,4,3,1),(9,'6/KaThaNa(A)987657','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007331/rhn2rbdhvymhg2ly5s1r.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007331/pmdthlihurl0orlr5wdj.jpg','2025-03-26 23:12:19.671945','2007-03-16','yuyu@gmail.com','MALE','yuyu','0978654321','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743007331/z1uzbwocjmbpkzggodmi.jpg','2025-03-26 23:12:19.671945','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00200006',24,4,3,1),(10,'4/MaTaNa(A)234456','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743008250/trjudlxy8gjpyvzy6zgq.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743008250/hnmcwz33aroyiaj9fk8y.jpg','2025-03-26 23:27:39.691778','2007-03-15','luluaung@gmail.com','MALE','luluaung','09898987765','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743008250/vnwwlbfkicffrlntpqab.jpg','2025-03-26 23:27:39.691778','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00100002',31,4,2,4),(11,'3/KaDaNa(P)678543','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743008332/xydlozolqo0pqkm3x3oc.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743008332/va2xrg62qvn0vboabe27.jpg','2025-03-26 23:29:01.045929','2007-03-15','kyawpo@gmail.com','MALE','kyawpopo','09898765543','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743008332/jrzg00mvothjlik1tfuo.jpg','2025-03-26 23:29:01.045929','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00200007',32,4,3,4),(14,'5/HaMaLa(A)123456','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743008505/jl57cau5dipupvdvyn5r.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743008505/q3yelx9j9s6vmpqqc2i8.jpg','2025-03-26 23:31:53.725765','2007-03-07','oo@gmail.com','MALE','Ooo','09976543321','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743008505/pyoiphg7nczz7icxdba5.jpg','2025-03-26 23:31:53.725765','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00200008',35,4,3,2),(16,'2/DaMaSa(N)234243','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743009017/hyolxy72dag3bdiizepb.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743009017/qqwyyrkm877gw76qppjt.jpg','2025-03-26 23:40:26.251106','2006-03-26','oomaywin@gmail.com','FEMALE','Oo May Win','09791322532','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743009017/mke1wappm6b5qo0libgh.jpg','2025-03-26 23:40:26.251106','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00300003',37,4,4,3),(17,'7/AhPhaNa(N)125452','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743009107/g3m6jxsdxyt5rexknoib.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743009107/lal8l19rojebrkq0j880.jpg','2025-03-26 23:41:55.887200','2006-09-26','phyokyawwin@gmail.com','MALE','Phyo Kyaw Win','097163616321','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743009107/jymvqlpbmhuxhi7viz1u.jpg','2025-03-26 23:43:33.262770','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00400001',38,4,5,2),(18,'4/HtaTaLa(N)356323','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743009307/we2dqgrvkh8wz8bxpkdh.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743009307/lttdyoahoqlrm73uoilq.jpg','2025-03-26 23:45:15.833939','2007-03-26','winkyaw@gmail.com','MALE','Win Kyaw ','09174616314','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743009307/vvgbvkpnloessvxq2cyp.jpg','2025-03-26 23:45:15.833939','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00400002',39,4,5,4),(19,'4/HaKhaNa(N)123412','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743009430/bqunad0mahcg71hroou2.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743009430/wfu3jaapxedoyeooc1rj.jpg','2025-03-26 23:47:18.531486','2007-03-13','kyawwinnaing@gmail.com','MALE','Kyaw Win Naing ','091782372382314','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743009430/tjid6fpybxjddgyjo2hf.jpg','2025-03-26 23:47:18.531486','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00400003',40,4,5,5),(20,'2/BaLaKha(N)356534','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743009519/pd5dauhx6je5i9jse5tl.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743009519/ggsucwgwwox4r6ydgify.jpg','2025-03-26 23:48:47.451020','2007-03-25','hungkyaw@gmail.com','MALE','Hung Kyaw','09817271736','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743009519/tayurgna0vgwmwkrgens.jpg','2025-03-26 23:48:47.451020','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00400004',41,4,5,3),(21,'7/AhPhaNa(N)344363','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743085487/gxxufnidpuww9xnzp0pn.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743085487/dbxbsb0nl4juaqfgthxv.jpg','2025-03-27 20:54:54.894788','2007-02-26','mahla@gmail.com','FEMALE','Ma Hla','09887866875','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743085486/nedrmjt5f5j9hgv0iomg.jpg','2025-03-27 20:54:54.894788','$2a$10$tVX1cC0Yq5MC5eF5YNzVle/vMqTuhvUqtPkD/Pv04vw2c3cNGm0d6',13,'00100003',42,6,2,2),(22,'3/BaThaSa(N)537354','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743312340/eeex6wpmtijm0pd974dy.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743312340/oerqq4rm93ldx3b4njyf.jpg','2025-03-30 11:55:47.117884','2007-03-30','pthat707@gmail.com','MALE','Ye Yint Oo','09782637632','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743312340/ipssf5cthg1bh13hzvbv.jpg','2025-03-30 11:55:47.117884','$2a$10$8mfzt0Wsw7PysgAzdkZzQOWUNkOJxnpy2OPAMDBuwIn34LF5tRHF2',13,'00400005',44,4,5,4),(23,'2/BaLaKha(N)245254','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743312710/sf7oayvfklldmvix9y01.jpg','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743312711/tdggcnpzopsirjjceade.jpg','2025-03-30 12:01:57.339309','2007-03-03','yeyintoo1732006@gmail.com','MALE','Ye Yint Too','09272827242','https://res.cloudinary.com/dwerfxy6q/image/upload/v1743312711/h9hhirsh5fk3dyiwwg9h.jpg','2025-03-30 12:01:57.339309','$2a$10$SoDfZwRaKcMm/raDQNoIQ.PA.OHLsIYT6HdG5Uf.RFVdCdIK8zic6',13,'00400006',45,4,5,2);
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_current_account`
--

LOCK TABLES `user_current_account` WRITE;
/*!40000 ALTER TABLE `user_current_account` DISABLE KEYS */;
INSERT INTO `user_current_account` VALUES (1,'UAC00300001',0.00,'2025-03-21 14:03:29.018263',2,'2025-03-21 14:03:29.018263',4),(2,'UAC00400001',0.00,'2025-03-21 21:44:07.916808',2,'2025-03-21 21:44:07.916808',5),(3,'UAC00200003',0.00,'2025-03-26 23:03:08.057162',2,'2025-03-26 23:03:08.057162',6),(4,'UAC00200004',0.00,'2025-03-26 23:09:17.576812',2,'2025-03-26 23:09:17.576812',7),(5,'UAC00200005',0.00,'2025-03-26 23:11:05.019634',2,'2025-03-26 23:11:05.019634',8),(6,'UAC00200006',0.00,'2025-03-26 23:12:19.786790',2,'2025-03-26 23:12:19.786790',9),(7,'UAC00100002',0.00,'2025-03-26 23:27:39.741981',2,'2025-03-26 23:27:39.741981',10),(8,'UAC00200007',0.00,'2025-03-26 23:29:01.082072',2,'2025-03-26 23:29:01.082072',11),(9,'UAC00200008',0.00,'2025-03-26 23:31:53.924883',2,'2025-03-26 23:31:53.924883',14),(10,'UAC00300003',0.00,'2025-03-26 23:40:26.377999',2,'2025-03-26 23:40:26.377999',16);
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
INSERT INTO `user_permission` VALUES ('2025-03-19 13:30:44.766556',16,'2026-03-19 13:30:44.766587',3,1,2),('2025-03-24 11:51:15.172447',16,'2026-03-24 11:51:15.172447',4,1,3),('2025-03-21 14:03:44.575852',16,'2026-03-21 14:03:44.575852',3,1,4),('2025-03-22 01:37:38.685280',16,'2026-03-22 01:37:38.685280',4,1,5),('2025-03-27 20:27:58.998874',16,'2026-03-27 20:27:58.998874',4,1,6),('2025-03-26 23:11:04.936451',16,'2026-03-26 23:11:04.936451',4,1,8),('2025-03-26 23:12:19.693315',16,'2026-03-26 23:12:19.693315',4,1,9),('2025-03-26 23:27:39.704053',16,'2026-03-26 23:27:39.704053',4,1,10),('2025-03-26 23:29:01.055465',16,'2026-03-26 23:29:01.055465',4,1,11),('2025-03-26 23:31:53.745163',16,'2026-03-26 23:31:53.745163',4,1,14),('2025-03-26 23:43:33.314341',16,'2026-03-26 23:43:33.314341',4,1,17),('2025-03-26 23:45:15.849536',16,'2026-03-26 23:45:15.849536',4,1,18),('2025-03-27 20:54:54.942513',16,'2026-03-27 20:54:54.942513',6,1,21),('2025-03-30 11:55:47.147355',16,'2026-03-30 11:55:47.147355',4,1,22),('2025-03-30 12:01:57.369535',16,'2026-03-30 12:01:57.369535',4,1,23),('2025-03-19 13:30:44.912013',16,'2026-03-19 13:30:44.912042',3,2,2),('2025-03-24 11:51:15.222395',16,'2026-03-24 11:51:15.222395',4,2,3),('2025-03-21 14:03:44.575852',16,'2026-03-21 14:03:44.575852',3,2,4),('2025-03-22 01:37:38.709763',16,'2026-03-22 01:37:38.709763',4,2,5),('2025-03-27 20:27:59.032228',16,'2026-03-27 20:27:59.032228',4,2,6),('2025-03-26 23:11:04.951277',16,'2026-03-26 23:11:04.951277',4,2,8),('2025-03-26 23:12:19.709114',16,'2026-03-26 23:12:19.709114',4,2,9),('2025-03-26 23:27:39.714763',16,'2026-03-26 23:27:39.714763',4,2,10),('2025-03-26 23:29:01.062991',16,'2026-03-26 23:29:01.062991',4,2,11),('2025-03-26 23:31:53.757711',16,'2026-03-26 23:31:53.757711',4,2,14),('2025-03-26 23:43:33.338480',16,'2026-03-26 23:43:33.338480',4,2,17),('2025-03-26 23:45:15.859797',16,'2026-03-26 23:45:15.859797',4,2,18),('2025-03-27 20:54:54.973907',17,'2026-03-27 20:54:54.973907',6,2,21),('2025-03-30 11:55:47.177088',16,'2026-03-30 11:55:47.177088',4,2,22),('2025-03-30 12:01:57.381687',16,'2026-03-30 12:01:57.381687',4,2,23),('2025-03-19 13:30:44.916601',16,'2026-03-19 13:30:44.916625',3,3,2),('2025-03-24 11:51:15.239087',16,'2026-03-24 11:51:15.239087',4,3,3),('2025-03-21 14:03:44.575852',16,'2026-03-21 14:03:44.575852',3,3,4),('2025-03-22 01:37:38.713095',16,'2026-03-22 01:37:38.713095',4,3,5),('2025-03-27 20:27:59.056217',16,'2026-03-27 20:27:59.056217',4,3,6),('2025-03-26 23:09:17.530668',16,'2026-03-26 23:09:17.530668',4,3,7),('2025-03-26 23:11:04.959788',16,'2026-03-26 23:11:04.959788',4,3,8),('2025-03-26 23:12:19.717611',16,'2026-03-26 23:12:19.717611',4,3,9),('2025-03-26 23:31:53.785406',16,'2026-03-26 23:31:53.785406',4,3,14),('2025-03-26 23:40:26.292446',16,'2026-03-26 23:40:26.292446',4,3,16),('2025-03-26 23:43:33.343396',16,'2026-03-26 23:43:33.343396',4,3,17),('2025-03-26 23:48:47.469859',16,'2026-03-26 23:48:47.469859',4,3,20),('2025-03-27 20:54:54.973907',16,'2026-03-27 20:54:54.973907',6,3,21),('2025-03-30 12:01:57.394486',16,'2026-03-30 12:01:57.394486',4,3,23),('2025-03-19 13:30:44.920437',16,'2026-03-19 13:30:44.920459',3,4,2),('2025-03-24 11:51:15.270805',16,'2026-03-24 11:51:15.270805',4,4,3),('2025-03-21 14:03:44.585306',16,'2026-03-21 14:03:44.585306',3,4,4),('2025-03-22 01:37:38.716100',16,'2026-03-22 01:37:38.716100',4,4,5),('2025-03-27 20:27:59.056217',16,'2026-03-27 20:27:59.056217',4,4,6),('2025-03-26 23:09:17.536660',16,'2026-03-26 23:09:17.536660',4,4,7),('2025-03-26 23:11:04.966998',16,'2026-03-26 23:11:04.966998',4,4,8),('2025-03-26 23:12:19.724943',16,'2026-03-26 23:12:19.724943',4,4,9),('2025-03-26 23:31:53.796262',16,'2026-03-26 23:31:53.796262',4,4,14),('2025-03-26 23:40:26.318498',16,'2026-03-26 23:40:26.318498',4,4,16),('2025-03-26 23:43:33.376925',16,'2026-03-26 23:43:33.376925',4,4,17),('2025-03-26 23:48:47.481323',16,'2026-03-26 23:48:47.481323',4,4,20),('2025-03-27 20:54:54.989975',16,'2026-03-27 20:54:54.989975',6,4,21),('2025-03-30 12:01:57.407577',16,'2026-03-30 12:01:57.407577',4,4,23),('2025-03-19 13:30:44.923477',16,'2026-03-19 13:30:44.923499',3,5,2),('2025-03-24 11:51:15.274088',16,'2026-03-24 11:51:15.274088',4,5,3),('2025-03-21 14:03:44.585774',16,'2026-03-21 14:03:44.585774',3,5,4),('2025-03-22 01:37:38.718634',16,'2026-03-22 01:37:38.718634',4,5,5),('2025-03-27 20:27:59.056217',16,'2026-03-27 20:27:59.056217',4,5,6),('2025-03-26 23:09:17.543995',16,'2026-03-26 23:09:17.543995',4,5,7),('2025-03-26 23:11:04.975942',16,'2026-03-26 23:11:04.975942',4,5,8),('2025-03-26 23:12:19.732331',16,'2026-03-26 23:12:19.732331',4,5,9),('2025-03-26 23:27:39.720123',16,'2026-03-26 23:27:39.720123',4,5,10),('2025-03-26 23:29:01.068422',16,'2026-03-26 23:29:01.068422',4,5,11),('2025-03-26 23:31:53.804260',16,'2026-03-26 23:31:53.804260',4,5,14),('2025-03-26 23:40:26.325496',16,'2026-03-26 23:40:26.325496',4,5,16),('2025-03-26 23:43:33.383017',16,'2026-03-26 23:43:33.383017',4,5,17),('2025-03-26 23:45:15.867965',16,'2026-03-26 23:45:15.867965',4,5,18),('2025-03-26 23:47:18.544839',16,'2026-03-26 23:47:18.544839',4,5,19),('2025-03-26 23:48:47.490466',16,'2026-03-26 23:48:47.490466',4,5,20),('2025-03-27 20:54:54.989975',16,'2026-03-27 20:54:54.989975',6,5,21),('2025-03-30 11:55:47.192754',16,'2026-03-30 11:55:47.193766',4,5,22),('2025-03-30 12:01:57.419125',16,'2026-03-30 12:01:57.419125',4,5,23),('2025-03-19 13:30:44.926769',16,'2026-03-19 13:30:44.926811',3,6,2),('2025-03-24 11:51:15.277046',16,'2026-03-24 11:51:15.277046',4,6,3),('2025-03-21 14:03:44.602008',16,'2026-03-21 14:03:44.602008',3,6,4),('2025-03-22 01:37:38.722644',16,'2026-03-22 01:37:38.722644',4,6,5),('2025-03-27 20:27:59.056217',16,'2026-03-27 20:27:59.056217',4,6,6),('2025-03-26 23:09:17.550179',16,'2026-03-26 23:09:17.550179',4,6,7),('2025-03-26 23:11:04.982423',16,'2026-03-26 23:11:04.982423',4,6,8),('2025-03-26 23:12:19.741684',16,'2026-03-26 23:12:19.741684',4,6,9),('2025-03-26 23:27:39.727612',16,'2026-03-26 23:27:39.727612',4,6,10),('2025-03-26 23:29:01.073423',16,'2026-03-26 23:29:01.073423',4,6,11),('2025-03-26 23:31:53.814830',16,'2026-03-26 23:31:53.814830',4,6,14),('2025-03-26 23:40:26.330979',16,'2026-03-26 23:40:26.330979',4,6,16),('2025-03-26 23:43:33.388018',16,'2026-03-26 23:43:33.388018',4,6,17),('2025-03-26 23:45:15.880481',16,'2026-03-26 23:45:15.880481',4,6,18),('2025-03-26 23:48:47.500967',16,'2026-03-26 23:48:47.500967',4,6,20),('2025-03-27 20:54:55.005978',16,'2026-03-27 20:54:55.005978',6,6,21),('2025-03-30 11:55:47.205382',16,'2026-03-30 11:55:47.205382',4,6,22),('2025-03-30 12:01:57.432538',16,'2026-03-30 12:01:57.432538',4,6,23),('2025-03-19 13:30:44.931457',16,'2026-03-19 13:30:44.931505',3,7,2),('2025-03-24 11:51:15.277987',16,'2026-03-24 11:51:15.277987',4,7,3),('2025-03-21 14:03:44.602008',16,'2026-03-21 14:03:44.602008',3,7,4),('2025-03-22 01:37:38.724659',16,'2026-03-22 01:37:38.724659',4,7,5),('2025-03-27 20:27:59.066194',16,'2026-03-27 20:27:59.066194',4,7,6),('2025-03-26 23:09:17.556199',16,'2026-03-26 23:09:17.556199',4,7,7),('2025-03-26 23:11:04.989825',16,'2026-03-26 23:11:04.989825',4,7,8),('2025-03-26 23:12:19.750025',16,'2026-03-26 23:12:19.750025',4,7,9),('2025-03-26 23:31:53.825278',16,'2026-03-26 23:31:53.825278',4,7,14),('2025-03-26 23:40:26.340184',16,'2026-03-26 23:40:26.340184',4,7,16),('2025-03-26 23:43:33.393920',16,'2026-03-26 23:43:33.393920',4,7,17),('2025-03-26 23:48:47.512086',16,'2026-03-26 23:48:47.512086',4,7,20),('2025-03-27 20:54:55.010987',16,'2026-03-27 20:54:55.010987',6,7,21),('2025-03-30 12:01:57.443592',16,'2026-03-30 12:01:57.443592',4,7,23),('2025-03-19 13:30:44.935666',16,'2026-03-19 13:30:44.935691',3,8,2),('2025-03-24 11:51:15.279982',16,'2026-03-24 11:51:15.279982',4,8,3),('2025-03-21 14:03:44.618944',16,'2026-03-21 14:03:44.618944',3,8,4),('2025-03-22 01:37:38.727338',16,'2026-03-22 01:37:38.727338',4,8,5),('2025-03-27 20:27:59.066731',16,'2026-03-27 20:27:59.066731',4,8,6),('2025-03-26 23:09:17.563379',16,'2026-03-26 23:09:17.563379',4,8,7),('2025-03-26 23:11:04.996032',16,'2026-03-26 23:11:04.996032',4,8,8),('2025-03-26 23:12:19.760266',16,'2026-03-26 23:12:19.760266',4,8,9),('2025-03-26 23:27:39.735478',16,'2026-03-26 23:27:39.735478',4,8,10),('2025-03-26 23:29:01.078067',16,'2026-03-26 23:29:01.078067',4,8,11),('2025-03-26 23:31:53.833786',16,'2026-03-26 23:31:53.834778',4,8,14),('2025-03-26 23:40:26.350274',16,'2026-03-26 23:40:26.350274',4,8,16),('2025-03-26 23:43:33.399924',16,'2026-03-26 23:43:33.399924',4,8,17),('2025-03-26 23:45:15.891696',16,'2026-03-26 23:45:15.891696',4,8,18),('2025-03-26 23:48:47.523476',16,'2026-03-26 23:48:47.523476',4,8,20),('2025-03-27 20:54:55.020911',16,'2026-03-27 20:54:55.020911',6,8,21),('2025-03-30 11:55:47.214900',16,'2026-03-30 11:55:47.214900',4,8,22),('2025-03-30 12:01:57.451927',16,'2026-03-30 12:01:57.451927',4,8,23),('2025-03-19 13:31:05.521949',16,'2026-03-19 13:31:05.521972',3,9,2),('2025-03-24 11:51:15.280981',16,'2026-03-24 11:51:15.280981',4,9,3),('2025-03-21 14:03:44.618944',16,'2026-03-21 14:03:44.618944',3,9,4),('2025-03-22 01:37:38.729405',16,'2026-03-22 01:37:38.729405',4,9,5),('2025-03-27 20:27:59.066731',16,'2026-03-27 20:27:59.066731',4,9,6),('2025-03-26 23:09:17.569629',16,'2026-03-26 23:09:17.569629',4,9,7),('2025-03-26 23:11:05.002416',16,'2026-03-26 23:11:05.002416',4,9,8),('2025-03-26 23:12:19.768509',16,'2026-03-26 23:12:19.768509',4,9,9),('2025-03-26 23:31:53.844029',16,'2026-03-26 23:31:53.844029',4,9,14),('2025-03-26 23:40:26.360722',16,'2026-03-26 23:40:26.360722',4,9,16),('2025-03-26 23:43:33.408544',16,'2026-03-26 23:43:33.408544',4,9,17),('2025-03-26 23:48:47.534870',16,'2026-03-26 23:48:47.534870',4,9,20),('2025-03-27 20:54:55.021923',16,'2026-03-27 20:54:55.021923',6,9,21),('2025-03-30 12:01:57.461441',16,'2026-03-30 12:01:57.461441',4,9,23),('2025-03-19 13:30:44.939290',16,'2026-03-19 13:30:44.939316',3,10,2),('2025-03-24 11:51:15.282980',16,'2026-03-24 11:51:15.282980',4,10,3),('2025-03-21 14:03:44.618944',16,'2026-03-21 14:03:44.618944',3,10,4),('2025-03-22 01:37:38.732877',16,'2026-03-22 01:37:38.732877',4,10,5),('2025-03-27 20:27:59.066731',16,'2026-03-27 20:27:59.066731',4,10,6),('2025-03-26 23:11:05.011678',16,'2026-03-26 23:11:05.011678',4,10,8),('2025-03-26 23:12:19.776497',16,'2026-03-26 23:12:19.776497',4,10,9),('2025-03-26 23:31:53.851372',16,'2026-03-26 23:31:53.851372',4,10,14),('2025-03-26 23:43:33.413556',16,'2026-03-26 23:43:33.413556',4,10,17),('2025-03-27 20:54:55.037633',16,'2026-03-27 20:54:55.037633',6,10,21),('2025-03-30 12:01:57.471465',16,'2026-03-30 12:01:57.471465',4,10,23),('2025-03-22 01:00:35.788453',16,'2026-03-22 01:00:35.788453',4,11,2),('2025-03-24 11:51:15.284978',16,'2026-03-24 11:51:15.284978',4,11,3),('2025-03-22 01:00:35.808061',16,'2026-03-22 01:00:35.808061',4,11,4),('2025-03-27 20:27:59.066731',16,'2026-03-27 20:27:59.066731',4,11,6),('2025-03-26 23:31:53.861358',16,'2026-03-26 23:31:53.861358',4,11,14),('2025-03-26 23:43:33.420546',16,'2026-03-26 23:43:33.420546',4,11,17),('2025-03-27 20:54:55.038874',16,'2026-03-27 20:54:55.038874',6,11,21),('2025-03-30 12:01:57.484566',16,'2026-03-30 12:01:57.484566',4,11,23),('2025-03-22 01:06:46.750126',16,'2026-03-22 01:06:46.750126',4,12,2),('2025-03-24 11:51:15.285981',16,'2026-03-24 11:51:15.285981',4,12,3),('2025-03-22 01:06:46.776742',16,'2026-03-22 01:06:46.776742',4,12,4),('2025-03-27 20:27:59.066731',16,'2026-03-27 20:27:59.066731',4,12,6),('2025-03-26 23:31:53.868785',16,'2026-03-26 23:31:53.868785',4,12,14),('2025-03-26 23:43:33.425738',16,'2026-03-26 23:43:33.425738',4,12,17),('2025-03-27 20:54:55.056346',16,'2026-03-27 20:54:55.056346',6,12,21),('2025-03-30 12:01:57.493572',16,'2026-03-30 12:01:57.493572',4,12,23),('2025-03-22 01:36:08.957354',16,'2026-03-22 01:36:08.957354',4,13,2),('2025-03-24 11:51:15.287989',16,'2026-03-24 11:51:15.287989',4,13,3),('2025-03-22 01:36:08.990338',16,'2026-03-22 01:36:08.990338',4,13,4),('2025-03-27 20:27:59.066731',16,'2026-03-27 20:27:59.066731',4,13,6),('2025-03-26 23:31:53.876776',16,'2026-03-26 23:31:53.876776',4,13,14),('2025-03-26 23:43:33.431743',16,'2026-03-26 23:43:33.431743',4,13,17),('2025-03-27 20:54:55.060786',16,'2026-03-27 20:54:55.060786',6,13,21),('2025-03-30 12:01:57.507199',16,'2026-03-30 12:01:57.507199',4,13,23),('2025-03-22 01:06:49.756306',16,'2026-03-22 01:06:49.756306',4,14,2),('2025-03-24 11:51:15.291219',16,'2026-03-24 11:51:15.291219',4,14,3),('2025-03-22 01:06:49.767832',16,'2026-03-22 01:06:49.767832',4,14,4),('2025-03-27 20:27:59.066731',16,'2026-03-27 20:27:59.066731',4,14,6),('2025-03-26 23:31:53.886197',16,'2026-03-26 23:31:53.886197',4,14,14),('2025-03-26 23:43:33.435893',16,'2026-03-26 23:43:33.435893',4,14,17),('2025-03-26 23:47:18.554069',16,'2026-03-26 23:47:18.554069',4,14,19),('2025-03-27 20:54:55.070978',16,'2026-03-27 20:54:55.070978',6,14,21),('2025-03-30 12:01:57.515908',16,'2026-03-30 12:01:57.515908',4,14,23),('2025-03-22 01:24:03.787305',16,'2026-03-22 01:24:03.787305',4,16,2),('2025-03-24 11:51:15.294314',16,'2026-03-24 11:51:15.294314',4,16,3),('2025-03-22 01:24:03.834279',16,'2026-03-22 01:24:03.834279',4,16,4),('2025-03-27 20:27:59.081894',16,'2026-03-27 20:27:59.081894',4,16,6),('2025-03-26 23:31:53.903714',16,'2026-03-26 23:31:53.903714',4,16,14),('2025-03-26 23:43:33.445747',16,'2026-03-26 23:43:33.445747',4,16,17),('2025-03-27 20:54:55.088057',16,'2026-03-27 20:54:55.088057',6,16,21),('2025-03-30 12:01:57.536085',16,'2026-03-30 12:01:57.536085',4,16,23),('2025-03-22 01:36:10.519993',16,'2026-03-22 01:36:10.519993',4,17,2),('2025-03-24 11:51:15.296300',16,'2026-03-24 11:51:15.296300',4,17,3),('2025-03-22 01:36:10.541268',16,'2026-03-22 01:36:10.541268',4,17,4),('2025-03-27 20:27:59.083231',16,'2026-03-27 20:27:59.083231',4,17,6),('2025-03-26 23:31:53.914246',16,'2026-03-26 23:31:53.914246',4,17,14),('2025-03-26 23:43:33.450048',16,'2026-03-26 23:43:33.450048',4,17,17),('2025-03-27 20:54:55.108567',16,'2026-03-27 20:54:55.108567',6,17,21),('2025-03-30 12:01:57.544728',16,'2026-03-30 12:01:57.544728',4,17,23);
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

-- Dump completed on 2025-04-01  2:43:43
