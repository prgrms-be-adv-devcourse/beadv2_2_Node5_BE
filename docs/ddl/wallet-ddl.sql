CREATE SCHEMA IF NOT EXISTS wallet;

CREATE TABLE wallet."wallet" (
                                  id uuid NOT NULL,
                                  member_id uuid NOT NULL,
                                  balance BIGINT NOT NULL DEFAULT 0,
                                  created_at timestamp NOT NULL,
                                  modified_at timestamp NOT NULL,
                                  deleted_at timestamp NULL,
                                  CONSTRAINT pk_wallet PRIMARY KEY (id),
                                  CONSTRAINT uq_wallet_member_id UNIQUE (member_id)
);

CREATE TABLE wallet."wallet_deposit_log" (
                                              id uuid NOT NULL,
                                              member_id uuid NOT NULL,
                                              settlement_id uuid NOT NULL,
                                              amount BIGINT NOT NULL,
                                              created_at timestamp NOT NULL,
                                              modified_at timestamp NOT NULL,
                                              CONSTRAINT pk_wallet_deposit_log PRIMARY KEY (id),
                                              CONSTRAINT uq_wallet_deposit_settlement UNIQUE (settlement_id)
);

CREATE TABLE wallet."wallet_withdraw_log" (
                                               id uuid NOT NULL,
                                               member_id uuid NOT NULL,
                                               order_id uuid NOT NULL,
                                               amount BIGINT NOT NULL,
                                               state varchar(20) NOT NULL,
                                               created_at timestamp NOT NULL,
                                               modified_at timestamp NOT NULL,
                                               CONSTRAINT pk_wallet_withdraw_log PRIMARY KEY (id),
                                               CONSTRAINT uq_wallet_withdraw_order UNIQUE (order_id)
);

CREATE TABLE wallet."wallet_transfer_log" (
                                               id uuid NOT NULL,
                                               member_id uuid NOT NULL,
                                               account_no varchar(20) NOT NULL,
                                               amount BIGINT NOT NULL,
                                               transaction_id varchar(100) NOT NULL,
                                               message varchar(100) NOT NULL,
                                               requested_at timestamp NOT NULL,
                                               approved_at timestamp NOT NULL,
                                               created_at timestamp NOT NULL,
                                               modified_at timestamp NOT NULL,
                                               CONSTRAINT pk_wallet_transfer_log PRIMARY KEY (id)
);

CREATE TABLE wallet."wallet_transaction_log" (
                                                  id UUID NOT NULL,                                -- 식별자 (UUID)
                                                  member_id UUID NOT NULL,                                -- 회원 식별자 (수정 불가 제약은 애플리케이션 레벨 권장)
                                                  type VARCHAR(50) NOT NULL,                              -- 거래 타입 (Enum 명칭 저장)
                                                  group_type VARCHAR(50) NOT NULL,                        -- 거래 그룹 타입 (Enum 명칭 저장)
                                                  amount BIGINT NOT NULL,                                 -- 거래 금액
                                                  balance_after BIGINT NOT NULL,                         -- 거래 후 잔액
                                                  status VARCHAR(30) NOT NULL,                            -- 거래 상태
                                                  reference_id VARCHAR(255) NOT NULL,                     -- 외부 참조 아이디 (주문번호 등)
                                                  created_at timestamp NOT NULL,
                                                  modified_at timestamp NOT NULL,
                                                  CONSTRAINT pk_wallet_transaction_log PRIMARY KEY (id)
);
