# <img width="30" alt="docker logo" src="https://github.com/user-attachments/assets/72091dc1-2915-4b35-bec9-efe983e4b083"> RestDocker

### RestDocker 란
RestApi를 통해 Docker 의 기본 개념(도커 이미지, 컨테이너, 네트워크 등)을 학습할 수 있도록 서비스이다.

<br>


## 💁‍♂ 프로젝트 소개

### **서비스 Identiity** 
Docker 를 자유롭게 공부할 수 있는 환경을 제공하는 서비스. Docker 서비스를 시스템 단이 아닌 RestApi 와의 통신으로 구현합니다.

### **프로젝트 우선순위** 

> 프로젝트 우선순위는 기술적 Trade-Off 인 상황에서 근거있는 선택을 할수있게 해줍니다. <br>
> 1. 확장성
> 2. 가독성
> 3. 성능
> 4. 보안성 <br>
> * Docker 에 대해 공부를 하면서 구현하는 프로젝트입니다. 따라서 언제 어떻게 요구사항이 변경될지 모르기에 확장성이 가장 중요시되는 가치입니다.

<br>

## 🏗 구현 기능

1. **회원 관리**
   - 카카오 로그인 / 네이버 로그인
   - 인증토큰 갱신
   - 회원가입 시, bridge 네트워크 기본 할당
2. **도커 이미지**
   - 이미지 전체 조회
   - 특정 이미지 자세히 조회
   - DockerHub에서 Image 가져오기
   - 이미지 삭제
3. **도커 네트워크**
   - Host의 네트워크 전체 조회
   - Host의 특정 네트워크 자세히 조회
   - Host의 네트워크 생성
   - Host의 네트워크 삭제
4. **도커 컨테이너**
   - 실행 상태의 컨테이너만 조회 ( 상태에 관련없이 전체 조회, 마지막 수정 컨테이너 조회 )
   - 특정 컨테이너 자세한 조회
   - 컨테이너 생성
   - 컨테이너 삭제
   - 컨테이너 실행
   - 컨테이너 시작
   - 컨테이너 종료

<br>

## 🛠 기술 스택

<img width="834" alt="기술 스택" src="https://github.com/user-attachments/assets/b7f971d1-9fc8-40b9-8e37-ad00e2f90043">

<br><br>

## ⚙️ 아키텍처
<img width="500" alt="아키텍처" src="https://github.com/user-attachments/assets/b783a49f-fc7c-4e61-a3f9-e4ad003f8acd">


<br><br>

## 🎨 UI

### Account

|   로그인 페이지   |
| ------------- |
| <img width="250" height="340" alt="도커 화면" src="https://github.com/user-attachments/assets/53976c6a-6827-4c43-947a-5f9efae731f0"> |


### Docker Image

|   Docker Image Ls   |   Docker Image Inspect   |
| ------------- | ------------- |
| <img width="550" alt="이미지 조회" src="https://github.com/user-attachments/assets/7ce31986-5ef9-4997-bb8c-ff4bf02f05cc"> | <img width="380" alt="자세한 이미지 조회" src="https://github.com/user-attachments/assets/98bffa72-b3a4-415b-9f1c-79b874554301"> |

|   Docker Image Pull   |   Docker Image Rm   |
| ------------- | ------------- |
| <img width="380" alt="이미지 가져오기" src="https://github.com/user-attachments/assets/2d0a5c89-2e9f-406e-8802-21a1a4d946f4"> | <img width="380" alt="이미지 삭제" src="https://github.com/user-attachments/assets/264b3659-df28-462a-9e88-e2f19d2b551d"> |

### Docker Network

|   Docker Network Ls   |   Docker Network Inspect   |
| ------------- | ------------- |
| <img width="380" alt="네트워크 조회" src="https://github.com/user-attachments/assets/71124797-c117-4684-bbe3-39e623815869"> | <img width="465" alt="네트워크 자세한 조회" src="https://github.com/user-attachments/assets/bb4db8a1-9414-4b56-9a94-1da05c9c0e18"> |

|   Docker Network Create   |   Docker Network Rm   |
| ------------- | ------------- |
| <img width="380" alt="네트워크 생성" src="https://github.com/user-attachments/assets/17cc2593-4cc8-40a2-bd21-482b1694c35f"> | <img width="380" alt="네트워크 삭제" src="https://github.com/user-attachments/assets/cccff03e-6844-4161-961c-cc49890bad62"> |

### Docker Container

|   Docker Container Ls   |   Docker Container Inspect   |
| ------------- | ------------- |
| <img width="470" alt="컨테이너 전체 조회" src="https://github.com/user-attachments/assets/eb1c66ba-5cc8-432c-b670-bd7238f858f0"> | <img width="380" alt="자세한 컨테이너 조회" src="https://github.com/user-attachments/assets/f680cc28-586c-4557-9d4e-ad83ec1b1503"> |

|   Docker Container Create   |   Docker Container Rm   |
| ------------- | ------------- |
| <img width="380" alt="컨테이너 생성" src="https://github.com/user-attachments/assets/75b8828a-fd15-451f-aefe-5d1997e3c320"> | <img width="380" alt="컨테이너 삭제" src="https://github.com/user-attachments/assets/6edd741e-4143-4eef-8090-5e44e313dc31"> |

|   Docker Container Rename   |   Docker Container Run   |
| ------------- | ------------- |
| <img width="380" alt="컨테이너 이름 변경" src="https://github.com/user-attachments/assets/0c167b16-8a79-43bb-a6e6-16c4dd3179e3"> | <img width="380" alt="컨테이너 실행" src="https://github.com/user-attachments/assets/ee1b6cd0-7583-487e-9b44-7a86a49e54db"> |

|   Docker Container Start   |   Docker Container Stop   |
| ------------- | ------------- |
| <img width="380" alt="컨테이너 시작" src="https://github.com/user-attachments/assets/69b9e1f6-355e-44c8-88a9-dde55a9382ae"> | <img width="380" alt="컨테이너 종료" src="https://github.com/user-attachments/assets/c382232a-2145-480d-a564-cc21ef837f77"> |
