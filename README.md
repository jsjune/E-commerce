# 🛒 E-commerce 프로젝트

## 🌟 프로젝트 소개
이 프로젝트는 MSA(Microservices Architecture) 기반의 전자상거래 플랫폼을 구축하는 것을 목표로 합니다. MSA는 각 서비스가 독립적으로 개발되고 배포될 수 있도록 하여 시스템의 유연성과 확장성을 높이는 아키텍처입니다. 이를 통해 다양한 기능들을 보다 효율적으로 관리하고, 빠르게 변화하는 비즈니스 요구사항에 유연하게 대응할 수 있습니다. 주요 기능으로는 상품 관리, 주문 처리 등이 있습니다. 

### 📅 프로젝트 실행 기간
2024년 6월 19일 ~ 2024년 7월 17일까지

</br>

## 📚 STACKS
<div align=center> 
  <img src="https://img.shields.io/badge/java%2017-007396?style=for-the-badge&logo=java&logoColor=white"> 
  <img src="https://img.shields.io/badge/springboot%203.2.6-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white">
  <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">
</br>
  <img src="https://img.shields.io/badge/spring%20cloud%20eureka-FF4A6E?style=for-the-badge&logo=spring&logoColor=white">
  <img src="https://img.shields.io/badge/spring%20cloud%20gateway-00A896?style=for-the-badge&logo=spring&logoColor=white">
  <img src="https://img.shields.io/badge/spring%20cloud%20resilience4j-3D5A80?style=for-the-badge&logo=spring&logoColor=white">
  <img src="https://img.shields.io/badge/spring%20data%20jpa-6D597A?style=for-the-badge&logo=spring&logoColor=white">
  <img src="https://img.shields.io/badge/querydsl--jpa-EE964B?style=for-the-badge&logo=querydsl&logoColor=white">
</br>
  <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/docker--compose-F7A81B?style=for-the-badge&logo=docker&logoColor=white">
</div>

</br>

## 🚀 시작 가이드
#### 📥설치
```
$ git clone --branch local-deploy https://github.com/jsjune/E-commerce.git
$ cd E-commerce
```
#### ▶️실행
```
$ ./gradlew docker
$ docker-compose up -d
```
#### 🚨주의
1. 컨테이너가 25개 띄워집니다!!
2. 해당 배포 버전은 이미지 업로드와 이메일 기능은 빠져있습니다.
 
</br>

## 📑 API 명세서
### [API 명세서 (Postman)](https://documenter.getpostman.com/view/18677964/2sA3XSBMRR#intro)

</br>

## 🗂 ERD
#### Monolithic
<div>
  <img src="https://github.com/user-attachments/assets/a16ee884-489a-46a0-92e3-ab1bf8927eac" width="70%">
</div>

#### MSA
<div>
  <img src="https://github.com/user-attachments/assets/8a73293d-8e34-4a3a-8acb-f16654f9e125" width="70%">
</div>

</br>

## 🏛️ Architecture
<div>
  <img src="https://github.com/user-attachments/assets/2e55d1a6-e3ad-41f6-bcb6-4915675f0e08" width="70%">
</div>

</br>

## 🌟 주요 기능
#### 주문하기 - EDA (분산 트랜잭션) [<ins>자세히 보기</ins>](https://jeongburgger.notion.site/3-EDA-6dce1ca4c75a479caac0c514c9b211b2)
<details>
  <summary>주문하기 flow</summary>
  <img src="https://github.com/user-attachments/assets/67a669b2-654a-4fcc-94c6-0a08b84daab8" width="70%">
  <ol>
    <li>레디스에서 재고 차감과 상품 정보 기반 이벤트 발송</li>
    <li>해당 이벤트를 받아 주문서 저장 후 결제 요청 이벤트를 보냄</li>
    <li>결제 컨슈머 서버에서 결제 내역 저장과 실제 결제 요청을 보낸 후 다시 order로 이벤트를 보냄</li>
    <li>결제에 대한 상태값을 받은 order는 rollback 요청을 하거나 배송 요청 이벤트를 보냄</li>
    <li>배송 컨슈머 서버에서 배송 내역 저장과 실제 배송 요청을 보낸 후 다시 order로 이벤트를 보냄</li>
    <li>배송에 대한 상태값을 받은 order는 rollback 요청을 하거나 상품 재고 감소 요청 이벤트를 보냄</li>
    <li>상품 컨슈머 서버에서 실제 db에 있는 상품의 재고 감소 후 다시 order로 이벤트를 보냄</li>
    <li>order에서 재고 감소에 대한 상태값에 따라 rollback 요청을 보냄</li>
  </ol>
</details>
<details>
  <summary>카프카 네트워크 장애가 난다면?</summary>
  <pre><code>
