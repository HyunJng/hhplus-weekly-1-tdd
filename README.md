# 포인트 관리 서비스 - TDD 과제

## 📌 목표

- 포인트 충전, 사용, 조회 기능을 구현한다.
- 도메인 중심의 설계와 테스트 주도 개발(TDD)로 기본 기능을 완성한다.
- 정책(제약 조건)을 반영하여 예외 상황도 테스트한다.

---

## 🧾 기능 개요

| 기능 | 설명 |
|------|------|
| 포인트 조회 | 특정 유저의 현재 포인트 조회 |
| 충전 내역 조회 | 특정 유저의 충전/사용 이력 리스트 조회 |
| 포인트 충전 | 특정 유저의 포인트를 충전 |
| 포인트 사용 | 특정 유저의 포인트를 사용 |

---

## 🧩 포인트 정책

### ✅ 공통
- 충전/사용 이력은 로그 테이블에 반드시 기록되어야 한다.

### ✅ 충전 정책
- 최소 충전 금액: **1000원 이상**
- 최대 1회 충전 금액: **10000원 이하**
- 최대 보유 포인트: **100000원**

### ✅ 사용 정책
- 최소 사용 금액: **1000원 이상**
- 보유 포인트 제한: **10000원 이상** 존재해야 사용 가능
- 보유 포인트보다 큰 금액 사용 시 실패

---

## 🧪 테스트 전략

### 단위 테스트
- 도메인 객체(Point)의 상태 변경 메서드 테스트
- 정책(ChargingPolicy, UsagePolicy) 검증
- PointService 내부 흐름 테스트
- PointControlle 요청과 응답 테스트 
---

## ✅ 주요 테스트 시나리오

### 포인트 조회

- 유저가 자신의 포인트를 조회할 수 있다

### 포인트 충전

- 유저는 1000원 이상 10000원 이하를 충전할 수 있다
- 충전 후 포인트 로그가 적재된다
- 최대 보유 금액(10만원)을 초과하는 충전은 실패한다
- 1000원 미만 또는 10000원 초과 충전은 실패한다

### 포인트 사용

- 유저는 1000원 이상을 사용할 수 있다
- 보유 포인트보다 큰 금액을 사용하려 하면 실패한다
- 사용 후 포인트 로그가 적재된다

---

## 🔁 동시성 테스트 (Step 2 사전 설계 포함)

- 여러 쓰레드가 동시에 충전/사용 요청 시:
    - **동시성 이슈 없이 정확하게 반영되어야 함**
    - **총 보유 포인트는 비즈니스 제약을 초과하지 않아야 함**
    - `Executors + CountDownLatch` 기반 테스트 설계 완료

---

## 🗂 디렉토리 구조 (요약)

```
point/
├─controller
│  │  PointController.java
│  │
│  └─dto
│          PointDto.java
│          PointLogDto.java
│
├─domain
│  ├─model
│  │      Point.java
│  │      PointLog.java
│  │      TransactionType.java
│  │
│  └─policy
│          ChargingPolicy.java
│          DefaultChargingPolicy.java
│          DefaultUsagePolicy.java
│          UsagePolicy.java
│
├─infrastruction
│  ├─adapter
│  │      PointRepositoryImpl.java
│  │
│  └─database
│      │  PointHistoryTable.java
│      │  UserPointTable.java
│      │
│      └─entity
│              PointHistory.java
│              UserPoint.java
│
└─service
    │  PointService.java
    │
    └─port
            PointRepository.java
```

---

## 💡 기타

- 외부 DB를 사용하지 않고 `Map 기반 Table 클래스`를 주입받아 사용
- 테스트 시간 의도적 랜덤 딜레이 (`Thread.sleep(random)`)가 있으므로  
  **동시성 테스트 시 일정한 실패율이 발생할 수 있음** → 정책적으로 제어 필요

---

## 📚 참고 키워드

- TDD (Test-Driven Development)
- Spring Boot, MockMvc
- JUnit5, AssertJ, Mockito
- CountDownLatch, ExecutorService (동시성 제어)

