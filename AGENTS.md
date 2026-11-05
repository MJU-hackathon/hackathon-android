# mjHackathon Android 개발 지침

홈과 상세 기능을 기준으로 새 API와 화면을 연결할 때 참고하는 팀 가이드다. 아래 Kotlin 파일 경로는 `app/src/main/java/com/cookandroid/mjhackathon/` 기준이다.

## Gradle과 서버 설정

- 라이브러리·플러그인의 버전과 별칭은 `gradle/libs.versions.toml`에서 관리한다. 기존 별칭을 먼저 확인하고 앱 라이브러리는 `app/build.gradle.kts`에서 `implementation(libs.…)`로 참조한다.
- Compose BOM이 관리하는 라이브러리는 개별 버전을 생략한다. 현재 `androidx-compose-ui`도 BOM 버전을 따른다.
- 새 외부 플러그인은 루트 `build.gradle.kts`에 `apply false`로 선언하고 앱 모듈에서 적용한다. AGP 9의 내장 Kotlin, Kotlin serialization 플러그인, Hilt 플러그인과 `ksp(libs.hilt.compiler)` 설정을 사용한다.
- 프로젝트 루트의 `local.properties`에 `BASE_URL="https://example.com/"` 형식으로 실제 서버 주소를 적는다. 현재 Gradle은 값을 그대로 `buildConfigField`에 전달하므로 큰따옴표와 마지막 슬래시를 포함한다.
- 앱 모듈에서 `buildFeatures.buildConfig = true`를 유지하고 `com.cookandroid.mjhackathon.BuildConfig`를 import한다. Manifest에는 인터넷 권한을 선언한다.

## data 레이어 패키지 구조

`com.cookandroid.mjhackathon.data` 아래에서 역할을 먼저 나누고, DTO·Repository·Service는 기능별로 묶는다.

```text
data/
├── di/
│   └── RepositoryModule.kt
├── dto/
│   ├── base/BaseResponse.kt
│   ├── detail/{request,response}/
│   └── home/{request,response}/
├── network/
│   └── NetworkModule.kt
├── repository/
│   ├── detail/  (DetailRepository.kt, DetailRepositoryImpl.kt)
│   └── home/    (HomeRepository.kt, HomeRepositoryImpl.kt)
└── service/
    ├── detail/DetailService.kt
    └── home/HomeService.kt
```

## API 작성 순서

1. **DTO** — 요청은 `dto/<기능>/request`, 응답은 `dto/<기능>/response`에 둔다. `@Serializable data class`로 선언하고 `@SerialName`에 서버 JSON 키를 적는다. 홈과 상세에 동명의 `ExampleResponse`가 있으므로 Service와 Repository에서 같은 기능의 DTO를 import한다.
2. **Service** — `<기능>Service` 인터페이스에 `suspend fun`을 추가한다. 함수에 `@GET`·`@POST`와 API 경로를 지정하고 요청 본문에는 `@Body`를 붙인다. 공통 응답 `BaseResponse<T>`는 `code`, `message`, `data`로 구성된다. 서버의 `data`가 객체이면 `T`는 응답 DTO, 배열이면 `List<응답 DTO>`다.
3. **Repository** — 인터페이스에 호출자가 사용할 함수와 반환 타입을 선언한다. 같은 패키지의 `<기능>RepositoryImpl`은 `@Inject constructor`로 Service를 받고, 조회 함수에서 응답의 `.data`를 반환한다.

상세 조회의 세 선언을 연결하면 다음과 같다. 경로와 DTO 이름은 현재 프로젝트의 예시값이다.

```kotlin
// DetailService.kt
@GET("api/v1/data")
suspend fun getExampleData(): BaseResponse<ExampleResponse>

// DetailRepository.kt
suspend fun getDetail(): ExampleResponse

// DetailRepositoryImpl.kt 내부
override suspend fun getDetail(): ExampleResponse =
    detailService.getExampleData().data
```

## Hilt 등록 기준

두 모듈 모두 `@Module`과 `@InstallIn(SingletonComponent::class)`를 사용한다. `NetworkModule`은 `BuildConfig.BASE_URL`, OkHttpClient, Kotlin serialization converter로 구성한 Retrofit을 공유한다.

