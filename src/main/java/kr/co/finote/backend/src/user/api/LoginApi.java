package kr.co.finote.backend.src.user.api;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import kr.co.finote.backend.global.authentication.oauth.google.GoogleOauth;
import kr.co.finote.backend.global.authentication.oauth.google.dto.request.GoogleAccessTokenRequest;
import kr.co.finote.backend.global.authentication.oauth.google.dto.response.GoogleLoginResponse;
import kr.co.finote.backend.global.authentication.oauth.google.dto.response.GoogleOauthUserInfoResponse;
import kr.co.finote.backend.src.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class LoginApi {

    private final GoogleOauth googleOauth;
    private final LoginService loginService;

    // TODO : Front에서 Access Token을 위한 코드 발급 완성 시 삭제
    @GetMapping("/login-google")
    public void loginGoogle(HttpServletResponse response) {
        log.info("/login-google");

        try {
            response.sendRedirect(googleOauth.getOauthRedirectURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/auth/google/")
    public GoogleLoginResponse auth(@RequestParam String code) {
        log.info("Code from Google social Login API {}", code);

        GoogleAccessTokenRequest googleAccessToken = loginService.getGoogleAccessToken(code);
        GoogleOauthUserInfoResponse GoogleOauthUserInfo =
                loginService.getGoogleUserInfo(googleAccessToken);

        Boolean newUser = loginService.saveUser(GoogleOauthUserInfo);

        GoogleLoginResponse response =
                GoogleLoginResponse.builder()
                        .accessToken(googleAccessToken.getAccessToken())
                        .refreshToken(googleAccessToken.getRefreshToken())
                        .newUser(newUser)
                        .build();

        return response;
    }
}
