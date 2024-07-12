/* Copyright(C) 2007 National Institute of Informatics, All rights reserved.*/

DROP TABLE PAYMENT IF EXISTS;
DROP TABLE AVAILABLEQTY IF EXISTS;
DROP TABLE RESERVATION IF EXISTS;
DROP TABLE ROOM IF EXISTS;

CREATE TABLE PAYMENT(ROOMNUMBER VARCHAR(50) NOT NULL,STAYINGDATE VARCHAR(20) NOT NULL,AMOUNT INTEGER,STATUS VARCHAR(10),CONSTRAINT ROOMNUMBER_STAYINGDATE PRIMARY KEY(ROOMNUMBER,STAYINGDATE));
CREATE TABLE AVAILABLEQTY(DATE VARCHAR(20) NOT NULL PRIMARY KEY,QTY INTEGER);
CREATE TABLE RESERVATION(RESERVATIONNUMBER VARCHAR(50) NOT NULL PRIMARY KEY,STAYINGDATE VARCHAR(20),STATUS VARCHAR(10));
CREATE TABLE ROOM(ROOMNUMBER VARCHAR(50) NOT NULL PRIMARY KEY,STAYINGDATE VARCHAR(20));


INSERT INTO ROOM VALUES('1001','');
INSERT INTO ROOM VALUES('1002','');
INSERT INTO ROOM VALUES('1003','');
INSERT INTO ROOM VALUES('1004','');
INSERT INTO ROOM VALUES('1005','');
