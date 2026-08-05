CREATE TABLE policy (
 policy_id INTEGER PRIMARY KEY, policy_number VARCHAR(30) NOT NULL UNIQUE,
 line_of_business VARCHAR(40) NOT NULL, insured_name VARCHAR(100) NOT NULL,
 insured_address VARCHAR(200) NOT NULL, effective_date DATE NOT NULL, expiry_date DATE NOT NULL,
 policy_limit DOUBLE PRECISION NOT NULL, deductible DOUBLE PRECISION NOT NULL,
 annual_premium DOUBLE PRECISION NOT NULL, status VARCHAR(20) NOT NULL
);
CREATE TABLE claim (
 claim_id INTEGER PRIMARY KEY, claim_number VARCHAR(30) NOT NULL UNIQUE,
 policy_id INTEGER NOT NULL, claimant_name VARCHAR(100) NOT NULL,
 loss_date DATE NOT NULL, reported_date DATE NOT NULL, loss_type VARCHAR(40) NOT NULL,
 description VARCHAR(300) NOT NULL, status VARCHAR(30) NOT NULL,
 reserve_amount DOUBLE PRECISION NOT NULL, assigned_adjuster VARCHAR(40) NOT NULL,
 created_by VARCHAR(40) NOT NULL, created_date DATE NOT NULL,
 CONSTRAINT fk_claim_policy FOREIGN KEY (policy_id) REFERENCES policy(policy_id)
);
CREATE TABLE payment (
 payment_id INTEGER PRIMARY KEY, claim_id INTEGER NOT NULL, settlement_id INTEGER NOT NULL,
 payee_name VARCHAR(100) NOT NULL, amount DOUBLE PRECISION NOT NULL,
 payment_method VARCHAR(20) NOT NULL, check_number VARCHAR(30) NOT NULL,
 issued_date DATE NOT NULL, status VARCHAR(20) NOT NULL,
 CONSTRAINT fk_payment_claim FOREIGN KEY (claim_id) REFERENCES claim(claim_id)
);
CREATE INDEX ix_claim_status ON claim(status);
CREATE INDEX ix_claim_adjuster ON claim(assigned_adjuster);
CREATE INDEX ix_claim_policy ON claim(policy_id);
CREATE INDEX ix_payment_claim ON payment(claim_id);
