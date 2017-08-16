SET FOREIGN_KEY_CHECKS=0;
-- ---------------------------
-- Table structure for `character_macroses`
-- ---------------------------

CREATE TABLE `character_macroses` (
  `char_obj_id` int(11) NOT NULL DEFAULT '0',
  `id` int(11) NOT NULL DEFAULT '0',
  `icon` int(11) DEFAULT NULL,
  `name` varchar(40) DEFAULT NULL,
  `descr` varchar(80) DEFAULT NULL,
  `acronym` varchar(4) DEFAULT NULL,
  `commands` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`char_obj_id`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=COMPRESSED COMMENT='L2jHellas Table';