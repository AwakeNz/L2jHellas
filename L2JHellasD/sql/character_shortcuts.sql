SET FOREIGN_KEY_CHECKS=0;
-- ---------------------------
-- Table structure for `character_shortcuts`
-- ---------------------------

CREATE TABLE `character_shortcuts` (
  `char_obj_id` decimal(11,0) NOT NULL DEFAULT '0',
  `slot` decimal(3,0) NOT NULL DEFAULT '0',
  `page` decimal(3,0) NOT NULL DEFAULT '0',
  `type` decimal(3,0) DEFAULT NULL,
  `shortcut_id` decimal(16,0) DEFAULT NULL,
  `level` varchar(4) DEFAULT NULL,
  `class_index` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`char_obj_id`,`slot`,`page`,`class_index`),
  KEY `shortcut_id` (`shortcut_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=COMPRESSED COMMENT='L2jHellas Table';