| 추가한 항목 | 수정할 모듈 | 작성 규칙 |
|---|---|---|
| 새 Service 인터페이스 | `network/NetworkModule.kt` | `object` 모듈에 `@Provides` 함수를 추가하고 `retrofit.create(DetailService::class.java)`처럼 Service를 생성한다. |
| 새 Repository 인터페이스와 구현체 | `di/RepositoryModule.kt` | `abstract class` 모듈에 `@Binds abstract fun bindDetailRepository(impl: DetailRepositoryImpl): DetailRepository` 형태로 바인딩을 추가한다. |
| 기존 기능의 API 메서드 또는 DTO | 모듈 수정 불필요 | 기존 Service와 Repository 바인딩을 재사용한다. |

## 앱 시작과 내비게이션 파일 지도

| 파일 | 책임과 작성 규칙 |
|---|---|
| `MjHackathonApplication.kt` | `@HiltAndroidApp`이 붙은 Application이다. Manifest에 `.MjHackathonApplication`으로 한 번 등록한다. 새 Service와 Repository의 바인딩은 각 Hilt 모듈에서 관리한다. |
| `presentation/main/MainActivity.kt` | `@AndroidEntryPoint`가 붙은 앱 진입점이다. `enableEdgeToEdge()`를 호출하고 테마 안에서 `rememberMainAppState()`를 얻는다. `Scaffold`의 `innerPadding`을 `MainNavHost`로 전달한다. 실제 패키지는 `com.cookandroid.mjhackathon`이므로 현재 Manifest의 선언은 `.MainActivity`다. |
| `presentation/main/navigation/MainAppState.kt` | `rememberNavController()`로 얻은 컨트롤러와 코루틴 스코프를 보관한다. 화면 이동은 같은 컨트롤러를 재사용한다. |
| `core/navigation/Destination.kt` | 현재 `Home`, `Detail`은 인자가 없는 `@Serializable data object`다. 특정 항목을 여는 요구가 생기면 서버의 ID 타입을 확인해 목적지를 `@Serializable data class`로 바꾸고 송신·수신 코드를 함께 수정한다. |
| `presentation/main/navigation/MainNavHost.kt` | 시작 목적지는 `Home`이다. `composable<Home>`, `composable<Detail>`에서 Route와 이동 콜백을 연결한다. 화면 이동·복귀와 300ms 슬라이드 전환을 관리한다. |

## 화면 구현 예시 · Detail

Screen, UiState, ViewModel을 `presentation/<기능>/`에 모은다. Home과 Detail 모두 Route와 Screen을 같은 `<기능>Screen.kt` 파일에 둔다.

| 상세 예시 파일 | 현재 구현과 적용 규칙 |
|---|---|
| `presentation/detail/DetailUiState.kt` | `data class DetailUiState(val text: String = "text")`가 초기 상태다. 실제 화면이 표시할 데이터와 필요한 로딩·실패 상태를 여기에 정의한다. |
| `presentation/detail/DetailViewModel.kt` | `@HiltViewModel`과 `@Inject constructor`로 `DetailRepository`를 받는다. 내부의 `MutableStateFlow(DetailUiState())`를 외부에 `asStateFlow()`로 공개한다. 현재는 초기 상태만 제공한다. 상세 조회 구현 시 `viewModelScope`에서 `getDetail()`을 호출하고 결과를 상태에 반영한다. |
| `presentation/detail/DetailScreen.kt` | `DetailRoute`가 ViewModel과 화면을 연결한다. `DetailScreen`은 UiState, Modifier, 뒤로가기 콜백을 받는다. Preview에서는 기본 상태와 빈 콜백으로 Screen을 직접 호출한다. |

프로젝트의 Route 패턴은 다음과 같다. `hiltViewModel`은 `androidx.hilt.lifecycle.viewmodel.compose`에서 import한다.

```kotlin
@Composable
fun DetailRoute(
    paddingValues: PaddingValues,
    viewModel: DetailViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DetailScreen(
        uiState = uiState,
        modifier = Modifier.padding(paddingValues),
        onNavigateToBack = navigateToBack,
    )
}
```

