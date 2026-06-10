-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: garagemautobot
-- ------------------------------------------------------
-- Server version	8.0.43

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
-- Current Database: `garagemautobot`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `garagemautobot` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `garagemautobot`;

--
-- Table structure for table `agendamentos`
--

DROP TABLE IF EXISTS `agendamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agendamentos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_agendada` date NOT NULL,
  `data_criacao` datetime(6) NOT NULL,
  `observacoes` text,
  `os_gerada_id` bigint DEFAULT NULL,
  `periodo` enum('MANHA','TARDE') NOT NULL,
  `servico_solicitado` text NOT NULL,
  `status` enum('AGENDADO','CANCELADO','COMPARECEU','NAO_COMPARECEU') NOT NULL,
  `telefone_contato` varchar(255) DEFAULT NULL,
  `cliente_id` bigint NOT NULL,
  `veiculo_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7j02h7ufegbom5ke8ydlafx0x` (`cliente_id`),
  KEY `FK9ld6btog6l6ai5mq4hklwbjg5` (`veiculo_id`),
  CONSTRAINT `FK7j02h7ufegbom5ke8ydlafx0x` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`),
  CONSTRAINT `FK9ld6btog6l6ai5mq4hklwbjg5` FOREIGN KEY (`veiculo_id`) REFERENCES `veiculos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agendamentos`
--

LOCK TABLES `agendamentos` WRITE;
/*!40000 ALTER TABLE `agendamentos` DISABLE KEYS */;
INSERT INTO `agendamentos` VALUES (1,'2026-06-30','2026-06-02 00:57:52.267157','',4,'TARDE','Problema na corrente','COMPARECEU','4199806060',1,1),(2,'2026-06-02','2026-06-02 01:04:29.732816','Oferecer também um serviço de lavagem',5,'MANHA','Troca de pastilhas de freio','COMPARECEU','998167377',3,7),(3,'2026-06-05','2026-06-03 23:37:09.557418','vender um servico a parte',8,'MANHA','troca de oleo','COMPARECEU','4199806060',1,NULL),(4,'2026-06-04','2026-06-06 15:41:50.423623','teste',12,'MANHA','teste','NAO_COMPARECEU','419888711312',13,NULL),(5,'2026-06-04','2026-06-06 15:42:15.070194','testwe',NULL,'MANHA','teste','CANCELADO','4199803139',2,NULL);
/*!40000 ALTER TABLE `agendamentos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cep` varchar(255) DEFAULT NULL,
  `cidade` varchar(255) DEFAULT NULL,
  `cpf` varchar(11) NOT NULL,
  `data_cadastro` date DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `endereco` varchar(255) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `nome` varchar(255) NOT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  `ativo` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK7wflw78ibh162cmq12ii6ffly` (`cpf`),
  UNIQUE KEY `UK1c96wv36rk2hwui7qhjks3mvg` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES (1,'88706130','Araucaria','12036601979','2025-09-26','gabrielvezusonego1004@gmail.com','Rua andorinha','Paraná','Gabriel Vezu Sonego','4199806060',_binary ''),(2,'82781022','Araucaria','12036601980','2025-10-14','evandro@rodoluki.com','Rua andorinha','Paraná','Evandro Sonego','4199803139',_binary ''),(3,'82781022','Araucaria','82787077911','2025-10-14','patriciaaparecida@gmail.com','Rua andorinha','Paraná','Patricia Aparecida','998167377',_binary ''),(4,'75408140','Araucaria','00971245712','2025-10-14','pedrohenriquekaras@gmail.com','Rua Indepêndencia','Paraná','Pedro Henrique Karas','41995883695',_binary ''),(5,'65481128','Araucaria','98761231232','2025-10-15','gustavooliveira@gmail.com.br','Rua Japonesa','Paraná','Gustavo Oliveira','4199800000',_binary ''),(6,'83706170','Araucaria','82787077900','2025-10-15','patriciaaparecida1004@gmail.com','Rua Pardal','Paraná','Patricia Aparecida ','419980311455',_binary ''),(7,'84184091','Araucaria','82787077922','2025-10-17','luziaalvesvezu@gmail.com','Rua Sabia','Paraná','Luzia Alves Vezú','41999804312',_binary ''),(8,'83410185','Araucária','25186618914','2025-10-17','marcelodesouzafilho@gmail.com','Rua Pinguim','Paraná','Marcelo de Souza Filho','419888711311',_binary ''),(9,'83465412','Araucária','87382399122','2025-10-17','pedrokaras1004@gmail.com','Rua Curiru','Paraná','Pedro Henrique Karas','4199803115',_binary ''),(10,'72104123','Araucária','85320210101','2025-10-17','fernandamanica04@gmail.com','Rua Tiko','Paraná','Fernanda Manica','41998041122',_binary ''),(11,'51608120','Araucária','13052218734','2025-10-17','rosanetedasilva@gmail.com','Rua Pedro Krusk','Paraná','Rosanete dos Santos','419984112',_binary ''),(12,'76512901','Araucária','82787077977','2025-10-17','patriciaaparecidavezusonego@gmail.com','Avenida das Araucarias','PR','Patricia Aparecida','4199803111',_binary ''),(13,'76357124','Araucaria','28172311123','2025-11-17','lincongabriel01@gmail.com','Rua Pinguins','Paraná','Abraham Lincon','419888711312',_binary ''),(14,'83706130','Araucaria','12036689812','2026-03-28','andressaolvieiraarros@gmail.com','Rua sem saida','Paraná','Andressa de Oliveira Barros','4199980312',_binary ''),(15,'82630450','Curitiba','96255853039','2026-06-01','liz_rita_oliveira@dinamicaconsultoria.com','Rua Avicena','PR','Juliano Henrique da Silva','4138890220',_binary '');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fotos_veiculo`
--

DROP TABLE IF EXISTS `fotos_veiculo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fotos_veiculo` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_registro` datetime(6) NOT NULL,
  `legenda` varchar(255) DEFAULT NULL,
  `momento` enum('ENTRADA','SAIDA') NOT NULL,
  `nome_arquivo` varchar(255) NOT NULL,
  `os_id` bigint DEFAULT NULL,
  `veiculo_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK54wulfhfa5l5bbyh7dacfxxas` (`os_id`),
  KEY `FK6elqykx4bdhgrxx515lvk0tm3` (`veiculo_id`),
  CONSTRAINT `FK54wulfhfa5l5bbyh7dacfxxas` FOREIGN KEY (`os_id`) REFERENCES `ordens_servico` (`id`),
  CONSTRAINT `FK6elqykx4bdhgrxx515lvk0tm3` FOREIGN KEY (`veiculo_id`) REFERENCES `veiculos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fotos_veiculo`
