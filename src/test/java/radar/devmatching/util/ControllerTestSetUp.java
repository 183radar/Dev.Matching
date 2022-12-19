package radar.devmatching.util;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Import({ControllerTestConfig.class})
@MockBean(JpaMetamodelMappingContext.class)
@WithCustomUser // 만약 UserRole 변경을 원하면 대상 메서드에 애노테이션을 붙여 설정 변경 가능
public abstract class ControllerTestSetUp {

	protected MockMvc mockMvc;

	@BeforeEach
	void setUp(WebApplicationContext context) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();
	}

}
