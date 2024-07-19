# 🛒 E-commerce 프로젝트

## 🚀프로젝트 소개
이 프로젝트는 MSA(Microservices Architecture) 기반의 전자상거래 플랫폼을 구축하는 것을 목표로 합니다. MSA는 각 서비스가 독립적으로 개발되고 배포될 수 있도록 하여 시스템의 유연성과 확장성을 높이는 아키텍처입니다. 이를 통해 다양한 기능들을 보다 효율적으로 관리하고, 빠르게 변화하는 비즈니스 요구사항에 유연하게 대응할 수 있습니다. 주요 기능으로는 상품 관리, 주문 처리 등이 있습니다. 

### 📅프로젝트 실행 기간
2024년 6월 19일 ~ 2024년 7월 17일까지

</br>

## 📚STACKS
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

## 🚀시작 가이드
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
##### 🚨주의
1. 컨테이너가 25개 띄워집니다!!
2. 해당 배포 버전은 이미지 업로드와 이메일 기능은 빠져있습니다.
 
</br>

## 📑API 명세서
### [API 명세서 (Postman)](https://documenter.getpostman.com/view/18677964/2sA3XSBMRR#intro)

</br>

## 🗂ERD
![image](https://github.com/user-attachments/assets/8a73293d-8e34-4a3a-8acb-f16654f9e125)

</br>

## 🏛️Architecture
<img src="https://github.com/user-attachments/assets/2e55d1a6-e3ad-41f6-bcb6-4915675f0e08" width="70%">

</br>

## 🌟 주요 기능
<details>
<summary>주문하기 flow</summary>
  <img src="https://github.com/user-attachments/assets/67a669b2-654a-4fcc-94c6-0a08b84daab8" width="70%">
  <ol>
    <li>레디스에서 재고 차감과 주문서 저장 후 결제 요청 이벤트를 보냄</li>
    <li>결제 컨슈머 서버에서 결제 내역 저장과 실제 결제 요청을 보낸 후 다시 order로 이벤트를 보냄</li>
    <li>결제에 대한 상태값을 받은 order는 rollback 요청을 하거나 배송 요청 이벤트를 보냄</li>
    <li>배송 컨슈머 서버에서 배송 내역 저장과 실제 배송 요청을 보낸 후 다시 order로 이벤트를 보냄</li>
    <li>배송에 대한 상태값을 받은 order는 rollback 요청을 하거나 상품 재고 감소 요청 이벤트를 보냄</li>
    <li>상품 컨슈머 서버에서 실제 db에 있는 상품의 재고 감소 후 다시 order로 이벤트를 보냄</li>
    <li>order에서 재고 감소에 대한 상태값에 따라 rollback 요청을 보냄</li>
  </ol>
</details>

</br>

## 📈 성능 최적화 및 트러블슈팅


