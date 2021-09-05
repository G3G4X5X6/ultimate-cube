/*
 Navicat Premium Data Transfer

 Source Server         : ultilmateshell
 Source Server Type    : SQLite
 Source Server Version : 3017000
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3017000
 File Encoding         : 65001

 Date: 05/09/2021 14:30:39
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for note
-- ----------------------------
DROP TABLE IF EXISTS "note";
CREATE TABLE "note" (
  "id" INTEGER NOT NULL,
  "title" TEXT NOT NULL,
  "content" TEXT NOT NULL,
  "create_time" TEXT NOT NULL,
  "modify_time" TEXT NOT NULL,
  "comment" TEXT NOT NULL DEFAULT '没有备注哦',
  PRIMARY KEY ("id")
);

PRAGMA foreign_keys = true;
