/*
 Navicat Premium Data Transfer

 Source Server         : ultilmateshell
 Source Server Type    : SQLite
 Source Server Version : 3017000
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3017000
 File Encoding         : 65001

 Date: 29/08/2021 11:26:22
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for theme
-- ----------------------------
DROP TABLE IF EXISTS "theme";
CREATE TABLE "theme" (
  "id" INTEGER NOT NULL,
  "name" TEXT NOT NULL,
  "class" TEXT NOT NULL,
  PRIMARY KEY ("id")
);

-- ----------------------------
-- Records of "theme"
-- ----------------------------
INSERT INTO "theme" VALUES (1, 'Arc', 'com.formdev.flatlaf.intellijthemes.FlatArcIJTheme');
INSERT INTO "theme" VALUES (2, 'Arc - Orange', 'com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme');
INSERT INTO "theme" VALUES (3, 'Arc Dark', 'com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme');
INSERT INTO "theme" VALUES (4, 'Arc Dark - Orange', 'com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme');
INSERT INTO "theme" VALUES (5, 'Carbon', 'com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme');
INSERT INTO "theme" VALUES (6, 'Cobalt 2', 'com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme');
INSERT INTO "theme" VALUES (7, 'Cyan light', 'com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme');
INSERT INTO "theme" VALUES (8, 'Dark Flat', 'com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme');
INSERT INTO "theme" VALUES (9, 'Dark purple', 'com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme');
INSERT INTO "theme" VALUES (10, 'Dracula', 'com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme');
INSERT INTO "theme" VALUES (11, 'Gradianto Dark Fuchsia', 'com.formdev.flatlaf.intellijthemes.FlatGradiantoDarkFuchsiaIJTheme');
INSERT INTO "theme" VALUES (12, 'Gradianto Deep Ocean', 'com.formdev.flatlaf.intellijthemes.FlatGradiantoDeepOceanIJTheme');
INSERT INTO "theme" VALUES (13, 'Gradianto Midnight Blue', 'com.formdev.flatlaf.intellijthemes.FlatGradiantoMidnightBlueIJTheme');
INSERT INTO "theme" VALUES (14, 'Gradianto Nature Green', 'com.formdev.flatlaf.intellijthemes.FlatGradiantoNatureGreenIJTheme');
INSERT INTO "theme" VALUES (15, 'Gray', 'com.formdev.flatlaf.intellijthemes.FlatGrayIJTheme');
INSERT INTO "theme" VALUES (16, 'Gruvbox Dark Hard', 'com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkHardIJTheme');
INSERT INTO "theme" VALUES (17, 'Gruvbox Dark Medium', 'com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkMediumIJTheme');
INSERT INTO "theme" VALUES (18, 'Gruvbox Dark Soft', 'com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkSoftIJTheme');
INSERT INTO "theme" VALUES (19, 'Hiberbee Dark', 'com.formdev.flatlaf.intellijthemes.FlatHiberbeeDarkIJTheme');
INSERT INTO "theme" VALUES (20, 'High contrast', 'com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme');
INSERT INTO "theme" VALUES (21, 'Light Flat', 'com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme');
INSERT INTO "theme" VALUES (22, 'Material Design Dark', 'com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme');
INSERT INTO "theme" VALUES (23, 'Monocai', 'com.formdev.flatlaf.intellijthemes.FlatMonocaiIJTheme');
INSERT INTO "theme" VALUES (24, 'Nord', 'com.formdev.flatlaf.intellijthemes.FlatNordIJTheme');
INSERT INTO "theme" VALUES (25, 'One Dark', 'com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme');
INSERT INTO "theme" VALUES (26, 'Solarized Dark', 'com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme');
INSERT INTO "theme" VALUES (27, 'Solarized Light', 'com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme');
INSERT INTO "theme" VALUES (28, 'Spacegray', 'com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme');
INSERT INTO "theme" VALUES (29, 'Vuesion', 'com.formdev.flatlaf.intellijthemes.FlatVuesionIJTheme');
INSERT INTO "theme" VALUES (30, 'Arc Dark (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkIJTheme');
INSERT INTO "theme" VALUES (31, 'Arc Dark Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkContrastIJTheme');
INSERT INTO "theme" VALUES (32, 'Atom One Dark (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme');
INSERT INTO "theme" VALUES (33, 'Atom One Dark (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme');
INSERT INTO "theme" VALUES (34, 'Atom One Dark Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkContrastIJTheme');
INSERT INTO "theme" VALUES (35, 'Atom One Light (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightIJTheme');
INSERT INTO "theme" VALUES (36, 'Atom One Light Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightContrastIJTheme');
INSERT INTO "theme" VALUES (37, 'Dracula (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaIJTheme');
INSERT INTO "theme" VALUES (38, 'Dracula Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaContrastIJTheme');
INSERT INTO "theme" VALUES (39, 'GitHub (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubIJTheme');
INSERT INTO "theme" VALUES (40, 'GitHub Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubContrastIJTheme');
INSERT INTO "theme" VALUES (41, 'GitHub Dark (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme');
INSERT INTO "theme" VALUES (42, 'GitHub Dark Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkContrastIJTheme');
INSERT INTO "theme" VALUES (43, 'Light Owl (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatLightOwlIJTheme');
INSERT INTO "theme" VALUES (44, 'Light Owl Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatLightOwlContrastIJTheme');
INSERT INTO "theme" VALUES (45, 'Material Darker (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme');
INSERT INTO "theme" VALUES (46, 'Material Darker Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerContrastIJTheme');
INSERT INTO "theme" VALUES (47, 'Material Deep Ocean (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanIJTheme');
INSERT INTO "theme" VALUES (48, 'Material Deep Ocean Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanContrastIJTheme');
INSERT INTO "theme" VALUES (49, 'Material Lighter (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterIJTheme');
INSERT INTO "theme" VALUES (50, 'Material Lighter Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterContrastIJTheme');
INSERT INTO "theme" VALUES (51, 'Material Oceanic (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicIJTheme');
INSERT INTO "theme" VALUES (52, 'Material Oceanic Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicContrastIJTheme');
INSERT INTO "theme" VALUES (53, 'Material Palenight (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightIJTheme');
INSERT INTO "theme" VALUES (54, 'Material Palenight Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightContrastIJTheme');
INSERT INTO "theme" VALUES (55, 'Monokai Pro (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMonokaiProIJTheme');
INSERT INTO "theme" VALUES (56, 'Monokai Pro Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMonokaiProContrastIJTheme');
INSERT INTO "theme" VALUES (57, 'Moonlight (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightIJTheme');
INSERT INTO "theme" VALUES (58, 'Moonlight Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightContrastIJTheme');
INSERT INTO "theme" VALUES (59, 'Night Owl (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatNightOwlIJTheme');
INSERT INTO "theme" VALUES (60, 'Night Owl Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatNightOwlContrastIJTheme');
INSERT INTO "theme" VALUES (61, 'Solarized Dark (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedDarkIJTheme');
INSERT INTO "theme" VALUES (62, 'Solarized Dark Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedDarkContrastIJTheme');
INSERT INTO "theme" VALUES (63, 'Solarized Light (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedLightIJTheme');
INSERT INTO "theme" VALUES (64, 'Solarized Light Contrast (Material)', 'com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedLightContrastIJTheme');
