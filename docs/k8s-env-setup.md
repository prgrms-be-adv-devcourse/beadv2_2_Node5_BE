# K8s env 적용 가이드 (Minikube / k3s)

이 문서는 로컬 Minikube와 서버 k3s에서 `.env` 파일들을 읽어 Kubernetes에 적용하는 절차를 정리한 것이다.

## 대상 .env 파일

- `.env` (공통/기본 값)
- `member-service/.env`
- `shop-service/.env`
- `env/.env.billing` (현재 비어 있음)

## 기본 원칙

- `.env` 값은 K8s Secret으로 올리고, 각 Deployment에서 `envFrom`으로 주입한다.
- 공통 값(예: `SPRING_DATASOURCE_*`, `MAIL_*`, `REDIS_HOST`)은 공통 Secret으로 만들고, 서비스별 값은 서비스 전용 Secret으로 만든다.
- 실제 배포에 쓰는 값은 로컬 파일에 두지 말고 별도 보관소/시크릿 매니저로 관리한다.

## 해야 할 작업 목록

### 1) Secret 생성

Minikube와 k3s 모두 동일한 리소스를 만든다. 실행 커맨드만 다르다.

- 공통 Secret 예시: `.env`
- 서비스 전용 Secret 예시: `member-service/.env`, `shop-service/.env`

### 2) Deployment에 envFrom 추가

각 서비스 Deployment에 다음과 같이 `envFrom`을 추가한다.

```yaml
        envFrom:
          - secretRef:
              name: common-env
          - secretRef:
              name: member-service-env
```

서비스별 Secret은 해당 서비스에만 추가한다.

### 3) 배포 순서

1) Secret 생성
2) `kubectl apply -k k8s`
3) `rollout status` 확인

## 로컬: Minikube

### Secret 생성

```sh
PROFILE=node5
KUBECTL="minikube -p $PROFILE kubectl --"

$KUBECTL create secret generic common-env \
  --from-env-file=.env \
  -o yaml --dry-run=client | $KUBECTL apply -f -

$KUBECTL create secret generic member-service-env \
  --from-env-file=member-service/.env \
  -o yaml --dry-run=client | $KUBECTL apply -f -

$KUBECTL create secret generic shop-service-env \
  --from-env-file=shop-service/.env \
  -o yaml --dry-run=client | $KUBECTL apply -f -
```

### 배포

```sh
$KUBECTL apply -k k8s
$KUBECTL rollout status deploy/apigateway
```

## 서버: k3s

### Secret 생성

```sh
KUBECTL="kubectl"  # 또는 k3s kubectl

$KUBECTL create secret generic common-env \
  --from-env-file=.env \
  -o yaml --dry-run=client | $KUBECTL apply -f -

$KUBECTL create secret generic member-service-env \
  --from-env-file=member-service/.env \
  -o yaml --dry-run=client | $KUBECTL apply -f -

$KUBECTL create secret generic shop-service-env \
  --from-env-file=shop-service/.env \
  -o yaml --dry-run=client | $KUBECTL apply -f -
```

### 배포

```sh
$KUBECTL apply -k k8s
$KUBECTL rollout status deploy/apigateway
```

## 주의 사항

- `.env`에 포함된 민감 정보는 로컬에만 두고, 서버에서는 별도 보안 저장소를 사용한다.
- `env/.env.billing`이 비어 있으니 필요하다면 값 채운 뒤 Secret을 추가한다.
- 특정 서비스에만 필요한 값은 공통 Secret에 넣지 않는다.