--

LOCK TABLES `fotos_veiculo` WRITE;
/*!40000 ALTER TABLE `fotos_veiculo` DISABLE KEYS */;
INSERT INTO `fotos_veiculo` VALUES (1,'2026-06-01 12:25:48.431148','','ENTRADA','d9001279-cefb-4ec3-a70b-d50a859fd072.jpg',2,1),(2,'2026-06-01 12:25:48.435959','','ENTRADA','fbe0c3d5-e01d-4723-a8b8-01cf9962fb2f.jfif',2,1),(3,'2026-06-01 12:25:48.438295','','ENTRADA','8b9b3702-5888-4091-8148-d7cddd44bd75.jfif',2,1),(4,'2026-06-01 12:25:48.439568','','ENTRADA','da78e394-cf79-4e03-ba78-52528b20fc61.jfif',2,1),(5,'2026-06-01 12:25:48.443053','','ENTRADA','64968ab8-5cbc-40c6-986b-5cb8890d9b75.jfif',2,1),(6,'2026-06-05 00:39:44.202214','','ENTRADA','485eb14e-c79f-4a0e-8abb-5374b6dc9d0a.jfif',NULL,2),(7,'2026-06-06 14:47:04.179742','','ENTRADA','f7dfe2c7-393d-4683-90a1-b9f7b186e1d9.jfif',10,16),(8,'2026-06-06 15:10:50.816427','','SAIDA','cee657d9-b54d-4206-8e6f-c4a5b1f40e69.jfif',NULL,1),(9,'2026-06-06 15:32:09.591791','','SAIDA','213c1a2e-2000-4ec7-83d0-3c661ec87bf8.jfif',NULL,16),(10,'2026-06-06 15:32:09.613481','','SAIDA','97e439ee-2f4e-474a-aaa4-88c5f5cd1558.jfif',NULL,16),(11,'2026-06-06 15:32:09.616080','','SAIDA','54affaec-6fb7-41f8-b6d3-41262596d401.jfif',NULL,16),(12,'2026-06-06 15:32:09.618413','','SAIDA','9b25e95f-66d4-4f73-8a9b-a30e39c58c65.jfif',NULL,16),(13,'2026-06-06 23:23:16.141692','','ENTRADA','34a22c3e-02f2-4348-8019-8ececc116897.jfif',NULL,4),(14,'2026-06-06 23:23:20.786482','','SAIDA','da6b3f3c-bb75-43a8-b07c-a070f67c8d60.jfif',NULL,4),(15,'2026-06-07 00:19:08.918917','','SAIDA','dccd7519-fd8d-4202-937a-529c89ceda12.jfif',6,1);
/*!40000 ALTER TABLE `fotos_veiculo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `funcionarios`
--

DROP TABLE IF EXISTS `funcionarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `funcionarios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cep` varchar(255) DEFAULT NULL,
  `cidade` varchar(255) DEFAULT NULL,
  `cpf` varchar(11) NOT NULL,
  `data_admissao` date DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `endereco` varchar(255) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `idade` int DEFAULT NULL,
  `nome` varchar(255) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  `papel` enum('ADMIN','FUNCIONARIO') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKrrykmyf711s2n7maopibw47s0` (`cpf`),
  UNIQUE KEY `UKo60knm5wr28plxwo0clyg2eu4` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `funcionarios`
--

LOCK TABLES `funcionarios` WRITE;
/*!40000 ALTER TABLE `funcionarios` DISABLE KEYS */;
INSERT INTO `funcionarios` VALUES (1,NULL,NULL,'00000000000',NULL,'admin@mecanikas.com',NULL,NULL,30,'Administrador','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',NULL,'ADMIN'),(2,'12345678',NULL,'11111111111',NULL,'teste@mecanikas.com',NULL,NULL,30,'Administrador','$2a$10$3/o9Z7ynv2oig5kg4zeoN.xgN9d2j.cYdeGoVnfWAdK31BrGTzBsi','1199998888','ADMIN'),(3,'83704444',NULL,'82787077900',NULL,'gabrielvez004@gmail.com',NULL,NULL,25,'Marcelo Almeida','$2a$10$lqOi0tfg/mdNduABa.RheO9TeAX0e6WQf7HVN/XbhQ7CIIRomGFlG','4199803133','FUNCIONARIO');
/*!40000 ALTER TABLE `funcionarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `itens_os`
--

DROP TABLE IF EXISTS `itens_os`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `itens_os` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `preco_unitario_aplicado` double NOT NULL,
  `quantidade` int NOT NULL,
  `os_id` bigint NOT NULL,
  `peca_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3ja6jhruvbew10y7e3eoy4q7c` (`os_id`),
  KEY `FKe97meaiesdr0bppj1g9ccj5hk` (`peca_id`),
  CONSTRAINT `FK3ja6jhruvbew10y7e3eoy4q7c` FOREIGN KEY (`os_id`) REFERENCES `ordens_servico` (`id`),
  CONSTRAINT `FKe97meaiesdr0bppj1g9ccj5hk` FOREIGN KEY (`peca_id`) REFERENCES `pecas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `itens_os`
--

LOCK TABLES `itens_os` WRITE;
/*!40000 ALTER TABLE `itens_os` DISABLE KEYS */;
INSERT INTO `itens_os` VALUES (1,72,1,1,2),(2,42,1,2,1),(3,42,1,5,1),(5,885,9,7,3),(6,42,1,8,1),(8,42,1,10,1),(9,72,1,10,2);
/*!40000 ALTER TABLE `itens_os` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ordens_servico`
--

DROP TABLE IF EXISTS `ordens_servico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ordens_servico` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_abertura` datetime(6) NOT NULL,
  `data_conclusao` date DEFAULT NULL,
  `descricao_problema` text NOT NULL,
  `mao_obra_por_percentual` bit(1) DEFAULT NULL,
  `numero_os` varchar(255) NOT NULL,
  `observacoes` text,
  `percentual_mao_obra` double DEFAULT NULL,
  `status` enum('ABERTA','AGUARDANDO_PECA','CANCELADA','CONCLUIDA','EM_ANDAMENTO') NOT NULL,
  `valor_mao_obra` double DEFAULT NULL,
  `veiculo_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK38vkgxu47h79on0qts60pty05` (`numero_os`),
  KEY `FKf1kufpc9s3ekh2y4nfinyn0ji` (`veiculo_id`),
  CONSTRAINT `FKf1kufpc9s3ekh2y4nfinyn0ji` FOREIGN KEY (`veiculo_id`) REFERENCES `veiculos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ordens_servico`
--

LOCK TABLES `ordens_servico` WRITE;
/*!40000 ALTER TABLE `ordens_servico` DISABLE KEYS */;
INSERT INTO `ordens_servico` VALUES (1,'2026-05-31 22:51:58.021099','2026-05-31','teste',_binary '\0','OS-0001','teste',NULL,'CONCLUIDA',150,3),(2,'2026-06-01 12:22:50.022992','2026-06-01','Troca de óleo e troca do filtro do ar condicionado',_binary '','OS-0002','Troca do filtro\r\nVer se o cliente quer trocar filtro de óleo também.',10,'CONCLUIDA',NULL,1),(3,'2026-06-01 21:36:42.981386',NULL,'Troca de freio\r\n',_binary '','OS-0003','',10,'ABERTA',NULL,7),(4,'2026-06-02 00:58:13.280142',NULL,'Problema na corrente',_binary '\0','OS-0004','',NULL,'ABERTA',NULL,1),(5,'2026-06-02 01:04:44.941003',NULL,'Troca de pastilhas de freio',_binary '','OS-0005','Oferecer também um serviço de lavagem',11,'EM_ANDAMENTO',NULL,7),(6,'2026-06-02 01:43:07.825574',NULL,'.',_binary '\0','OS-0006','.',NULL,'EM_ANDAMENTO',NULL,1),(7,'2026-06-03 23:40:38.536484','2026-06-03','Troca de filtro de ar condicionado',_binary '','OS-0007','Troca das palhetas',5,'CONCLUIDA',NULL,15),(8,'2026-06-03 23:47:20.674035',NULL,'troca de oleo',_binary '\0','OS-0008','vender um servico a parte',NULL,'AGUARDANDO_PECA',400,3),(9,'2026-06-05 00:39:32.129581',NULL,'TEste',_binary '\0','OS-0009','testetes',NULL,'ABERTA',NULL,1),(10,'2026-06-06 14:46:08.701197','2026-06-06','testestrstrstrstrst',_binary '','OS-0010','testre',10.1,'AGUARDANDO_PECA',NULL,16),(11,'2026-06-06 14:49:31.994352',NULL,'teste',_binary '\0','OS-0011','teste',NULL,'ABERTA',NULL,5),(12,'2026-06-06 15:41:57.648242',NULL,'teste',_binary '\0','OS-0012','teste',NULL,'ABERTA',NULL,11);
/*!40000 ALTER TABLE `ordens_servico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pecas`
--

DROP TABLE IF EXISTS `pecas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pecas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `codigo` varchar(255) NOT NULL,
  `data_cadastro` datetime(6) NOT NULL,
  `nome` varchar(255) NOT NULL,
  `preco_unitario` double NOT NULL,
  `quantidade` int NOT NULL,
  `ativo` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK8cpg4nf7cm5camxy7rd3jakok` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pecas`
--

LOCK TABLES `pecas` WRITE;
/*!40000 ALTER TABLE `pecas` DISABLE KEYS */;
INSERT INTO `pecas` VALUES (1,'1A','2026-03-28 23:39:26.264207','Oleo Motorcraft 5w30 Ford Sintético',42,0,_binary ''),(2,'1B','2026-06-02 01:43:55.104341','Par Terminal De Direção Escort Xr3',72,4,_binary ''),(3,'34','2026-06-07 20:27:39.737342','Kit 4 Pneu Aro 13 Westlake Zuper Eco',885,0,_binary '\0');
/*!40000 ALTER TABLE `pecas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pecas_usadas`
--

DROP TABLE IF EXISTS `pecas_usadas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pecas_usadas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_uso` datetime(6) NOT NULL,
  `quantidade_usada` int NOT NULL,
  `valor_unitario_aplicado` double NOT NULL,
  `peca_id` bigint NOT NULL,
  `veiculo_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgnlsklo3u7r2vg5cwimtjogt7` (`peca_id`),
  KEY `FKjmb7de3fcifjqgi6k1b94byty` (`veiculo_id`),
  CONSTRAINT `FKgnlsklo3u7r2vg5cwimtjogt7` FOREIGN KEY (`peca_id`) REFERENCES `pecas` (`id`),
  CONSTRAINT `FKjmb7de3fcifjqgi6k1b94byty` FOREIGN KEY (`veiculo_id`) REFERENCES `veiculos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pecas_usadas`
--

LOCK TABLES `pecas_usadas` WRITE;
/*!40000 ALTER TABLE `pecas_usadas` DISABLE KEYS */;
/*!40000 ALTER TABLE `pecas_usadas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `veiculo_peca`
--

DROP TABLE IF EXISTS `veiculo_peca`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `veiculo_peca` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quantidade_usada` int NOT NULL,
  `peca_id` bigint NOT NULL,
  `veiculo_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4i0maooo914ps9omd735yvhau` (`peca_id`),
  KEY `FK5q1sbi202mdsbkj6ycwyje53f` (`veiculo_id`),
  CONSTRAINT `FK4i0maooo914ps9omd735yvhau` FOREIGN KEY (`peca_id`) REFERENCES `pecas` (`id`),
  CONSTRAINT `FK5q1sbi202mdsbkj6ycwyje53f` FOREIGN KEY (`veiculo_id`) REFERENCES `veiculos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `veiculo_peca`
--

LOCK TABLES `veiculo_peca` WRITE;
/*!40000 ALTER TABLE `veiculo_peca` DISABLE KEYS */;
INSERT INTO `veiculo_peca` VALUES (1,1,1,1),(2,1,3,9),(3,2,1,2),(4,1,2,10),(5,1,1,7),(6,1,1,1);
/*!40000 ALTER TABLE `veiculo_peca` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `veiculos`
--

DROP TABLE IF EXISTS `veiculos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `veiculos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ano` varchar(4) NOT NULL,
  `caminho_foto` varchar(255) DEFAULT NULL,
  `data_cadastro` date DEFAULT NULL,
  `marca` varchar(255) NOT NULL,
  `modelo` varchar(255) NOT NULL,
  `placa` varchar(7) NOT NULL,
  `status` enum('AGUARDANDO_PECA','EM_MANUTENCAO','MANUTENCAO_FINALIZADA') NOT NULL,
  `cliente_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKti2seg7u1j0clbvwol5x11jmv` (`placa`),
  KEY `FKo6t7kavymtdqlsm2ytuyvhor2` (`cliente_id`),
  CONSTRAINT `FKo6t7kavymtdqlsm2ytuyvhor2` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `veiculos`
--

LOCK TABLES `veiculos` WRITE;
/*!40000 ALTER TABLE `veiculos` DISABLE KEYS */;
INSERT INTO `veiculos` VALUES (1,'2011',NULL,'2026-05-31','Volksewagen','Gol','ALJ-971','EM_MANUTENCAO',1),(2,'2025','20f7ee32-bb7f-4360-8215-5569437f78da_VOLKSWAGEN_NIVUS_1.0_200_TSI_TOTAL_FLEX_HIGHLINE_AUTOMATICO_34839517523485623.webp','2025-10-02','Volksewagen','Nivus','ART9870','MANUTENCAO_FINALIZADA',1),(3,'2002','4a238aa5-efc1-4730-b879-23626d25e002_download.jfif','2025-10-14','Volksewagen','Gol','ALK9871','AGUARDANDO_PECA',1),(4,'2014',NULL,'2026-06-03','Citroen','C4 LOUNge','AXD1703','AGUARDANDO_PECA',1),(5,'2010','38f6b627-1b37-4815-b1b1-4ef535a4f863_download.jfif','2025-10-14','Fiat','Siena HLX','ATN0957','EM_MANUTENCAO',1),(6,'1991','68636500-b85d-4827-9119-2511f06ea9c7_download (2).jfif','2025-10-14','Ford','Versailles GL 2.0','AQB8688','AGUARDANDO_PECA',1),(7,'1991','b58c2bdb-57e8-4040-8aa3-0b71ad5c59d9_download (3).jfif','2025-10-14','Cadillac','Seville 4.6','AZN1406','EM_MANUTENCAO',3),(8,'2016','d0837620-f5de-4c55-a1c3-b4224e903540_download (4).jfif','2025-10-15','Jeep','Renegade Sport 1.8','JTE-119','AGUARDANDO_PECA',5),(9,'2020',NULL,'2026-06-01','Volksewagen','T-cross','BEE5B72','EM_MANUTENCAO',6),(10,'2011','c5ef9ffe-b222-47c5-b263-6b231c89472f_13002_1.jpg','2025-10-28','Citroen','Jumper 2.3 Vetrato','AZH4719','AGUARDANDO_PECA',10),(11,'2014','1ab3a8e9-4a3a-4e9c-a896-18c99a294e9a_download.jpg','2025-11-17','Citroen','C3 Picasso Tendance 1.6 Flex ','AKX-375','EM_MANUTENCAO',13),(12,'2000','29996905-85c3-4f44-b350-f1922f59af40_gol3.jfif','2026-06-02','teste','teste','RRR4444','EM_MANUTENCAO',1),(13,'2002','74d13831-f9c4-4a85-b785-b5f8ae4b1b41_gol1.jpg','2026-06-02','Teste1','teste','ART9880','EM_MANUTENCAO',14),(14,'2012','1be3a033-8c01-464a-9622-d9371d85f684_gol5.jfif','2026-06-02','Citroen','C3 Picasso Excl. 1.6 Flex 16V 5p Aut.','MYO7085','EM_MANUTENCAO',8),(15,'2002',NULL,'2026-06-03','Kia','Soul','ADE8878','MANUTENCAO_FINALIZADA',11),(16,'2002',NULL,'2026-06-06','Fiat','Nivus','ART9844','AGUARDANDO_PECA',13);
/*!40000 ALTER TABLE `veiculos` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-07 17:58:04
