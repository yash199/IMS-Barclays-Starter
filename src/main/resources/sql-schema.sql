create database if not exists ims;
use ims;

drop table if exists Customer;
create table Customer (
    id int auto_increment,
    name varchar(100),
    primary key (id, name));

drop table if exists Item;
create table Item (
    id int auto_increment,
    name varchar(100),
    cost double,
    primary key (id,name));

drop table if exists Orders;
create table Orders (
    id int auto_increment,
    name varchar(100),
    items varchar(10000),
    primary key (id,name));