/*
Navicat SQLite Data Transfer

Source Server         : mobile
Source Server Version : 30808
Source Host           : :0

Target Server Type    : SQLite
Target Server Version : 30808
File Encoding         : 65001

Date: 2017-08-12 07:58:58
*/

PRAGMA foreign_keys = OFF;

-- ----------------------------
-- Table structure for food_list
-- ----------------------------
DROP TABLE IF EXISTS "main"."food_list";
CREATE TABLE "food_list" (
"id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"food_name"  varchar(50),
"food_type"  INTEGER,
"ratings"  INTEGER,
"for_season"  INTEGER,
"for_week"  INTEGER
);
