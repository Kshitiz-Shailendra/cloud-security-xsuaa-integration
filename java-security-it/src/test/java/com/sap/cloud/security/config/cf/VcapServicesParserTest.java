package com.sap.cloud.security.config.cf;

import com.sap.cloud.security.config.OAuth2ServiceConfiguration;
import com.sap.cloud.security.json.JsonParsingException;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static com.sap.cloud.security.config.cf.CFConstants.XSUAA.UAA_DOMAIN;
import static com.sap.cloud.security.config.cf.CFConstants.XSUAA.VERIFICATION_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VcapServicesParserTest {

	@Test
	void fromFile_loadsConfiguration() {
		OAuth2ServiceConfiguration oAuth2ServiceConfiguration = VcapServicesParser
				.fromFile("/vcapServices/vcapSimple.json")
				.createConfiguration();

		assertThat(oAuth2ServiceConfiguration.getClientId()).isEqualTo("clientId");
		assertThat(oAuth2ServiceConfiguration.getProperty(CFConstants.SERVICE_PLAN)).isEqualTo("broker");
	}

	@Test
	void fromFile_loadsConfiguration_PropertiesAreOverridden() {
		OAuth2ServiceConfiguration oAuth2ServiceConfiguration = VcapServicesParser
				.fromFile("/vcapServices/vcapSimple.json")
				.createConfiguration();

		assertThat(oAuth2ServiceConfiguration.getProperty(VERIFICATION_KEY)).isNull();
		assertThat(oAuth2ServiceConfiguration.getClientSecret()).isNull();
		assertThat(oAuth2ServiceConfiguration.getUrl()).isEqualTo(URI.create("http://localhost"));
		assertThat(oAuth2ServiceConfiguration.getProperty(UAA_DOMAIN)).isEqualTo("localhost");
	}

	@Test
	void fromFile_resourceDoesNotExits_throwsException() {
		assertThatThrownBy(() -> VcapServicesParser.fromFile("/doesNotExist.json"))
				.isInstanceOf(JsonParsingException.class)
				.hasMessageContaining("Resource not found: /doesNotExist.json");
	}

	@Test
	void fromFile_rejectsConfigurationWithClientCredentials() {
		assertThatThrownBy(() -> VcapServicesParser.fromFile("/vcapServices/vcapWithClientSecret.json"))
				.isInstanceOf(JsonParsingException.class)
				.hasMessageContaining("Client secret must not be provided!");
	}

	@Test
	void fromFile_rejectsVcapWithoutBinding() {
		assertThatThrownBy(() -> VcapServicesParser.fromFile("/vcapServices/vcapWithoutBinding.json"))
				.isInstanceOf(JsonParsingException.class)
				.hasMessageContaining("No supported binding found in VCAP_SERVICES!");
	}
}