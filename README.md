# MyRoutin (마이루틴)

판매자는 자유롭게 가게(Shop)를 개설하여 상품을 판매하고, 구매자는 필요한 상품을 단건 구매하거나 정기 구독할 수 있는 오픈마켓 플랫폼 입니다.

## 프로젝트 구조
___
이 프로젝트는 마이크로서비스 아키텍처(MSA)로 구성되어 있으며, 각 서비스의 역할은 다음과 같습니다.

**인프라**

| 서비스               | 포트          | 설명                |
|-------------------|-------------|-------------------|
| **redis**         | 6379        | 데이터 캐싱            |
| **rabbitmq**      | 5672, 15672 | 이메일 전송     |
| **pgvector**      | 5432        | 데이터베이스 + 유사도 검색   |
| **elasticsearch** | 9200        | 검색                |
| **nginx**         | 80, 443     | 프록시 + SSL         |
| **kafka**         | 9092, 9093  | 서비스 간 비동기 통신 |

**Spring Cloud**

| 서비스                 | 포트   | 설명                                |
|---------------------|------|-----------------------------------|
| **apigateway**      | 8000 | API Gateway Service (진입점)         |
| **discovery**       | 8761 | Service Discovery (Eureka Server) |
| **config**          | 8888 | Config Server (로컬 환경에서만 사용)       |

**비즈니스**

| 서비스                 | 포트   | 설명                               |
|---------------------|------|----------------------------------|
| **member-service**  | 8081 | 회원 및 상점 관리 서비스                   |
| **batch-service**   | 8089 | 대용량 배치 작업 서비스                    |
| **catalog-service** | 8082 | 상품 및 재고 관리 서비스                   |
| **order-service**   | 8083 | 주문 처리 및 구독 관리 서비스                |
| **payment-service** | 8088 | PG 결제 처리 서비스                     |
| **support-service** | 8087 | 보조 서비스(알림, 추천, 리뷰, 검색)           |
| **wallet-service**  | 8084 | 포인트 및 지갑 관리 서비스                  |




## 프로젝트 아키텍처
___
EC2 인스턴스(t3.medium) 2개를 활용했습니다. 각 인스턴스는 K3S를 통해 클러스터링 했습니다.

- Master: Spring Cloud, Member
- Worker: Batch, Catalog, Order, Payment, Support, Wallet

![Project Architecture](https://github.com/user-attachments/assets/baf6200e-cbcb-4de0-b3d3-c346ba5f918c)

## 기술 스택
- **Language**: Java 17
- **Framework**: Spring Boot 3.5.8
- **Build Tool**: Gradle 8.14.3
- **Spring Cloud**: 
  - Spring Cloud Gateway
  - Spring Cloud Config
  - Eureka (Discovery)
  - OpenFeign
- **Spring Boot**:
  - Spring Security
  - Spring Batch
  - Spring Ai
- **Database**:
  - PostgreSQL 17 (pgvector)
  - Spring Data JPA
- **Cache**: Redis 7
- **Message Queue**:
  - Kafka 7.6.1 (KRaft Mode)
  - RabbitMQ 3.13
- **Search**: Elasticsearch 8.15.3
- **Proxy**: Nginx (NPM)
- **OAuth**:
  - Kakao
  - Google
- **PG**: TossPayments
- **AI**: OpenAi
- **Infra**:
  - AWS EC2
  - AWS S3
- **Container**:
  - Docker
  - Kubernetes(K3S)
- **CI/CD**: Github Actions

## 서비스 기능
- **회원**: 회원가입, 로그인, 회원 정보 관리
- **상품**: 상품 등록, 조회, 검색
- **주문**: 주문 생성, 조회, 주문 취소, 주문 환불
- **구독**: 정기 구독 신청, 해지, 갱신
- **정산**: 판매자 정산, 수수료 계산
- **지갑**: 예치금 충전, 사용, 환불

## 서비스 실행

### 1. 인프라 실행
Docker Compose를 사용하여 필요한 인프라(DB, Redis, Kafka 등)를 실행합니다.
```bash
docker compose -f docker-compose-infra.yaml up -d
```

### 2. 애플리케이션 실행
각 서비스 디렉토리에서 Gradle을 사용하여 실행하거나, IDE에서 실행합니다.
로컬 환경에서 실행 시 'local' 프로파일을 활성화해야 합니다.

**빌드**

```bash
# 빌드
./gradlew build

# 테스트 제외 빌드
./gradlew build -x test

```

**CLI 실행 (Jar)**
```bash
# 아래 명령어 순서대로 실행
chmod +x run-local.sh
./run-local.sh
# ... 각 서비스 별 .env 파일 필요
```

**Environment**

각 서비스는 실행 시 환경 변수를 통해 민감 정보(DB, OAuth, PG 키 등)를 주입받습니다.  
실제 값은 Git에 포함되지 않으며, `.env` 파일 또는 OS 환경 변수로 설정해야 합니다.

예시는 `docs/env_template`를 참고하세요.

**IDE 실행 (IntelliJ 등)**
- **VM Options**: `-Dspring.profiles.active=local` 추가
- 또는 **Environment Variables**: `SPRING_PROFILES_ACTIVE=local` 설정

## API 명세 - Swagger
API 문서는 Swagger UI를 통해 확인할 수 있습니다.
- URL: [http://localhost:8000/webjars/swagger-ui/index.html](http://localhost:8000/webjars/swagger-ui/index.html)

## 브랜치 네이밍 규칙
___
Git Flow 전략을 따릅니다.
- `main`: 배포 가능한 안정 버전
- `develop`: 다음 배포를 위한 개발 버전
- `feature/XXX`: 새로운 기능 개발 (예: `feature/123`)
- `bugfix/XXX`: 버그 수정
- `refactor/XXX`: 리팩토링
- `chore/XXX`: 문서 작성

## 커밋 메시지
Conventional Commits 규칙을 따릅니다.
- `[Feat]`: 새로운 기능 추가
- `[Bugfix]`: 버그 수정
- `[Refactor]`: 코드 리팩토링
- `[Chore]`: 문서 작업

## CI/CD
- **CI**: Github Actions를 통해 빌드 및 테스트 자동화
- **CD**: Docker Image 빌드 및 Kubernetes 배포 자동화