- **상태와 여백** — Screen은 받은 상태를 표시하고 받은 Modifier를 최상위 UI에 적용한다. Scaffold 여백은 Route에서 한 번 적용한다.
- **이동 이벤트** — 홈 클릭은 `HomeScreen.onNavigateToDetail → HomeRoute.navigateToDetail → MainNavHost`로 전달해 `navigate(Detail)`을 호출한다. 상세 뒤로가기는 `DetailScreen.onNavigateToBack → DetailRoute.navigateToBack → MainNavHost`로 전달해 `popBackStack()`을 호출한다.
- **새 화면** — 목적지를 선언하고 기능 폴더에 Route·Screen·UiState·ViewModel을 둔 뒤, 그래프에 `composable<목적지>`와 진입 콜백을 연결한다. 데이터가 같으면 기존 Repository를 재사용한다.
- **상세 의존성** — `DetailViewModel → DetailRepository → DetailRepositoryImpl → DetailService` 순서로 연결된다. ViewModel이 조회 함수를 호출해야 실제 요청이 발생한다.

## 아키텍처 원칙

- Google의 권장 Android 앱 아키텍처를 기반으로 하되, 기능의 규모와 요구사항에 맞춰 유연하게 적용한다.
- 단일 Activity 아키텍처(SAA)를 사용한다. `MainActivity`가 Jetpack Compose 화면을 호스팅하고, 화면 이동은 Navigation Compose로 관리한다.
- 화면은 MVVM 패턴으로 구성한다. ViewModel은 Repository를 통해 데이터를 받아 `UiState`를 관리하고, Compose UI는 상태를 표시하고 사용자 이벤트를 전달한다.

<!-- postman-cli:installation:start -->
Postman CLI is installed on this device, and can be used for API Engineering work.
<!-- postman-cli:installation:end -->

## Compose Preview 작성 규칙

- Screen이나 UI 컴포넌트를 만들거나 수정할 때는 같은 Kotlin 파일에 `@Preview`를 작성하고, 상태가 추가되면 Preview도 함께 갱신한다.
- 화면에 다르게 보이는 모든 상태를 Android Studio의 Preview 목록에서 각각 확인할 수 있게 한다. 예를 들어 Screen은 로딩·빈 결과·성공·오류를, 컴포넌트는 선택·미선택·비활성·입력 오류 등을 실제로 지원하는 만큼 포함한다. 여러 조건이 겹쳐 표시가 달라지는 경우 그 조합도 포함한다.
- 상태마다 `@Preview(name = "상세 - 로딩")`처럼 구분되는 이름을 붙이거나, `@PreviewParameter`로 상태별 샘플을 제공한다. 각 Preview는 `MjHackathonTheme` 안에서 고정 샘플 데이터와 빈 콜백으로 Screen 또는 컴포넌트를 직접 호출한다.


## 이미지 처리 및 서버 통신 규칙

### 1. 아키텍처
- 기존 `core / data / presentation` 아키텍처를 유지한다.
- 별도의 UseCase 계층을 도입하지 않는다.
- 이미지 업로드 및 조회 API 통신에는 Retrofit을 사용한다.
- Jetpack Compose에서 로컬 및 원격 이미지를 표시할 때는 Coil을 사용한다.
- Composable 내부에서 직접 네트워크 요청을 수행하거나 Multipart 변환 로직을 작성하지 않는다.
- ViewModel은 이미지 관련 UI 상태를 관리하고, 데이터 처리는 Repository에 위임한다.
- Repository는 이미지 업로드 및 조회 기능을 추상화하며, Repository 구현체에서 Retrofit API를 호출한다.

### 2. 촬영 결과 처리

- 일기 사진은 앱 내부 CameraX로 직접 촬영한 사진만 사용한다.
- 갤러리 선택, Photo Picker, 외부 카메라 앱 실행은 제공하지 않는다.
- 촬영 구현은 아래의 `직접 사진 촬영` 지침을 따른다.
- 촬영 결과는 파일 또는 Uri로 관리한다.
- 미리보기만을 위해 Bitmap으로 변환하지 않는다.
- 사진 미리보기는 Coil의 AsyncImage를 사용한다.

