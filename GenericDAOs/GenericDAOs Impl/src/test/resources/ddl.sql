CREATE TABLE TEST2
(
  TEST2ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  B       BOOLEAN                           NOT NULL
);
CREATE TABLE TEST
(
  TESTID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
  S      CLOB,
  I      INTEGER,
  D      TIMESTAMP,
  TEST2  BIGINT,
  CONSTRAINT TEST_TEST2_TEST2ID_FK FOREIGN KEY (TEST2) REFERENCES TEST2 (TEST2ID)
);
INSERT INTO PUBLIC.TEST2 (B) VALUES (0);
INSERT INTO TEST (S, I, D, TEST2) VALUES ('s', 1, '2017-05-13 16:42:43', 1);