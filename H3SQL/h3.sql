CREATE TABLE IF NOT EXISTS `Account`(
   `id` BIGINT NOT NULL AUTO_INCREMENT,
   `uid` VARCHAR(64) NOT NULL,
   `pwd` VARCHAR(64) NOT NULL,
   `salt` VARCHAR(64) NOT NULL,
   `git_account` VARCHAR(128),
   `git_pwd` VARCHAR(128),
   `git_opened` SMALLINT NOT NULL DEFAULT 0,
   `status` SMALLINT NOT NULL DEFAULT 1,
   `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
   `modify_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
   `sys_ver` INT NOT NULL DEFAULT 0,
   PRIMARY KEY (`id`),
   UNIQUE KEY (`uid`),
   KEY (create_time),
   KEY (modify_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `OpenIds`(
 `id` BIGINT NOT NULL AUTO_INCREMENT,
 `open_id` VARCHAR(128),
 `id_type` SMALLINT NOT NULL,
 `uid` VARCHAR(64) NOT NULL,
 `status` SMALLINT NOT NULL DEFAULT 1,
 `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
 `modify_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
 `sys_ver` INT NOT NULL DEFAULT 0, 
 PRIMARY KEY (`id`),
 UNIQUE KEY (`open_id`,`id_type`),
 KEY (create_time),
 KEY (modify_time) 
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `keyVault`(
`key_name` VARCHAR(64) NOT NULL,
`key_salt` VARCHAR(4096) NOT NULL,
`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
`modify_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
 PRIMARY KEY (`key_name`),
 KEY (create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `HomeSpace`(
`id` BIGINT NOT NULL AUTO_INCREMENT,
`home_name` VARCHAR(64) NOT NULL,
`home_salt` VARCHAR(4096) NOT NULL,
`uid` VARCHAR(64) NOT NULL,
`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
`modify_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
PRIMARY KEY (`id`),
UNIQUE KEY (`uid`,`home_name`),
KEY (create_time),
KEY (modify_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


