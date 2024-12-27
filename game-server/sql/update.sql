/*
* DB changes since 16e4dcf (15.12.2024)
 */

DROP TABLE `skill_motions`;
DROP TABLE `tasks`;
DELETE inventory FROM inventory LEFT JOIN legions l ON l.id = item_owner WHERE item_location = 3 AND l.id IS NULL;