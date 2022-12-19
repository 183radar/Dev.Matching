package radar.devmatching.domain.user.service.dto.request;

import javax.validation.constraints.NotBlank;

public class SignInRequest {

	@NotBlank
	private String username;

	@NotBlank
	private String password;

	public SignInRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
