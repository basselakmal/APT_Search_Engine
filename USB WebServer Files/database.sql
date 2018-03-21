-- phpMyAdmin SQL Dump
-- version 4.0.10.18
-- https://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 21, 2018 at 09:56 PM
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
-- Table structure for table `crawledpages`
--

CREATE TABLE IF NOT EXISTS `crawledpages` (
  `domainURL` varchar(200) NOT NULL,
  `Title` varchar(100) NOT NULL,
  `Keywords` varchar(300) NOT NULL,
  `Description` varchar(300) NOT NULL,
  PRIMARY KEY (`domainURL`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `domain_referrer`
--

CREATE TABLE IF NOT EXISTS `domain_referrer` (
  `domainURL` varchar(200) NOT NULL,
  `referrerURL` varchar(200) NOT NULL,
  `isIndexed` tinyint(1) NOT NULL DEFAULT '0',
  `isCrawled` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`domainURL`,`referrerURL`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
