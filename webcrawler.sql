-- phpMyAdmin SQL Dump
-- version 4.0.10.18
-- https://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 03, 2018 at 02:00 AM
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

-- --------------------------------------------------------

--
-- Table structure for table `crawled_pages`
--

CREATE TABLE IF NOT EXISTS `crawled_pages` (
  `domainURL` varchar(500) COLLATE latin7_general_cs NOT NULL,
  `isIndexed` tinyint(1) NOT NULL DEFAULT '0',
  `highPriority` tinyint(1) NOT NULL DEFAULT '0',
  `isCrawled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`domainURL`)
) ENGINE=InnoDB DEFAULT CHARSET=latin7 COLLATE=latin7_general_cs;

-- --------------------------------------------------------

--
-- Table structure for table `domain_referrer`
--

CREATE TABLE IF NOT EXISTS `domain_referrer` (
  `domainURL` varchar(500) CHARACTER SET latin7 COLLATE latin7_general_cs NOT NULL,
  `referrerURL` varchar(500) CHARACTER SET latin7 COLLATE latin7_general_cs NOT NULL,
  PRIMARY KEY (`domainURL`,`referrerURL`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `indexed_pages`
--

CREATE TABLE IF NOT EXISTS `indexed_pages` (
  `domainURL` varchar(300) CHARACTER SET latin7 COLLATE latin7_general_cs NOT NULL,
  `Title` varchar(300) NOT NULL,
  `Keywords` varchar(700) NOT NULL,
  `Description` varchar(700) NOT NULL,
  PRIMARY KEY (`domainURL`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `inverted_file`
--

CREATE TABLE IF NOT EXISTS `inverted_file` (
  `Token` varchar(500) NOT NULL,
  `Count` int(11) NOT NULL,
  `URL` varchar(500) CHARACTER SET latin7 COLLATE latin7_general_cs NOT NULL,
  PRIMARY KEY (`Token`,`URL`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
