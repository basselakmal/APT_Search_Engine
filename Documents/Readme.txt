How to run the project?
----------------------
=> IDE Used: IntelliJ Idea
=> DB: MySQL (USBWebServer)
=> Steps:
	-Open the project files in IntilliJ or any other IDE.
	-Import the libraries found in folder: "Java Libraries".
	-Start your MySQL Database Server (We use USBWebServer).
	-Import the database file "webcrawler.sql" to your MySQL Server.
	-In the DataSource class, you need to add your MySQL Server details (host - port - username - password).
	-You may start the Crawler from CrawlerRunner class.
	-You may start the Indexer from IndexerRunner class.
	-Both the Crawler and the Indexer are multi-threaded, so the program will ask you to specify the number of threads you want.
