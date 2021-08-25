/*
 Navicat Premium Data Transfer

 Source Server         : UltimateShell
 Source Server Type    : SQLite
 Source Server Version : 3017000
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3017000
 File Encoding         : 65001

 Date: 22/08/2021 14:12:38
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for relation
-- ----------------------------
DROP TABLE IF EXISTS "relation";
CREATE TABLE "relation" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "session" integer NOT NULL,
  "tag" integer NOT NULL,
  CONSTRAINT "session" FOREIGN KEY ("session") REFERENCES "session" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "tag" FOREIGN KEY ("tag") REFERENCES "tag" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);

PRAGMA foreign_keys = true;