### 3. 이미지 업로드 (POST)
- 이미지 업로드에는 Retrofit의 `multipart/form-data` 방식을 사용한다.
- API 인터페이스에서는 `@Multipart`, `@POST`, `@Part`를 사용한다.
- 이미지 업로드 시 선택된 Uri를 `RequestBody` 및 `MultipartBody.Part`로 변환한다.
- Uri에서 이미지 데이터를 읽을 때는 `ContentResolver`를 사용한다.
- Multipart 변환 로직은 Data 계층에서 처리하며, ViewModel이나 Composable에 작성하지 않는다.
- Multipart의 파라미터 이름은 백엔드 API 명세와 일치시킨다.
- 이미지의 MIME 타입과 확장자를 고려하여 적절한 파일명을 사용한다.
- 파일 읽기와 같은 블로킹 I/O 작업은 `Dispatchers.IO`에서 처리한다.
- 대용량 이미지 업로드 시 파일 전체를 메모리에 적재하는 방식을 지양한다.
- 업로드 중 로딩 상태와 실패 상태를 UI에 반영한다.
- 업로드 성공 시 서버에서 반환한 응답 데이터를 UI 상태에 반영한다.

### 4. 이미지 조회 (GET)
- 이미지 조회는 Retrofit의 `@GET`을 사용한다.
- 기본적으로 백엔드가 이미지 URL을 포함한 JSON 응답을 반환한다고 가정한다.
- API 응답은 DTO로 정의하고, 필요한 경우 UI에서 사용하는 모델로 변환한다.
- 서버에서 반환한 이미지 URL은 Coil의 `AsyncImage(model = imageUrl)`로 표시한다.
- Coil로 처리할 수 있는 이미지를 직접 다운로드하거나 Bitmap으로 디코딩하지 않는다.
- GET 요청에는 Multipart를 사용하지 않는다.
- 백엔드에서 이미지 바이너리를 직접 반환하는 경우에는 실제 API 명세에 맞게 별도로 구현한다.

### 5. Compose UI
- 로컬 Uri 및 서버 이미지 URL을 표시할 때는 Coil의 `AsyncImage` 사용을 우선한다.
- Compose의 일반 `Image`에 Uri를 Painter로 직접 전달하지 않는다.
- 앱 내부 Drawable 리소스는 필요에 따라 `painterResource`를 사용한다.
- 일반 `Image`와 Coil을 함께 사용해야 하는 경우 `rememberAsyncImagePainter`를 사용할 수 있다.
- 필요한 경우 이미지 로딩, 실패 및 Placeholder UI를 구현한다.
- Route에서는 이미지 선택 및 ViewModel과의 연결을 담당한다.
- Screen에서는 전달받은 UI State와 이벤트 콜백을 사용하여 UI를 구성한다.
- 이미지 관련 비즈니스 로직을 Screen에 작성하지 않는다.

### 6. 의존성 관리
- 라이브러리 버전은 기존 Gradle Version Catalog를 통해 관리한다.
- 이미지 로딩 라이브러리는 Coil 3을 사용한다.
- Compose 이미지 표시를 위해 `coil-compose`를 사용한다.
- 네트워크 이미지 로딩을 위해 `coil-network-okhttp`를 사용한다.
- API 통신에는 기존 Retrofit 및 OkHttp 설정을 재사용한다.
- 이미 추가된 라이브러리는 중복으로 선언하지 않는다.
- 새로운 의존성을 추가할 때는 기존 프로젝트의 Gradle 설정 방식과 버전 호환성을 확인한다.

## 추가 라이브러리 사용 지침

카메라 권한과 Google Maps API 키 연결이 완료된 상태를 전제로 한다.
기존 Route·Screen·UiState·ViewModel·Repository 구조를 유지한다.

### 화면 상태 수집

- ViewModel은 기존 hiltViewModel()로 가져온다.
- Route에서 lifecycle-runtime-compose의 collectAsStateWithLifecycle()로
  uiState를 수집한다.
- Screen에는 상태와 이벤트 콜백을 전달한다.
- 화면 이동은 기존 MainNavHost에서 처리한다.

### 현재 위치와 주변 퀘스트

- play-services-location의 FusedLocationProviderClient를 사용한다.
- 위치 조회는 Repository로 분리하고 ViewModel에서 호출한다.
- getCurrentLocation()으로 좌표를 얻은 후 주변 퀘스트 API를 호출한다.
- 대략적 위치만 허용된 경우에도 가능한 범위에서 처리한다.
- 권한 거부, 위치 서비스 꺼짐, null 결과, 조회 실패를 처리한다.
- 재구성마다 위치 조회나 API 호출이 반복되지 않게 한다.
- 백그라운드 위치 추적은 구현하지 않는다.
- Task.await()는 현재 의존성에 포함되어 있다고 가정하지 않는다.
  Task 콜백 또는 취소 처리를 포함한 코루틴 래퍼를 사용할 수 있다.

