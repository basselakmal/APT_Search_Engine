-- phpMyAdmin SQL Dump
-- version 4.0.4.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 31, 2018 at 12:21 AM
-- Server version: 5.6.13
-- PHP Version: 5.4.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `webcrawler`
--
CREATE DATABASE IF NOT EXISTS `webcrawler` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `webcrawler`;

-- --------------------------------------------------------

--
-- Table structure for table `crawledpages`
--

CREATE TABLE IF NOT EXISTS `crawledpages` (
  `domainURL` varchar(300) NOT NULL,
  `Title` varchar(300) NOT NULL,
  `Keywords` varchar(700) NOT NULL,
  `Description` varchar(700) NOT NULL,
  PRIMARY KEY (`domainURL`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `domain_referrer`
--

CREATE TABLE IF NOT EXISTS `domain_referrer` (
  `domainURL` varchar(500) NOT NULL,
  `referrerURL` varchar(500) NOT NULL,
  `isIndexed` tinyint(1) NOT NULL DEFAULT '0',
  `isCrawled` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`domainURL`,`referrerURL`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `invertedfile`
--

CREATE TABLE IF NOT EXISTS `invertedfile` (
  `Token` varchar(500) NOT NULL,
  `Count` int(11) NOT NULL,
  `URL` varchar(500) NOT NULL,
  PRIMARY KEY (`Token`,`URL`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
