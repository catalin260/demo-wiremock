-- MySQL dump 10.13  Distrib 8.0.20, for Linux (x86_64)
--
-- Host: localhost    Database: wiremock
-- ------------------------------------------------------
-- Server version	8.0.20-0ubuntu0.20.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `account_id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int unsigned NOT NULL,
  `account_currency` enum('RON','EUR','USD') NOT NULL,
  `account_created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `account_last_update` datetime DEFAULT NULL,
  `account_status` enum('active','inactive') NOT NULL DEFAULT 'active',
  `account_type` enum('CREDIT_CARD','DEBIT_CARD') NOT NULL,
  `account_ballance` decimal(12,3) NOT NULL DEFAULT '0.000',
  PRIMARY KEY (`account_id`),
  KEY `accounts_FK` (`user_id`),
  CONSTRAINT `accounts_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,1,'RON','2020-06-14 20:29:02',NULL,'active','DEBIT_CARD',999939.000),(2,2,'RON','2020-06-14 20:31:26',NULL,'active','DEBIT_CARD',1000059.000),(3,2,'RON','2020-06-14 20:31:49',NULL,'active','CREDIT_CARD',999999.000),(4,2,'EUR','2020-06-14 20:32:03',NULL,'active','CREDIT_CARD',999999.000),(5,1,'EUR','2020-06-14 20:32:10',NULL,'active','CREDIT_CARD',999999.000),(6,1,'EUR','2020-06-14 20:32:32',NULL,'active','DEBIT_CARD',999999.000);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `trans_id` int unsigned NOT NULL AUTO_INCREMENT,
  `account_id_src` int unsigned NOT NULL,
  `account_id_dest` int unsigned NOT NULL,
  `trans_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `trans_desc` varchar(100) DEFAULT NULL,
  `trans_amount` decimal(12,3) unsigned NOT NULL,
  `trans_status` enum('queue','done','error') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'queue',
  `trans_datetime_exec` datetime DEFAULT NULL,
  PRIMARY KEY (`trans_id`),
  KEY `transactions_FK` (`account_id_src`),
  KEY `transactions_FK_2` (`account_id_dest`),
  CONSTRAINT `transactions_FK` FOREIGN KEY (`account_id_src`) REFERENCES `accounts` (`account_id`) ON DELETE RESTRICT,
  CONSTRAINT `transactions_FK_2` FOREIGN KEY (`account_id_dest`) REFERENCES `accounts` (`account_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (3,1,2,'2020-06-14 21:54:05','O tranzactie',30.000,'done','2020-06-14 22:25:03'),(5,2,1,'2020-06-14 22:30:33','O tranzactie - returnata',30.000,'done','2020-06-14 22:30:34'),(6,2,1,'2020-06-14 22:30:54','O tranzactie - returnata',30.000,'done','2020-06-14 22:30:55'),(7,2,1,'2020-06-14 22:31:01','O tranzactie - returnata',30.000,'done','2020-06-14 22:31:02'),(8,1,2,'2020-06-14 22:31:15','O tranzactie - returnata',30.000,'done','2020-06-14 22:31:16'),(9,1,2,'2020-06-14 22:31:16','O tranzactie - returnata',30.000,'done','2020-06-14 22:31:17'),(10,1,2,'2020-06-14 22:31:16','O tranzactie - returnata',30.000,'done','2020-06-14 22:31:17'),(11,1,2,'2020-06-14 22:31:22','O tranzactie - returnata',30.000,'done','2020-06-14 22:31:23');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_username` varchar(10) NOT NULL,
  `user_password` tinytext NOT NULL,
  `user_status` enum('active','inactive') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'active',
  `user_date_add` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_last_login` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `users_UN` (`user_username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'catalin','9HzRXvP1Hu+88YH3BYTNaQ==','active','2020-06-14 19:20:54','2020-06-14 19:20:54'),(2,'john','9HzRXvP1Hu+88YH3BYTNaQ==','active','2020-06-14 20:31:07','2020-06-14 20:31:07');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'wiremock'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-06-14 22:34:53
