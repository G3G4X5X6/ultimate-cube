/*
 Navicat Premium Data Transfer

 Source Server         : ultilmateshell
 Source Server Type    : SQLite
 Source Server Version : 3017000
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3017000
 File Encoding         : 65001

 Date: 29/08/2021 15:20:46
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for options
-- ----------------------------
DROP TABLE IF EXISTS "settings";
CREATE TABLE "settings" (
  "id" INTEGER NOT NULL,
  "key" TEXT NOT NULL,
  "value" TEXT NOT NULL,
  PRIMARY KEY ("id")
);

-- ----------------------------
-- Records of "options"
-- ----------------------------
INSERT INTO "settings" VALUES (1, 'theme', 48);
INSERT INTO "settings" VALUES (2, 'theme_enable', 0);

PRAGMA foreign_keys = true;
