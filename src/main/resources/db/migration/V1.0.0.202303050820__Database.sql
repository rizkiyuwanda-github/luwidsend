
CREATE TABLE transaction (
                id VARCHAR(50) NOT NULL,
                sender_bank_id VARCHAR(5) NOT NULL,
                sender_bank_name VARCHAR(100) NOT NULL,
                sender_account_id VARCHAR(50) NOT NULL,
                sender_account_name VARCHAR(100) NOT NULL,
                receiver_bank_id VARCHAR(5) NOT NULL,
                receiver_bank_name VARCHAR(100) NOT NULL,
                receiver_account_id VARCHAR(50) NOT NULL,
                receiver_account_name VARCHAR(100) NOT NULL,
                time TIMESTAMP NOT NULL,
                amount NUMERIC(19,2) NOT NULL,
                fee NUMERIC(19,2) NOT NULL,
                note TEXT,
                status VARCHAR(50) NOT NULL,
                CONSTRAINT transaction_pk PRIMARY KEY (id)
);
COMMENT ON COLUMN transaction.status IS 'Pending
Delivered
Canceled';


CREATE TABLE bank (
                id VARCHAR(5) NOT NULL,
                name VARCHAR(100) NOT NULL,
                CONSTRAINT bank_pk PRIMARY KEY (id)
);


CREATE TABLE account (
                id VARCHAR(50) NOT NULL,
                bank_id VARCHAR(5) NOT NULL,
                name VARCHAR(100) NOT NULL,
                CONSTRAINT account_pk PRIMARY KEY (id)
);


CREATE TABLE user_app (
                id VARCHAR(100) NOT NULL,
                password VARCHAR(255) NOT NULL,
                role VARCHAR(50) NOT NULL,
                CONSTRAINT user_app_pk PRIMARY KEY (id)
);


ALTER TABLE account ADD CONSTRAINT bank_account_fk
FOREIGN KEY (bank_id)
REFERENCES bank (id)
ON DELETE RESTRICT
ON UPDATE CASCADE
NOT DEFERRABLE;