/*
 Navicat Premium Data Transfer

 Source Server         : UltimateShell
 Source Server Type    : SQLite
 Source Server Version : 3017000
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3017000
 File Encoding         : 65001

 Date: 21/08/2021 19:10:15
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

-- ----------------------------
-- Records of "session"
-- ----------------------------
INSERT INTO "session" VALUES (1, '192.168.83.137', 'SSH', '192.168.83.137', 22, 'password', 'root', 12345678, NULL, '会话标签', '2021-8-16 23:01', '2021-8-16 23:01', '2021-8-16 23:01', 'Nothing');
INSERT INTO "session" VALUES (2, '192.168.83.131', 'SSH', '192.168.83.131', 22, 'password', 'kali', 'kali', NULL, '会话标签/Linux', '2021-7-15 20:01', '2021-7-15 20:01', '2021-7-15 20:01', 'Your comment');

-- ----------------------------
-- Auto increment value for session
-- ----------------------------
UPDATE "sqlite_sequence" SET seq = 2 WHERE name = 'session';

PRAGMA foreign_keys = true;
