package radar.devmatching.domain.user.service;

import radar.devmatching.domain.user.service.dto.response.SignInResponse;
import radar.devmatching.domain.user.service.dto.response.SignOutResponse;

public interface AuthService {
	SignInResponse signIn(String username, String password);

	SignOutResponse singOut();
}
