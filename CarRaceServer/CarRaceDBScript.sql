
drop database if exists carRaceDB ;
create database carRaceDB;
use carRaceDB;
drop table if exists UserBets;
drop table if exists Users;
drop table if exists History;
drop table if exists WaitingRaces;
drop table if exists Car;
create TABLE Car(Name varchar(30) not null primary key,TotalInvestment integer default 0,RaceId varchar(30));
create TABLE Users(UserName varchar(15) not null primary key,Password varchar(10) not null,Balance integer default 0);
create TABLE WaitingRaces(RaceId varchar(30),IdNumber integer not null primary key ,CarName1 varchar(30) not null,CarName2 varchar(30) not null,CarName3 varchar(30) not null,CarName4 varchar(30) not null,CarName5 varchar(30) not null,CashDeposit integer default 0);
create TABLE UserBets(UserName varchar(15) not null,CarName varchar(30),Stake integer DEFAULT 0,RaceId varchar(30),foreign key (UserName) references Users(UserName),foreign key (CarName) references Car(Name));
create TABLE History(RaceName varchar(30) not null primary key,RaceId varchar(30) ,WinnerCar varchar(30),Profit integer,TotalInvestment integer);
create TABLE RaceResult(CarName varchar(30) not null primary key,Place varchar(10) not null,RaceNumber integer not null,foreign key(CarName) references Car(Name));




