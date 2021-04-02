create database if not exists ims;
use ims;

drop table if exists CUSTOMER;
create table CUSTOMER (
    ID int auto_increment,
    NAME varchar(100),
    primary key (ID));

drop table if exists ITEM;
create table ITEM (
    ID int auto_increment,
    NAME varchar(100),
    COST double,
    primary key (ID));

drop table if exists ORDERS;
create table ORDERS (
    ID int auto_increment,
    CUSTOMER_ID int,
    primary key (ID),
    foreign key (CUSTOMER_ID) references CUSTOMER(ID));


drop table if exists ORDERED_ITEMS;
create table ORDERED_ITEMS (
    ORDER_ID int,
    ITEM_ID int,
    primary key (ORDER_ID, ITEM_ID));