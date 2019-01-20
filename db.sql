CREATE DATABASE `buslogging` ;


CREATE TABLE `qbuzzlog` (
  
 `idqbuzzlog` int(11) NOT NULL AUTO_INCREMENT,
  
 `logrecord` varchar(1024) DEFAULT NULL,
  
 PRIMARY KEY (`idqbuzzlog`)
) 
 ENGINE=InnoDB AUTO_INCREMENT=1  DEFAULT CHARSET=utf8;
