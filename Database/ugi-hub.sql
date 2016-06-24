
DROP TABLE IF EXISTS `Groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Groups` (
  `Name` varchar(50) NOT NULL,
  PRIMARY KEY (`Name`)
) ;
/*ENGINE=InnoDB DEFAULT CHARSET=latin1;*/
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `UserGroup`
--

DROP TABLE IF EXISTS `UserGroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UserGroup` (
  `Groupname` varchar(50) NOT NULL,
  `Username` varchar(50) NOT NULL,
  `UserIP` int(10)  NOT NULL,
  `IsOwner` tinyint(1) NOT NULL,
  PRIMARY KEY (`Groupname`,`Username`,`UserIP`),
  CONSTRAINT `usergroup_ibfk_1` FOREIGN KEY (`Groupname`) REFERENCES `Groups` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `usergroup_ibfk_2` FOREIGN KEY (`Username`, `UserIP`) REFERENCES `Users` (`Name`, `IP`) ON DELETE CASCADE ON UPDATE CASCADE
) ;
/*ENGINE=InnoDB DEFAULT CHARSET=latin1;*/
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Users` (
  `Name` varchar(50) NOT NULL,
  `IP` int(11)  NOT NULL,
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Name`,`IP`)
) ;
/*ENGINE=InnoDB DEFAULT CHARSET=latin1;*/
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-06-10  1:03:00

