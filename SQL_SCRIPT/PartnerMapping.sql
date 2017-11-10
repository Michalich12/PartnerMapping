--
-- Скрипт сгенерирован Devart dbForge Studio for SQL Server, Версия 5.4.275.0
-- Домашняя страница продукта: http://devart.com/ru/dbforge/sql/studio
-- Дата скрипта: 09.11.2017 20:10:21
-- Версия сервера: 11.00.2100
-- Версия клиента: 
--
USE master
GO

IF DB_NAME() <> N'master' SET NOEXEC ON

CREATE DATABASE [PartnerMapping]
  COLLATE Cyrillic_General_CI_AS
GO


USE PartnerMapping
GO

IF DB_NAME() <> N'PartnerMapping' SET NOEXEC ON
GO

--
-- Создать таблицу [dbo].[PartnerMapping]
--
PRINT (N'Создать таблицу [dbo].[PartnerMapping]')
GO
CREATE TABLE dbo.PartnerMapping (
  ID bigint IDENTITY,
  CustomerID int NOT NULL,
  ClientID nvarchar(50) NOT NULL,
  FullName nvarchar(250) NOT NULL,
  Avatar nvarchar(250) NULL,
  AccountID nvarchar(50) NOT NULL,
  CONSTRAINT PK_PartnerMapping_ID PRIMARY KEY CLUSTERED (ID),
  UNIQUE (ClientID)
)
ON [PRIMARY]
GO

--
-- Создать таблицу [dbo].[Customer]
--
PRINT (N'Создать таблицу [dbo].[Customer]')
GO
CREATE TABLE dbo.Customer (
  ID int IDENTITY,
  Login nvarchar(50) NOT NULL,
  Pass nvarchar(50) NOT NULL,
  FullName nvarchar(250) NOT NULL,
  Balance decimal(19, 4) NOT NULL CONSTRAINT DF__Customer__Balanc__1920BF5C DEFAULT (0),
  Status bit NOT NULL CONSTRAINT DF__Customer__Status__1A14E395 DEFAULT (1),
  CONSTRAINT PK_Customer_ID PRIMARY KEY CLUSTERED (ID),
  UNIQUE (Login)
)
ON [PRIMARY]
GO

--
-- Создать пользователя [test]
--
PRINT (N'Создать пользователя [test]')
GO
CREATE USER test
  WITHOUT LOGIN
GO
-- 
-- Вывод данных для таблицы Customer
--
SET IDENTITY_INSERT dbo.Customer ON
GO
INSERT dbo.Customer(ID, Login, Pass, FullName, Balance, Status) VALUES (1, N'Ivanov', N'12345', N'Иванов Иван Иванович', 10.0000, CONVERT(bit, 'True'))
INSERT dbo.Customer(ID, Login, Pass, FullName, Balance, Status) VALUES (2, N'Petrov', N'123', N'Петров Петр Петрович', 0.0000, CONVERT(bit, 'True'))
INSERT dbo.Customer(ID, Login, Pass, FullName, Balance, Status) VALUES (3, N'Sidorov', N'12345', N'Сидоров Сидр Сидорович', 0.0000, CONVERT(bit, 'True'))
GO
SET IDENTITY_INSERT dbo.Customer OFF
GO
-- 
-- Вывод данных для таблицы PartnerMapping
--
SET IDENTITY_INSERT dbo.PartnerMapping ON
GO
INSERT dbo.PartnerMapping(ID, CustomerID, ClientID, FullName, Avatar, AccountID) VALUES (8, 2, N'fdfg', N'5545 ggf', N'http://rertert', N'443534gdfg')
INSERT dbo.PartnerMapping(ID, CustomerID, ClientID, FullName, Avatar, AccountID) VALUES (23, 1, N'APPVC25478', N'Иванов', N'https://upload.wikimedia.org/wikipedia/en/b/b0/Avatar-Teaser-Poster.jpg', N'id1111111')
GO
SET IDENTITY_INSERT dbo.PartnerMapping OFF
GO

USE PartnerMapping
GO

IF DB_NAME() <> N'PartnerMapping' SET NOEXEC ON
GO

--
-- Создать внешний ключ [FK_PartnerMapping_CustomerID] для объекта типа таблица [dbo].[PartnerMapping]
--
PRINT (N'Создать внешний ключ [FK_PartnerMapping_CustomerID] для объекта типа таблица [dbo].[PartnerMapping]')
GO
ALTER TABLE dbo.PartnerMapping
  ADD CONSTRAINT FK_PartnerMapping_CustomerID FOREIGN KEY (CustomerID) REFERENCES dbo.Customer (ID)
GO
SET NOEXEC OFF
GO