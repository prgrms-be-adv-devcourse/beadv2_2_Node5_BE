import com.node5.common.domain.ApiResponseDto;
import com.node5.memberservice.auth.application.AuthService;
import com.node5.memberservice.auth.application.dto.LoginInfo;
import com.node5.memberservice.auth.presentation.dto.OAuthLoginRequest;
import com.node5.memberservice.auth.presentation.dto.OAuthRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "OAuth 로그인", description = "OAuth 로그인을 처리합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @PostMapping("/oauth/login")
    public ResponseEntity<ApiResponseDto<LoginInfo>> oAuthLogin(@RequestBody OAuthLoginRequest request) {
        return authService.login(request.toCommand());
    }

    @Operation(summary = "OAuth 회원가입", description = "OAuth 회원가입을 처리합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    @PostMapping("/oauth/register")
    public ResponseEntity<ApiResponseDto<LoginInfo>> register(@RequestBody OAuthRegisterRequest request) {
        return authService.register(request.toCommand());
    }
}
