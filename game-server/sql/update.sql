/*
* DB changes since 16e4dcf (15.12.2024)
 */

DROP TABLE `skill_motions`;
DROP TABLE `tasks`;
DELETE inventory FROM inventory LEFT JOIN legions l ON l.id = item_owner WHERE item_location = 3 AND l.id IS NULL;
ALTER TABLE `inventory`
	CHANGE COLUMN `enchant` `enchant` TINYINT UNSIGNED NOT NULL DEFAULT '0' AFTER `item_location`,
	CHANGE COLUMN `optional_socket` `optional_socket` TINYINT UNSIGNED NOT NULL DEFAULT (0) AFTER `fusioned_item`,
	CHANGE COLUMN `optional_fusion_socket` `optional_fusion_socket` TINYINT UNSIGNED NOT NULL DEFAULT (0) AFTER `optional_socket`,
	CHANGE COLUMN `rnd_plume_bonus` `rnd_plume_bonus` SMALLINT UNSIGNED NOT NULL DEFAULT (0) AFTER `buff_skill`;
ALTER TABLE `player_registered_items`
	DROP INDEX `item_unique_id`,
	ADD UNIQUE INDEX `item_unique_id` (`item_unique_id`) USING BTREE;
DELETE b FROM broker b LEFT JOIN inventory i ON b.item_pointer = i.item_unique_id AND i.item_id = b.item_id AND i.item_location = 126 WHERE i.item_id != b.item_id OR i.item_id IS NULL AND b.is_sold = 0;
