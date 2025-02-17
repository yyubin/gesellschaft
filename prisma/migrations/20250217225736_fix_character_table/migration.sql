/*
  Warnings:

  - The primary key for the `Character` table will be changed. If it partially fails, the table could be left without primary key constraint.
  - You are about to drop the column `Character` on the `Character` table. All the data in the column will be lost.
  - Added the required column `id` to the `Character` table without a default value. This is not possible if the table is not empty.

*/
-- DropForeignKey
ALTER TABLE `Persona` DROP FOREIGN KEY `Persona_character_id_fkey`;

-- AlterTable
ALTER TABLE `Character` DROP PRIMARY KEY,
    DROP COLUMN `Character`,
    ADD COLUMN `id` INTEGER NOT NULL AUTO_INCREMENT,
    ADD PRIMARY KEY (`id`);

-- AddForeignKey
ALTER TABLE `Persona` ADD CONSTRAINT `Persona_character_id_fkey` FOREIGN KEY (`character_id`) REFERENCES `Character`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
