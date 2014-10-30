SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `metricsdb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `metricsdb` ;

-- -----------------------------------------------------
-- Table `metricsdb`.`metrics_experiments`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `metricsdb`.`metrics_experiments` (
  `id_metrics_experiments` INT NOT NULL ,
  `aggregator_location` VARCHAR(45) NULL ,
  `active` TINYINT(1) NOT NULL ,
  PRIMARY KEY (`id_metrics_experiments`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `metricsdb`.`metrics_virtual_machines`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `metricsdb`.`metrics_virtual_machines` (
  `id_metrics_virtual_machines` INT NOT NULL AUTO_INCREMENT ,
  `location` VARCHAR(45) NOT NULL ,
  `fk_metrics_experiments` INT NOT NULL ,
  `start_time` BIGINT NOT NULL ,
  `end_time` BIGINT NULL ,
  PRIMARY KEY (`id_metrics_virtual_machines`) ,
  INDEX `fk_metrics_virtual_machines_metrics_experiments1_idx` (`fk_metrics_experiments` ASC) ,
  CONSTRAINT `fk_metrics_virtual_machines_metrics_experiments1`
    FOREIGN KEY (`fk_metrics_experiments` )
    REFERENCES `metricsdb`.`metrics_experiments` (`id_metrics_experiments` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `metricsdb`.`metrics_sites`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `metricsdb`.`metrics_sites` (
  `id_metrics_sites` INT NOT NULL AUTO_INCREMENT ,
  `location` VARCHAR(15) NOT NULL ,
  PRIMARY KEY (`id_metrics_sites`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `metricsdb`.`metrics_physical_hosts`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `metricsdb`.`metrics_physical_hosts` (
  `id_metrics_physical_hosts` INT NOT NULL AUTO_INCREMENT ,
  `location` VARCHAR(45) NOT NULL ,
  `fk_metrics_sites` INT NOT NULL ,
  PRIMARY KEY (`id_metrics_physical_hosts`) ,
  INDEX `fk_metrics_physical_hosts_metrics_sites1_idx` (`fk_metrics_sites` ASC) ,
  CONSTRAINT `fk_metrics_physical_hosts_metrics_sites1`
    FOREIGN KEY (`fk_metrics_sites` )
    REFERENCES `metricsdb`.`metrics_sites` (`id_metrics_sites` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `metricsdb`.`items`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `metricsdb`.`items` (
  `id_items` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  `zabbix_itemid` INT NOT NULL ,
  `clock` BIGINT UNSIGNED NOT NULL ,
  `value` DOUBLE NOT NULL ,
  `unity` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`id_items`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `metricsdb`.`experiments_items`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `metricsdb`.`experiments_items` (
  `id_experiments_items` INT NOT NULL AUTO_INCREMENT ,
  `fk_metrics_experiments` INT NOT NULL ,
  `fk_items` INT NOT NULL ,
  PRIMARY KEY (`id_experiments_items`, `fk_metrics_experiments`, `fk_items`) ,
  INDEX `fk_experiments_items_metrics_experiments1_idx` (`fk_metrics_experiments` ASC) ,
  INDEX `fk_experiments_items_items1_idx` (`fk_items` ASC) ,
  CONSTRAINT `fk_experiments_items_metrics_experiments1`
    FOREIGN KEY (`fk_metrics_experiments` )
    REFERENCES `metricsdb`.`metrics_experiments` (`id_metrics_experiments` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_experiments_items_items1`
    FOREIGN KEY (`fk_items` )
    REFERENCES `metricsdb`.`items` (`id_items` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `metricsdb`.`virtual_machines_items`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `metricsdb`.`virtual_machines_items` (
  `id_virtual_machines_items` INT NOT NULL AUTO_INCREMENT ,
  `fk_metrics_virtual_machines` INT NOT NULL ,
  `fk_items` INT NOT NULL ,
  PRIMARY KEY (`id_virtual_machines_items`, `fk_metrics_virtual_machines`, `fk_items`) ,
  INDEX `fk_virtual_machines_items_metrics_virtual_machines1_idx` (`fk_metrics_virtual_machines` ASC) ,
  INDEX `fk_virtual_machines_items_items1_idx` (`fk_items` ASC) ,
  CONSTRAINT `fk_virtual_machines_items_metrics_virtual_machines1`
    FOREIGN KEY (`fk_metrics_virtual_machines` )
    REFERENCES `metricsdb`.`metrics_virtual_machines` (`id_metrics_virtual_machines` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_virtual_machines_items_items1`
    FOREIGN KEY (`fk_items` )
    REFERENCES `metricsdb`.`items` (`id_items` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `metricsdb`.`physical_hosts_items`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `metricsdb`.`physical_hosts_items` (
  `id_physical_hosts_items` INT NOT NULL AUTO_INCREMENT ,
  `fk_metrics_physical_hosts` INT NOT NULL ,
  `fk_items` INT NOT NULL ,
  PRIMARY KEY (`id_physical_hosts_items`, `fk_metrics_physical_hosts`, `fk_items`) ,
  INDEX `fk_physical_hosts_items_metrics_physical_hosts1_idx` (`fk_metrics_physical_hosts` ASC) ,
  INDEX `fk_physical_hosts_items_items1_idx` (`fk_items` ASC) ,
  CONSTRAINT `fk_physical_hosts_items_metrics_physical_hosts1`
    FOREIGN KEY (`fk_metrics_physical_hosts` )
    REFERENCES `metricsdb`.`metrics_physical_hosts` (`id_metrics_physical_hosts` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_physical_hosts_items_items1`
    FOREIGN KEY (`fk_items` )
    REFERENCES `metricsdb`.`items` (`id_items` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `metricsdb`.`sites_items`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `metricsdb`.`sites_items` (
  `id_sites_items` INT NOT NULL AUTO_INCREMENT ,
  `fk_metrics_sites` INT NOT NULL ,
  `fk_items` INT NOT NULL ,
  PRIMARY KEY (`id_sites_items`, `fk_metrics_sites`, `fk_items`) ,
  INDEX `fk_sites_items_items1_idx` (`fk_items` ASC) ,
  INDEX `fk_sites_items_metrics_sites1_idx` (`fk_metrics_sites` ASC) ,
  CONSTRAINT `fk_sites_items_items1`
    FOREIGN KEY (`fk_items` )
    REFERENCES `metricsdb`.`items` (`id_items` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_sites_items_metrics_sites1`
    FOREIGN KEY (`fk_metrics_sites` )
    REFERENCES `metricsdb`.`metrics_sites` (`id_metrics_sites` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `metricsdb`.`virtual_machines_physical_hosts_link`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `metricsdb`.`virtual_machines_physical_hosts_link` (
  `id_virtual_machines_physical_hosts_link` INT NOT NULL AUTO_INCREMENT ,
  `fk_metrics_virtual_machines` INT NOT NULL ,
  `fk_metrics_physical_hosts` INT NULL ,
  `start_time` BIGINT NOT NULL ,
  `end_time` BIGINT NULL ,
  PRIMARY KEY (`id_virtual_machines_physical_hosts_link`) ,
  INDEX `fk_virtual_machines_physical_hosts_link_metrics_virtual_mac_idx` (`fk_metrics_virtual_machines` ASC) ,
  INDEX `fk_virtual_machines_physical_hosts_link_metrics_physical_ho_idx` (`fk_metrics_physical_hosts` ASC) ,
  CONSTRAINT `fk_virtual_machines_physical_hosts_link_metrics_virtual_machi1`
    FOREIGN KEY (`fk_metrics_virtual_machines` )
    REFERENCES `metricsdb`.`metrics_virtual_machines` (`id_metrics_virtual_machines` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_virtual_machines_physical_hosts_link_metrics_physical_hosts1`
    FOREIGN KEY (`fk_metrics_physical_hosts` )
    REFERENCES `metricsdb`.`metrics_physical_hosts` (`id_metrics_physical_hosts` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `metricsdb` ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
