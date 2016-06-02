CREATE DATABASE IF NOT EXISTS Hadoop;
USE Hadoop;
CREATE TABLE Groups (
	Name varchar(50) NOT NULL PRIMARY KEY
);
CREATE TABLE Users (
	Name varchar(50) NOT NULL,
	IP Int UNSIGNED,
	CreateTime Timestamp,
	PRIMARY KEY (Name, IP)
);
CREATE TABLE UserGroup (
	Groupname varchar(50),
	Username varchar(50),
	UserIP Int UNSIGNED,
	PRIMARY KEY (Groupname, Username, UserIP),
	FOREIGN KEY (Groupname) references Groups(Name),
	FOREIGN KEY (Username, UserIP) references Users(Name, IP)
);