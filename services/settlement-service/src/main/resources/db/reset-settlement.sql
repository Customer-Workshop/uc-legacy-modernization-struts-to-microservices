INSERT INTO policy VALUES (1,'NS-00001','AUTO','Policyholder 1','1 Main Street','2018-01-01','2019-12-31',25000,500,1200,'ACTIVE');
INSERT INTO policy VALUES (2,'NS-00002','HOMEOWNERS','Policyholder 2','2 Main Street','2018-02-01','2019-12-31',150000,1000,1300,'ACTIVE');
INSERT INTO policy VALUES (3,'NS-00003','COMMERCIAL_PROPERTY','Policyholder 3','3 Main Street','2018-03-01','2019-12-31',500000,2500,1400,'ACTIVE');
INSERT INTO policy VALUES (4,'NS-00004','GENERAL_LIABILITY','Policyholder 4','4 Main Street','2018-04-01','2019-12-31',100000,1000,1500,'ACTIVE');
INSERT INTO policy VALUES (5,'NS-00005','AUTO','Policyholder 5','5 Main Street','2018-05-01','2019-12-31',25000,500,1600,'ACTIVE');
INSERT INTO policy VALUES (9,'NS-00009','AUTO','Policyholder 9','9 Main Street','2018-09-01','2019-12-31',25000,500,2000,'ACTIVE');
INSERT INTO policy VALUES (13,'NS-00013','AUTO','Policyholder 13','13 Main Street','2018-03-01','2019-12-31',25000,500,2400,'ACTIVE');
INSERT INTO policy VALUES (17,'NS-00017','AUTO','Policyholder 17','17 Main Street','2018-07-01','2019-12-31',25000,500,2800,'ACTIVE');
INSERT INTO policy VALUES (21,'NS-00021','AUTO','Policyholder 21','21 Main Street','2018-01-01','2019-12-31',25000,500,3200,'ACTIVE');
INSERT INTO policy VALUES (25,'NS-00025','AUTO','Policyholder 25','25 Main Street','2018-05-01','2019-12-31',25000,500,3600,'ACTIVE');
INSERT INTO policy VALUES (29,'NS-00029','AUTO','Policyholder 29','29 Main Street','2018-09-01','2019-12-31',25000,500,4000,'ACTIVE');
INSERT INTO policy VALUES (33,'NS-00033','AUTO','Policyholder 33','33 Main Street','2018-03-01','2019-12-31',25000,500,4400,'ACTIVE');
INSERT INTO policy VALUES (37,'NS-00037','AUTO','Policyholder 37','37 Main Street','2018-07-01','2019-12-31',25000,500,4800,'ACTIVE');
INSERT INTO policy VALUES (9001,'NS-09001','AUTO','Cap Trap Policy','9001 Reserve Way','2018-01-01','2019-12-31',1000,100,700,'ACTIVE');
INSERT INTO policy VALUES (9002,'NS-09002','HOMEOWNERS','Deductible Trap Policy','9002 Reserve Way','2018-01-01','2019-12-31',100000,5000,700,'ACTIVE');
INSERT INTO policy
SELECT i, 'NS-' || lpad(i::text, 5, '0'),
       CASE ((i - 1) % 4) WHEN 0 THEN 'AUTO' WHEN 1 THEN 'HOMEOWNERS'
         WHEN 2 THEN 'COMMERCIAL_PROPERTY' ELSE 'GENERAL_LIABILITY' END,
       'Policyholder ' || i, i || ' Main Street',
       make_date(2018, ((i - 1) % 10) + 1, 1), DATE '2019-12-31',
       CASE ((i - 1) % 4) WHEN 0 THEN 25000 WHEN 1 THEN 150000 WHEN 2 THEN 500000 ELSE 100000 END,
       CASE ((i - 1) % 4) WHEN 0 THEN 500 WHEN 1 THEN 1000 WHEN 2 THEN 2500 ELSE 1000 END,
       1100 + (i * 100), 'ACTIVE'
FROM generate_series(1, 40) AS s(i)
WHERE i NOT IN (1, 2, 3, 4, 5, 9, 13, 17, 21, 25, 29, 33, 37);
INSERT INTO claim
SELECT i, 'CLM-' || lpad(i::text, 5, '0'),
       CASE WHEN i = 119 THEN 9001 WHEN i = 120 THEN 9002 ELSE ((i - 1) % 40) + 1 END,
       'Claimant ' || i, DATE '2019-01-15',
       CASE (i % 5) WHEN 1 THEN DATE '2019-03-15' WHEN 2 THEN DATE '2019-02-15'
         WHEN 3 THEN DATE '2019-01-15' WHEN 4 THEN DATE '2018-12-01' ELSE DATE '2019-03-15' END,
       CASE ((i - 1) % 4) WHEN 0 THEN 'FIRE' WHEN 1 THEN 'WATER' WHEN 2 THEN 'LIABILITY' ELSE 'COLLISION' END,
       'Seeded claim ' || i,
       CASE ((i - 1) % 5) WHEN 0 THEN 'OPEN' WHEN 1 THEN 'INVESTIGATING' WHEN 2 THEN 'APPROVED'
         WHEN 3 THEN 'DENIED' ELSE 'CLOSED' END,
       CASE WHEN i = 119 THEN 1500 WHEN i = 120 THEN 2000 ELSE 1000 + i END,
       CASE ((i - 1) % 5) WHEN 0 THEN 'adjuster1' WHEN 1 THEN 'adjuster2' WHEN 2 THEN 'adjuster3'
         WHEN 3 THEN 'adjuster4' ELSE 'adjuster5' END,
       'supervisor',
       CASE (i % 5) WHEN 1 THEN DATE '2019-03-15' WHEN 2 THEN DATE '2019-02-15'
         WHEN 3 THEN DATE '2019-01-15' WHEN 4 THEN DATE '2018-12-01' ELSE DATE '2019-03-15' END
FROM generate_series(1, 120) AS s(i);
INSERT INTO settlement
SELECT i, i, CASE WHEN i = 119 THEN 1500 WHEN i = 120 THEN 2000 ELSE 1000 + i END,
       CASE WHEN i = 120 THEN 5000 ELSE 500 END, 100, false,
       CASE WHEN i >= 3 AND (i - 3) % 5 = 0 AND i < 119 THEN i ELSE 0 END,
       'adjuster1', DATE '2019-03-01'
FROM generate_series(1, 120) AS s(i);
INSERT INTO payment
SELECT i, i, i, 'Claimant ' || i, CASE WHEN i % 2 = 1 THEN 500 ELSE 250 END,
       'CHECK', 'CHK-' || lpad(i::text, 5, '0'), DATE '2019-03-15', 'ISSUED'
FROM generate_series(1, 60) AS s(i);
