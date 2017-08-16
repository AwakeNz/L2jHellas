SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `character_colors`
-- ----------------------------

CREATE TABLE `character_colors` (
  `char_name` varchar(35) NOT NULL,
  `color` int(8) NOT NULL,
  `reg_time` bigint(40) NOT NULL,
  `time` bigint(40) NOT NULL,
  PRIMARY KEY (`char_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=COMPRESSED COMMENT='L2jHellas Table';