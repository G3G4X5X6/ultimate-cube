/*
 Navicat Premium Data Transfer

 Source Server         : ultimateshell
 Source Server Type    : SQLite
 Source Server Version : 3017000
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3017000
 File Encoding         : 65001

 Date: 01/09/2021 10:28:09
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for terminal_color
-- ----------------------------
DROP TABLE IF EXISTS "terminal_color";
CREATE TABLE "terminal_color" (
  "id" INTEGER NOT NULL,
  "foreground" TEXT NOT NULL,
  "background" TEXT NOT NULL,
  "theme" integer NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "theme" FOREIGN KEY ("theme") REFERENCES "theme" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);

PRAGMA foreign_keys = true;
