/*create a new DB to store user credentials of the apps*/
DROP DATABASE IF EXISTS accounts;
CREATE DATABASE accounts;

/*create a new DB account for the webserver, if not exists*/
GRANT ALL ON *.* TO 'webserver'@'localhost' IDENTIFIED BY 'password';
/*create the account for original app, in case of deleted*/
GRANT ALL ON *.* TO 'remote'@'localhost' IDENTIFIED BY 'remote_pass';
FLUSH privileges;

USE accounts;
/*name: unique user name as ID
 *password: password
 *token: server return this token when logged in,
			the client should append this token in every request
			to show his identity.
*/
CREATE TABLE credentials
(
	name varchar(255) NOT NULL PRIMARY KEY,
    password varchar(255) NOT NULL,
    token varchar(255) NOT NULL
);
CREATE TABLE activityLogs
(
    name varchar(255) NOT NULL,
    time varchar(255) NOT NULL,
    event varchar(255) NOT NULL
);
/*sample user accounts*/
INSERT INTO credentials
VALUES ("user1", "password", "&A%@S(aiFSushdf(&iY@#&*R$Y");
INSERT INTO credentials
VALUES ("user2", "password", "aiushdadsasfsdf&*R$&(*WSHY");
INSERT INTO credentials
VALUES ("user3", "password", "32q4aaa12da34ahdf(&iY@#R$Y");
INSERT INTO credentials
VALUES ("user4", "password", "1234dsiahusdfui(&iY@#&*R$Y");

/*create a procedure, when called, perform the db switching over*/
DROP PROCEDURE IF EXISTS switchover;
delimiter $$
CREATE PROCEDURE switchover (arg VARCHAR(256))
BEGIN
	IF (arg = "kickoff") THEN
		SET PASSWORD FOR 'remote'@'localhost' = PASSWORD('!');
		DROP USER 'remote'@'localhost';
		FLUSH PRIVILEGES;
	ELSE
        GRANT ALL ON *.* TO 'remote'@'localhost' IDENTIFIED BY 'remote_pass';
		FLUSH privileges;
	END IF;
END$$
delimiter ;