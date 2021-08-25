/*
 Navicat Premium Data Transfer

 Source Server         : UltimateShell
 Source Server Type    : SQLite
 Source Server Version : 3017000
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3017000
 File Encoding         : 65001

 Date: 21/08/2021 18:10:42
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for session
-- ----------------------------
DROP TABLE IF EXISTS "session";
CREATE TABLE "session" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "session_name" TEXT NOT NULL DEFAULT '新建会话',
  "protocol" TEXT NOT NULL DEFAULT 'SSH',
  "address" TEXT NOT NULL DEFAULT '127.0.0.1',
  "port" integer NOT NULL DEFAULT 22,
  "auth_type" TEXT NOT NULL DEFAULT 'password',
  "username" TEXT,
  "password" TEXT,
  "private_key" TEXT,
  "tags" TEXT NOT NULL DEFAULT '会话标签',
  "create_time" TEXT NOT NULL,
  "access_time" TEXT NOT NULL,
  "modified_time" TEXT NOT NULL,
  "comment" TEXT NOT NULL DEFAULT 'Your comment'
);

PRAGMA foreign_keys = true;
