CREATE TABLE policy (
 policy_id INTEGER PRIMARY KEY, policy_number VARCHAR(30) NOT NULL UNIQUE,
 line_of_business VARCHAR(40) NOT NULL, insured_name VARCHAR(100) NOT NULL,
 insured_address VARCHAR(200) NOT NULL, effective_date DATE NOT NULL, expiry_date DATE NOT NULL,
 policy_limit DECIMAL(19,4) NOT NULL, deductible DECIMAL(19,4) NOT NULL,
 annual_premium DECIMAL(19,4) NOT NULL, status VARCHAR(20) NOT NULL
);
CREATE TABLE insured_party (
 party_id INTEGER PRIMARY KEY, policy_id INTEGER NOT NULL, name VARCHAR(100) NOT NULL,
 relationship VARCHAR(30) NOT NULL, phone VARCHAR(30), email VARCHAR(100),
 CONSTRAINT fk_party_policy FOREIGN KEY (policy_id) REFERENCES policy(policy_id)
);
CREATE TABLE coverage (
 coverage_id INTEGER PRIMARY KEY, policy_id INTEGER NOT NULL, coverage_code VARCHAR(30) NOT NULL,
 description VARCHAR(200) NOT NULL, coverage_limit DECIMAL(19,4) NOT NULL,
 deductible DECIMAL(19,4) NOT NULL, CONSTRAINT fk_coverage_policy FOREIGN KEY (policy_id) REFERENCES policy(policy_id)
);
CREATE INDEX ix_policy_line ON policy(line_of_business);
