drop schema if exists `spring_security`;
create schema `spring_security`;
use `spring_security`;
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `user_id`         int(10) unsigned NOT NULL AUTO_INCREMENT,
    `username`        varchar(1024)    NOT NULL unique ,
    `encode_password` varchar(1024)       NOT NULL,
    `permissions`             varchar(1000)           NOT NULL,
    PRIMARY KEY (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
DROP TABLE IF EXISTS `sys_projects`;
CREATE TABLE `sys_projects`
(
    `project_id`         int(10) unsigned NOT NULL AUTO_INCREMENT,
    `res_auth_id`        varchar(10) NOT NULL,
    `project_name`       varchar(1024)    NOT NULL unique ,
    PRIMARY KEY (`project_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
DROP TABLE IF EXISTS `oms_books`;
CREATE TABLE `oms_books`
(
    `book_id`         int(10) unsigned NOT NULL AUTO_INCREMENT,
    `res_auth_id`     varchar(10) NOT NULL,
    `book_name`       varchar(1024)    NOT NULL unique ,
    PRIMARY KEY (`book_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
DROP TABLE IF EXISTS `oms_items`;
CREATE TABLE `oms_items`
(
    `item_id`         int(10) unsigned NOT NULL AUTO_INCREMENT,
    `res_auth_id`     varchar(10) NOT NULL,
    `res_item_id`     varchar(10) NOT NULL,
    `item_name`       varchar(1024)    NOT NULL unique ,
    PRIMARY KEY (`item_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;