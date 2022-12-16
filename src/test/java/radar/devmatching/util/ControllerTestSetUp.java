package radar.devmatching.util;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Import(ControllerTestConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
public abstract class ControllerTestSetUp {

	protected MockMvc mockMvc;

	@BeforeEach
	void setUp(WebApplicationContext context) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();
	}

}
