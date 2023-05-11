# jwp-subway-path

## 비즈니스 요구사항

### 노선에 역 추가

- [x] 노선에 역이 존재하지 않으면 새로운 역을 2개 입력받아 추가한다.
- [x] 노선에 이미 역이 존재할 경우 입력으로 들어온 2개의 역이 노선에 존재하지 않으면 예외가 발생한다.
- [x] 종점에 추가할 경우 새로운 섹션 하나가 생긴다.
- [x] 기존 섹션 중간에 역을 추가할 경우 기존 섹션을 지우고 새로운 섹션 2개를 생성한다.
- [x] 입력으로 들어온 역이 모두 기존 노선에 존재할 경우 예외가 발생한다.
- [x] 노선에 역 추가 시 입력 순서는 상행역, 하행역으로 한다.
- [x] 중간에 새로운 역을 추가할 경우 새롭게 추가되는 섹션의 길이는 기존 섹션보다 작아야 한다.
- [x] 섹션의 길이는 양의 정수만 혀용한다.

### 노선에 역 제거

- [x] 노선 정보와, 지울 역을 입력받아 진행한다.
- [x] 노선에 존재하지 않는 역을 삭제하려 하면 예외가 발생한다.
- [x] 종점 역을 지울 경우 단순히 해당 섹션만 제거한다.
- [x] 중간 역을 지울 경우 지울 역과 연결된 2개의 섹션을 제거하고, 새로운 섹션이 하나 생긴다.
    - ex) A -> B -> C 에서 B를 지울 경우, A -> C 섹션이 새로 생긴다.

## API

### 지하철 역

- [x] /stations POST : 지하철 역 추가
- [x] /stations/{id} GET : 지하철 역 조회

### 지하철 노선

- [x] /lines POST : 노선 추가
- [x] /lines/{id} GET : id에 맞는 노선 조회

### 지하철 경로(섹션)

- [x] /sections POST : 섹션 추가
- [ ] /sections GET : 전체 섹션 조회