@KafkaListener(topics = "${consumers.topic1}", groupId = "${consumers.groupId}")
public void consumeOrderFromPayment(ConsumerRecord<String, String> record) {
    try {
        EventResult eventResult = objectMapper.readValue(record.value(), EventResult.class);
        if (eventResult.status() == -1) {
            orderKafkaService.handleRollbackOrderFromPayment(eventResult);
        } else {
            if (kafkaHealthIndicator.isKafkaUp()) {
                orderKafkaService.handleOrderFromPayment(eventResult);
            } else {
                log.error("Failed to send payment event");
                orderKafkaService.occurDeliveryFailure(eventResult);
            }
        }
    } catch (Exception e) {
        log.error("Failed to consume order from payment", e);
        throw new RuntimeException("Failed to consume order from payment");
    }
}
  </code></pre>
  <ol>
    <li>Kafka health check 수행</li>
    <li>통신 가능 시 정상적으로 publish</li>
    <li>통신 불가 시 이벤트를 DB에 저장</li>
    <ul>
      <li>스케줄러를 이용해 카프카와의 통신 상태 주기적으로 체크</li>
      <li>통신 가능 시 DB에 있는 이벤트 정상적으로 전송</li>
    </ul>
  </ol>
</details>
  
</br>

## 📈 성능 최적화 및 트러블슈팅
### 🕒성능 최적화
1. Monolithic에서 MSA로 전환 [<ins>자세히 보기</ins>](https://jeongburgger.notion.site/monolithic-msa-e63e65abcc1c47118bcf16022bad421a)
    - 테스트는 로컬에서 jmeter를 사용해서 테스트를 진행했습니다. 
    - 조건은 100초 동안 점진적으로 사용자가 증가하는 조건으로 주문하기에 대한 요청 테스트를 진행했습니다.

    |  | 모놀리식 아키텍처 | MSA (동기) | MSA (EDA, 비동기) |
    | --- | --- | --- | --- |
    | 3000명일 때 평균 응답 | 6.3초 | 13초 | 0.141초 |
    | 5000명일 때 평균 응답 | 42초 | 137초 | 0.196초 |
    | TPS 그래프 | 불규칙적, <br>평균 22 ~ 25 TPS | 불규칙적, <br>평균 15 ~ 20 TPS | 일정, <br>평균 30 ~ 50 TPS |
    | Latency 그래프 | 우상향 | 우상향 | 대부분 평온 |

    - 이를 통해 EDA 기반 비동기 MSA가 성능 면에서 가장 우수함을 확인할 수 있습니다.

2. 검색 조회 성능 개선 [<ins>자세히 보기</ins>](https://jeongburgger.notion.site/31787fcbc3fa47178d753db7855e78d7)
    - 500만개의 데이터를 기준으로 테스트 했습니다.
  
    |  | 응답 시간 | 응답 속도 개선 정도 |
    | --- | --- | --- |
    | 인덱스  | 8500ms |  |
    | 커버링 인덱스 | 900ms | 944% 빨라짐 |
    | 캐싱 | 15ms | 56666% 빨라짐 |

3. 이메일 인증 코드 보내기 속도 개선  
    - `ApplicationEventPublisher`을 사용하여 비동기통신을 사용하여 개선
    - 12~14초 → 30ms, 433배 속도 개선

4. 상품 등록 속도 개선
    - `ApplicationEventPublisher`을 사용하여 비동기통신을 사용하여 개선
    - 5초 -> 100ms, 50배 속도 개선

### 🛠️트러블 슈팅
- 분산 환경에서 재고 감소에 대한 동시성 문제 [<ins>자세히 보기</ins>](https://jeongburgger.notion.site/fadcbd5a4ed04726a13bbac744a380f0)
  - 기본적으로 파티션을 하나로 하고 컨슈머를 하나만 띄웠을 경우, 동시성 문제가 일어나지 않는다.
  - 하지만 성능을 위해 여러개의 파티션과 그에 맞는 컨슈머 서버를 띄우게 된다.
  - 그렇게 되면 동시에 동일한 데이터를 조회하게 되어 동시성 문제가 터질 수 있다.
  - **해결 방안**: 레디스 분산락 적용 </br>
    <img src="https://github.com/user-attachments/assets/b036c091-ef74-40d3-9822-e349da71e3ee" width="70%">
- 이미지 업로드 비동기 통신으로 변환 과정에서 문제 발생
  - MultipartFile을 이벤트로 보내고 Listener에서 해당 파일을 처리하는 과정에서 NoSuchFileException이 발생
  - **해결 방안**: MultipartFile을 바이트 배열로 변환하여 이벤트를 보내서 해결
