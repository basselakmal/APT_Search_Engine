-- phpMyAdmin SQL Dump
-- version 4.0.4.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 20, 2018 at 12:14 PM
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

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `PageRank`(IN `pageURL` VARCHAR(700) CHARSET utf8)
    NO SQL
BEGIN


SELECT ip.PageRank / Count(dr.domainURL) as Value FROM domain_referrer dr, indexed_pages ip WHERE ip.domainURL in (SELECT referrerURL FROM domain_referrer WHERE domainURL = pageURL) AND dr.referrerURL = ip.domainURL GROUP BY dr.referrerURL;


END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `Search`(IN `paramToken` VARCHAR(100))
    NO SQL
BEGIN

SET @TotalPagesCount = (SELECT COUNT(*) FROM indexed_pages);

SET @PagesCountWithParam = (SELECT COUNT(*) FROM inverted_file WHERE Token = paramToken);

SET @IDF := LN(@TotalPagesCount / @PagesCountWithParam);

SELECT @IDF * (f.Count/ip.TokensCount) as TF_IDF, ip.PageRank, f.URL, ip.Title, ip.Description FROM inverted_file as f, indexed_pages as ip WHERE Token = paramToken AND f.URL = ip.domainURL;

END$$

DELIMITER ;

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
  `domainURL` varchar(700) CHARACTER SET latin7 COLLATE latin7_general_cs NOT NULL,
  `referrerURL` varchar(700) CHARACTER SET latin7 COLLATE latin7_general_cs NOT NULL,
  PRIMARY KEY (`domainURL`,`referrerURL`),
  KEY `referrerURL` (`referrerURL`)
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
  `PageRank` float NOT NULL DEFAULT '0',
  `TokensCount` int(200) DEFAULT '0',
  PRIMARY KEY (`domainURL`),
  KEY `idx_indexed_pages_domainURL` (`domainURL`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `inverted_file`
--

CREATE TABLE IF NOT EXISTS `inverted_file` (
  `Token` varchar(500) NOT NULL,
  `Count` int(11) NOT NULL,
  `URL` varchar(500) CHARACTER SET latin7 COLLATE latin7_general_cs NOT NULL,
  PRIMARY KEY (`Token`,`URL`),
  KEY `idx_inverted_file_Token_URL` (`Token`,`URL`),
  KEY `idx_inverted_file_Token` (`Token`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
