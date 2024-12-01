/*
* DB changes since 9556a4b (30.11.2024)
 */

ALTER TABLE `legion_history` DROP COLUMN `tab_id`;

-- drop advent calendar table
drop table advent;

-- create advent calendar table
CREATE TABLE `advent` (
												`account_id` int(11) NOT NULL,
												`last_day_received` tinyint(4) NOT NULL,
												PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `player_veteran_rewards` CHANGE COLUMN `received_months` `received_months` TINYINT UNSIGNED NOT NULL DEFAULT 0 AFTER `player_id`;