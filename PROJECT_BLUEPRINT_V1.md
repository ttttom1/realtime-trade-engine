# 프로젝트: 글로벌 레벨 초저지연 거래 플랫폼 (Global-Scale Exchange System)

## 1. 프로젝트 최종 비전
- **목표**: 개인이 구현할 수 있는 최고 수준의 엔지니어링 역량을 투입하여, 실제 글로벌 거래소(Binance, NASDAQ 등)의 아키텍처를 로컬 환경에 재현한다.
- **지표**: 수만 명의 동시 접속, 초당 수천 건(TPS)의 매칭, 0ms에 수렴하는 시세 전파, 그리고 어떠한 장애 상황에서도 1원도 틀리지 않는 데이터 무결성.

## 2. 핵심 작동 메커니즘: [문제 발견 - 해결] 프레임워크
이 프로젝트의 모든 진전은 반드시 아래의 사고 흐름을 따른다:
1. **문제점 발견 (Issue Discovery)**: 현재 아키텍처가 가진 태생적 한계(동시성, 병목, 단일 장애점, 확장성 제약 등)를 기술적으로 날카롭게 지적한다.
2. **아키텍처 설계 (Architectural Design)**: 해당 문제를 해결하기 위한 글로벌 표준 패턴(CQRS, Event Sourcing, Pub/Sub, Sharding 등)을 도입한다.
3. **실전 구현 (Implementation)**: Java/Spring 환경에서 최적화된 자료구조와 비동기 프로그래밍을 활용해 구현한다.
4. **엔지니어링 인사이트 (Engineering Insight)**: 해결 후 얻은 이점과 새롭게 발생할 수 있는 Trade-off를 분석한다.

## 3. 실무적 아키텍처 로드맵

### [Phase 1] Core: 무결성 거래 로직 (완료)
- **Status**: TreeMap 매칭, RDB 트랜잭션 정산 기초 확립.
- **Challenge**: '동기 방식' 주문 처리의 한계와 데이터 고립 문제 해결.

### [Phase 2] Real-time & Visibility: 초저지연 시세 파이프라인 (Next)
- **Goal**: 서버가 클라이언트에게 즉시 데이터를 푸시하는 구조.
- **Keywords**: WebSocket, STOMP, Message Brokering, OrderBook Snapshotting.
- **Problem**: "조회 시점의 과부하와 HTTP 통신의 오버헤드를 어떻게 제거할 것인가?"

### [Phase 3] Resilience & Scaling: 비동기 메시징 및 분산 아키텍처
- **Goal**: 주문 접수와 매칭의 완전 분리 및 영속성 보장.
- **Keywords**: Redis Streams/Pub-Sub, Kafka, Global State Recovery.
- **Problem**: "서버가 다운되었을 때 메모리 상의 수만 개 주문을 어떻게 1초 만에 복구할 것인가?"

### [Phase 4] Extreme Optimization: 성능 한계 돌파 및 모니터링
- **Goal**: 병목 구간 제로화 및 운영 수준의 가시성 확보.
- **Keywords**: Prometheus/Grafana, JMeter, GC Tuning, Connection Pooling Optimization.

## 4. AI 협업 마인드셋 (AI Senior Architect Mode)
- **타협 없는 코드**: "연습용이니까 이 정도면 됐지"라는 태도를 버리고, 실제 대규모 트래픽을 견딜 수 있는 프로덕션급 코드를 제안할 것.
- **기술적 근거**: 특정 라이브러리나 패턴을 제안할 때, 그것이 왜 '글로벌 최고 수준'의 선택인지 CS 기초 지식과 실무 사례를 바탕으로 증명할 것.
- **아키텍처 설명**: 단순 코드 스니펫 제공보다, 전체 시스템 아키텍처 다이어그램(텍스트 기반)과 데이터 흐름을 먼저 설명할 것.