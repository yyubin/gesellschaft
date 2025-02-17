-- CreateTable
CREATE TABLE `Character` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(10) NOT NULL,
    `name_ko` VARCHAR(5) NOT NULL,

    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `Persona` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `character_id` INTEGER NOT NULL,
    `rank` INTEGER NOT NULL,
    `persona_name` VARCHAR(18) NOT NULL,
    `health` INTEGER NOT NULL,
    `min_speed` INTEGER NOT NULL,
    `max_speed` INTEGER NOT NULL,
    `defence_level` INTEGER NOT NULL,
    `attack_resistance` VARCHAR(4) NOT NULL,
    `penetration_resistance` VARCHAR(4) NOT NULL,
    `batting_resistance` VARCHAR(4) NOT NULL,
    `season` VARCHAR(6) NOT NULL,
    `release_date` DATETIME NOT NULL,
    `mental_strength` INTEGER NOT NULL,
    `sync` INTEGER NOT NULL,

    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- AddForeignKey
ALTER TABLE `Persona` ADD CONSTRAINT `Persona_character_id_fkey` FOREIGN KEY (`character_id`) REFERENCES `Character`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
