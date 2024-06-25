package org.example.projectspringfalling.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.projectspringfalling._core.errors.exception.Exception404;
import org.example.projectspringfalling.userSubscription.UserSubscription;
import org.example.projectspringfalling.userSubscription.UserSubscriptionRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.example.projectspringfalling._core.utils.DateUtil.formatDate;
import static org.example.projectspringfalling._core.utils.DateUtil.formatYear;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 카카오 로그아웃
    @Transactional
    public void logoutKakao(SessionUser sessionUser, HttpServletRequest request) {
        // RestTemplate 설정
        RestTemplate rt = new RestTemplate();

        // http header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization", "Bearer " + sessionUser.getAccessToken());

        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(headers);

        // api 요청하기
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v1/user/unlink",
                HttpMethod.POST,
                req,
                String.class);

        // Redis에서 세션 유저 정보 삭제
        HttpSession session = request.getSession();
        String redisKey = "sessionUser:" + session.getId();
        redisTemplate.delete(redisKey);

        System.out.println("로그아웃 성공");
    }

    // 회원가입
    @Transactional
    public UserResponse.JoinDTO join(UserRequest.JoinDTO reqDTO) {
        User joinUser = userRepository.save(reqDTO.toEntity());
        return new UserResponse.JoinDTO(joinUser);
    }

    // 로그인
    @Transactional
    public SessionUser login(UserRequest.LoginDTO reqDTO) {
        User sessionUser = userRepository.findByEmailAndPassword(reqDTO.getEmail(), reqDTO.getPassword())
                .orElseThrow(() -> new Exception404("존재 하지 않는 회원입니다"));
        return new SessionUser(sessionUser);
    }

    // 카카오 로그인
    @Transactional
    public SessionUser kakaoLogin(String code) {
        // 1. code로 카카오에서 토큰 받기 (위임 완료) - OAuth2.0
        // RestTemplate 설정
        RestTemplate rt = new RestTemplate();

        // http header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // http body 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "de886838ff478f3a5b83c56cdb2dd412");
        body.add("redirect_uri", "http://localhost:8080/oauth/callback/kakao");
        body.add("code", code);

        // body+header 객체 만들기
        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        // api 요청하기 (토큰 받기)
        ResponseEntity<UserResponse.KakaoTokenDTO> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                request,
                UserResponse.KakaoTokenDTO.class);

        // 값 확인
        System.out.println(response.getBody().toString());

        // 2. 토큰으로 사용자 정보 받기
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers2.add("Authorization", "Bearer " + response.getBody().getAccessToken());

        HttpEntity<MultiValueMap<String, String>> request2 =
                new HttpEntity<>(headers2);

        ResponseEntity<UserResponse.KakaoUserDTO> response2 = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                request2,
                UserResponse.KakaoUserDTO.class);

        System.out.println("response2 : " + response2.getBody().toString());

        // 3. 해당 정보로 DB 조회
        String username = "kakao_" + response2.getBody().getId();
        User userPS = userRepository.findByEmail(username + "@nate.com");

        // 4. 있으면 조회된 유저 정보를 리턴, 없으면 강제 회원가입
        if (userPS != null) {
            return new SessionUser(userPS, response.getBody().getAccessToken());
        } else {
            // 강제 회원가입
            User user = User.builder()
                    .email(response2.getBody().getProperties().getNickname() + "@nate.com")
                    .password(UUID.randomUUID().toString())
                    .provider("Kakao")
                    .build();
            User returnUser = userRepository.save(user);
            return new SessionUser(returnUser, response.getBody().getAccessToken());
        }
    }

    // 네이버 로그인
    @Transactional
    public SessionUser naverLogin(String code) {
        // 1. code로 네이버에서 토큰 받기 (위임 완료) - OAuth2.0
        // RestTemplate 설정
        RestTemplate rt = new RestTemplate();

        // http header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // http body 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", "B_iv0qTaEJVdKWa_FPzy");
        body.add("client_secret", "Svp3dM07pF");
        body.add("grant_type", "authorization_code");
        body.add("state", "1234"); // 개발단계라서 일단 임의로 1234 설정
        body.add("code", code);

        // body+header 객체 만들기
        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        // api 요청하기 (토큰 받기)
        ResponseEntity<UserResponse.NaverTokenDTO> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token?",
                HttpMethod.POST,
                request,
                UserResponse.NaverTokenDTO.class);

        // 값 확인
        System.out.println(response.getBody().toString());

        // 2. 토큰으로 사용자 정보 받기
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers2.add("Authorization", "Bearer " + response.getBody().getAccessToken());

        HttpEntity<MultiValueMap<String, String>> request2 =
                new HttpEntity<>(headers2);

        ResponseEntity<UserResponse.NaverUserDTO> response2 = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                request2,
                UserResponse.NaverUserDTO.class);

        System.out.println("response2 : " + response2.getBody().toString());

        // 3. 해당 정보로 DB 조회
        String email = "naver_" + response2.getBody().getResponse().getEmail();
        User userPS = userRepository.findByEmail(email);

        // 4. 있으면 조회된 유저 정보를 리턴, 없으면 강제 회원가입
        if (userPS != null) {
            return new SessionUser(userPS, response.getBody().getAccessToken());
        } else {
            User user = User.builder()
                    .email(email)
                    .birth(formatYear(response2.getBody().getResponse().getBirthyear()) + formatDate(response2.getBody().getResponse().getBirthday()))
                    .password(UUID.randomUUID().toString())
                    .phone(response2.getBody().getResponse().getMobile())
                    .provider("Naver")
                    .build();
            User returnUser = userRepository.save(user);
            return new SessionUser(returnUser, response.getBody().getAccessToken());
        }
    }

    // 프로필에 활성화된 이용권 이름 넣기
    public Optional<UserSubscription> getActiveUserSubscription(Integer userId) {
        return userSubscriptionRepository.findByUserIdAndStatus(userId, "ACTIVE");
    }
}