### 퀘스트 장소 지도

- maps-compose의 GoogleMap으로 지도를 표시한다.
- 서버에서 받은 장소 위도·경도로 Marker를 표시한다.
- rememberCameraPositionState로 지도 시점을 관리한다.
- 장소가 처음 준비되거나 선택 장소가 바뀔 때 시점을 이동한다.
- 일반적인 재구성으로 사용자가 움직인 지도 시점을 초기화하지 않는다.
- 현재 위치 레이어는 위치 권한이 있을 때만 활성화한다.
- 위치 권한이 없어도 퀘스트 장소 지도와 핀은 표시한다.
- 서버가 장소 좌표를 제공하므로 별도 장소 검색 SDK를 추가하지 않는다.
- 지도 객체를 ViewModel에 보관하지 않는다.

### 직접 사진 촬영

- camera-view의 PreviewView를 AndroidView로 화면에 배치한다.
- camera-core의 Preview와 ImageCapture를 사용한다.
- camera-lifecycle의 ProcessCameraProvider로 화면의 LifecycleOwner에 연결한다.
- camera-camera2는 CameraX의 카메라 구현을 제공한다.
- Preview의 SurfaceProvider를 PreviewView에 연결한다.
- 촬영 버튼에서 ImageCapture.takePicture()를 호출한다.
- 사진은 앱 전용 파일에 저장하고 파일 또는 URI로 관리한다.
- 사진 확인 화면은 기존 Coil로 표시한다.
- 다시 찍기와 사용하기를 제공한다.
- 카메라 화면 이탈 시 연결한 use case를 해제한다.
- 비동기 초기화가 화면 이탈 후 완료되어도 다시 연결하지 않는다.
- PreviewView, LifecycleOwner, 카메라 객체는 ViewModel에 보관하지 않는다.
- 갤러리 선택과 외부 카메라 앱 실행은 제공하지 않는다.

### 촬영 결과와 일기 저장

- 직접 촬영 사진과 경험 여부는 필수이며 한 줄 메모는 선택이다.
- 촬영 → 사진 확인 → 경험 여부·선택 메모 → 저장 순서로 연결한다.
- 업로드는 기존 Retrofit·OkHttp와 Repository를 사용한다.
- multipart 또는 업로드 URL 방식은 실제 서버 명세를 따른다.
- 저장 중 중복 요청을 막는다.
- 저장 실패 시 사진·메모·경험 여부를 유지한다.
- 다시 찍기·취소·저장 완료 후 불필요한 임시 파일을 정리한다.
- 못 경험했다도 정상 저장하며 재도전을 허용한다.
- 재도전으로 새 일기를 저장해도 이전 일기는 유지한다.
- 패스에는 사진 촬영이나 일기 저장을 요구하지 않는다.

### 체류 날짜 선택

- 기존 Material 3의 DatePicker와 DatePickerDialog를 사용한다.
- 시작일·종료일 입력 여부와 날짜 순서를 검증한다.
- DatePicker의 UTC 기준 밀리초를 날짜로 변환할 때 날짜 밀림에 유의한다.
- 선택 날짜는 가입 API로 저장하고 홈은 GET 응답으로 표시한다.
- 백엔드가 D-day 계산값을 제공하면 앱에서 중복 계산하지 않는다.
- 체류 날짜를 퀘스트 추천 조건으로 사용하지 않는다.

### 권한 처리

- MainActivity에서 앱 진입 시 RequestMultiplePermissions와
  LaunchedEffect(Unit)를 사용해 카메라·위치 권한을 요청한다.
- 정밀 위치와 대략적 위치 권한은 함께 요청한다.
- 거부 안내, 재시도 다이얼로그, 설정 이동 UI는 구현하지 않는다.
- 실제 카메라 실행과 위치 조회 직전에는 현재 권한을 확인한다.
- 권한이 없으면 해당 기능을 실행하지 않는다.
- 대략적 위치만 허용되어도 가능한 범위에서 위치를 조회한다.
- 시스템이 재요청을 차단한 권한창을 강제로 띄우지 않는다.
