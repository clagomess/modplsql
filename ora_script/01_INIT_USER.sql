-- oracle 12
alter session set "_ORACLE_SCRIPT"=true;

-- tablespace
CREATE TABLESPACE TS_MODPLSQL DATAFILE 'TS_MODPLSQL' SIZE 5M;

-- USER SQL
CREATE USER "MODPLSQL" IDENTIFIED BY "010203"
DEFAULT TABLESPACE "TS_MODPLSQL"
TEMPORARY TABLESPACE "TEMP"
ACCOUNT UNLOCK ;

-- QUOTAS
ALTER USER "MODPLSQL" QUOTA UNLIMITED ON TS_MODPLSQL;

-- SYSTEM PRIVILEGES
GRANT CONNECT TO MODPLSQL;
GRANT UPDATE ANY TABLE TO "MODPLSQL";
GRANT CREATE TABLE TO "MODPLSQL";
GRANT SELECT ANY TABLE TO "MODPLSQL";
GRANT INSERT ANY TABLE TO "MODPLSQL";
GRANT DROP ANY TABLE TO "MODPLSQL";
GRANT ALTER ANY TABLE TO "MODPLSQL";
GRANT CREATE ANY TABLE TO "MODPLSQL";
GRANT DELETE ANY TABLE TO "MODPLSQL";
GRANT COMMENT ANY TABLE TO "MODPLSQL";
GRANT COMMENT ANY TABLE TO "MODPLSQL";
GRANT EXECUTE ANY PROCEDURE TO "MODPLSQL";
GRANT CREATE ANY PROCEDURE TO "MODPLSQL";