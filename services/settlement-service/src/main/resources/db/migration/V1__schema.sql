CREATE TABLE settlement (
 settlement_id int PRIMARY KEY, claim_id int NOT NULL, covered_amount double precision NOT NULL,
 deductible_applied double precision NOT NULL, depreciation double precision NOT NULL,
 capped_at_limit boolean NOT NULL, settlement_amount double precision NOT NULL,
 calculated_by varchar(40), calculated_date date
);
CREATE TABLE payment (
 payment_id int PRIMARY KEY, claim_id int, settlement_id int, payee_name varchar(100),
 amount double precision, payment_method varchar(20), check_number varchar(30),
 issued_date date, status varchar(20),
 CONSTRAINT fk_payment_settlement FOREIGN KEY (settlement_id) REFERENCES settlement(settlement_id)
);
