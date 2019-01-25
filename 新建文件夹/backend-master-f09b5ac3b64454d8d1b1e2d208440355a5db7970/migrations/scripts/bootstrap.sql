--
--    Copyright 2010-2016 the original author or authors.
--
--    Licensed under the Apache License, Version 2.0 (the "License");
--    you may not use this file except in compliance with the License.
--    You may obtain a copy of the License at
--
--       http://www.apache.org/licenses/LICENSE-2.0
--
--    Unless required by applicable law or agreed to in writing, software
--    distributed under the License is distributed on an "AS IS" BASIS,
--    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--    See the License for the specific language governing permissions and
--    limitations under the License.
--

-- // Bootstrap.sql

-- This is the only SQL script file that is NOT
-- a valid migration and will not be run or tracked
-- in the changelog.  There is no @UNDO section.

-- // Do I need this file?

-- New projects likely won't need this file.
-- Existing projects will likely need this file.
-- It's unlikely that this bootstrap should be run
-- in the production environment.

-- // Purpose

-- The purpose of this file is to provide a facility
-- to initialize the database to a state before MyBatis
-- SQL migrations were applied.  If you already have
-- a database in production, then you probably have
-- a script that you run on your developer machine
-- to initialize the database.  That script can now
-- be put in this bootstrap file (but does not have
-- to be if you are comfortable with your current process.

-- // Running

-- The bootstrap SQL is run with the "migrate bootstrap"
-- command.  It must be run manually, it's never run as
-- part of the regular migration process and will never
-- be undone. Variables (e.g. ${variable}) are still
-- parsed in the bootstrap SQL.

-- After the boostrap SQL has been run, you can then
-- use the migrations and the changelog for all future
-- database change management.

-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: server.autol.demo.chilunyc.com    Database: autol
-- ------------------------------------------------------
-- Server version	5.7.18

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `b_at_log`
--

DROP TABLE IF EXISTS `b_at_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `b_at_log` (
  `id` varchar(100) NOT NULL,
  `device_id` varchar(100) DEFAULT NULL,
  `phone_number` varchar(100) DEFAULT NULL,
  `generate_id` varchar(100) DEFAULT NULL,
  `type` varchar(30) DEFAULT NULL,
  `req_time` datetime DEFAULT NULL,
  `req_content` varchar(300) DEFAULT NULL,
  `resp_time` datetime DEFAULT NULL,
  `resp_content` varchar(3000) DEFAULT NULL,
  `resp_code` varchar(100) DEFAULT NULL,
  `resp_message` varchar(100) DEFAULT NULL,
  `exception` varchar(5010) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `b_config`
--

DROP TABLE IF EXISTS `b_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `b_config` (
  `key` varchar(100) NOT NULL,
  `value` varchar(100) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `b_custom`
--

DROP TABLE IF EXISTS `b_custom`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `b_custom` (
  `id` varchar(100) NOT NULL,
  `code` varchar(45) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `contact` varchar(100) DEFAULT NULL,
  `mobile` varchar(100) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `account` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `add_time` datetime DEFAULT NULL,
  `sms_push` int(11) DEFAULT NULL,
  `sms_alarm` int(11) DEFAULT NULL,
  `device_count` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `user_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`),
  UNIQUE KEY `idx_mobile` (`mobile`),
  UNIQUE KEY `idx_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `b_custom_type`
--

DROP TABLE IF EXISTS `b_custom_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `b_custom_type` (
  `custom_id` varchar(100) DEFAULT NULL,
  `type_code` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `b_device`
--

DROP TABLE IF EXISTS `b_device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `b_device` (
  `device_id` varchar(45) NOT NULL,
  `phone_number` varchar(100) DEFAULT NULL,
  `device_type` varchar(100) DEFAULT NULL,
  `manufacturer` varchar(45) DEFAULT NULL,
  `device_user` varchar(100) DEFAULT NULL,
  `area_id` varchar(100) DEFAULT NULL,
  `area_code` varchar(100) DEFAULT NULL,
  `local_address` varchar(100) DEFAULT NULL,
  `generate_id` varchar(100) DEFAULT NULL,
  `temperature` varchar(30) DEFAULT NULL,
  `working_state` int(11) DEFAULT NULL,
  `processing_state` int(11) DEFAULT NULL,
  `fuel_level_state` int(11) DEFAULT NULL,
  `fuel_pressure_state` int(11) DEFAULT NULL,
  `low_count_num` int(11) DEFAULT NULL,
  `loop_time` varchar(100) DEFAULT NULL,
  `stop_time` varchar(50) DEFAULT NULL,
  `run_time` varchar(50) DEFAULT NULL,
  `pressure_state` int(11) DEFAULT NULL,
  `pressure_time` varchar(100) DEFAULT NULL,
  `longitude` varchar(100) DEFAULT NULL,
  `longitude_degree` varchar(100) DEFAULT NULL,
  `longitude_minute` varchar(100) DEFAULT NULL,
  `longitude_second` varchar(100) DEFAULT NULL,
  `latitude` varchar(100) DEFAULT NULL,
  `latitude_degree` varchar(100) DEFAULT NULL,
  `latitude_minute` varchar(100) DEFAULT NULL,
  `latitude_second` varchar(100) DEFAULT NULL,
  `latest_time` datetime DEFAULT NULL,
  `p1` varchar(100) DEFAULT NULL,
  `p2` varchar(100) DEFAULT NULL,
  `p3` varchar(100) DEFAULT NULL,
  `p4` varchar(100) DEFAULT NULL,
  `version` varchar(100) DEFAULT NULL,
  `upload_period` varchar(100) DEFAULT NULL,
  `old_pressure_time` varchar(100) DEFAULT NULL,
  `pressure_minute` int(11) DEFAULT NULL,
  `pressure_second` int(11) DEFAULT NULL,
  `count_num` int(11) DEFAULT NULL,
  `tag` varchar(1000) DEFAULT NULL,
  `add_time` datetime DEFAULT NULL,
  `custom_id` varchar(100) DEFAULT NULL,
  `owner_time` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`device_id`),
  UNIQUE KEY `uk_mobile` (`phone_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `b_dict_area`
--

DROP TABLE IF EXISTS `b_dict_area`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `b_dict_area` (
  `id` varchar(100) DEFAULT NULL,
  `code` varchar(50) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `parent_code` varchar(50) DEFAULT NULL,
  `parent_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `b_dict_dtype`
--

DROP TABLE IF EXISTS `b_dict_dtype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `b_dict_dtype` (
  `id` varchar(100) DEFAULT NULL,
  `code` varchar(50) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `parent_code` varchar(50) DEFAULT NULL,
  `parent_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `b_dict_manuf`
--

DROP TABLE IF EXISTS `b_dict_manuf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `b_dict_manuf` (
  `id` varchar(100) DEFAULT NULL,
  `code` varchar(50) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `parent_code` varchar(50) DEFAULT NULL,
  `parent_id` varchar(100) DEFAULT NULL,
  `extra` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `b_notify`
--

DROP TABLE IF EXISTS `b_notify`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `b_notify` (
  `id` varchar(100) NOT NULL,
  `device_id` varchar(100) DEFAULT NULL,
  `type` varchar(30) DEFAULT NULL,
  `date_str` varchar(30) DEFAULT NULL,
  `content` varchar(200) DEFAULT NULL,
  `add_time` datetime DEFAULT NULL,
  `read_time` datetime DEFAULT NULL,
  `sms_send` int(11) DEFAULT NULL,
  `sms_mobile` varchar(100) DEFAULT NULL,
  `sms_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `b_owner_his`
--

DROP TABLE IF EXISTS `b_owner_his`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `b_owner_his` (
  `id` varchar(100) NOT NULL,
  `device_id` varchar(100) DEFAULT NULL,
  `custom_id` varchar(100) DEFAULT NULL,
  `from_time` datetime DEFAULT NULL,
  `to_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_api_log`
--

DROP TABLE IF EXISTS `sys_api_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_api_log` (
  `method` varchar(30) DEFAULT NULL,
  `from_ip` varchar(100) DEFAULT NULL,
  `token` varchar(200) DEFAULT NULL,
  `client_type` varchar(10) DEFAULT NULL,
  `user_type` varchar(10) DEFAULT NULL,
  `user_id` varchar(100) DEFAULT NULL,
  `req_time` datetime DEFAULT NULL,
  `req_content` varchar(1000) DEFAULT NULL,
  `resp_time` datetime DEFAULT NULL,
  `resp_content` varchar(5000) DEFAULT NULL,
  `resp_code` int(11) DEFAULT NULL,
  `resp_message` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_push_cid`
--

DROP TABLE IF EXISTS `sys_push_cid`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_push_cid` (
  `user_id` varchar(100) DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL,
  `cid` varchar(100) DEFAULT NULL,
  `plat` varchar(1000) DEFAULT NULL,
  UNIQUE KEY `idx_` (`user_id`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role` (
  `id` varchar(100) NOT NULL,
  `type` varchar(20) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `auth_list` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`type`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_token`
--

DROP TABLE IF EXISTS `sys_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_token` (
  `token` varchar(100) NOT NULL,
  `client_type` varchar(10) DEFAULT NULL,
  `client_key` varchar(100) DEFAULT NULL,
  `user_type` varchar(20) DEFAULT NULL,
  `user_id` varchar(100) DEFAULT NULL,
  `login_time` datetime DEFAULT NULL,
  PRIMARY KEY (`token`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user` (
  `id` varchar(100) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `mobile` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `role_id` varchar(100) DEFAULT NULL,
  `add_time` datetime DEFAULT NULL,
  `sms_push` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `custom` int(11) DEFAULT NULL,
  `custom_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`),
  UNIQUE KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-12-18  8:34:01
