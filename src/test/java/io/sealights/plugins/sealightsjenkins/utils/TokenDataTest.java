package io.sealights.plugins.sealightsjenkins.utils;


import io.sealights.plugins.sealightsjenkins.entities.TokenData;
import org.junit.Assert;
import org.junit.Test;

public class TokenDataTest {

	@Test
	public void parse_validToken_shouldParseTokenData() {
		//Arrange, Act
		TokenData tokenData = TokenData.parse("eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL0RFVi1OYWRhdi5hdXRoLnNlYWxpZ2h0cy5pby8iLCJqd3RpZCI6IkRFVi1OYWRhdixpLTI0OWY0ZWU0LEFQSUdXLWNhOWYyOWI4LWZkNDctNDAzMi04M2UzLTUxYzQ4YjEzNTgyOSwxNDc5OTE5MjMxMjMyIiwic3ViamVjdCI6IlNlYUxpZ2h0c0BhZ2VudCIsImF1ZGllbmNlIjpbImFnZW50cyJdLCJ4LXNsLXJvbGUiOiJhZ2VudCIsIngtc2wtc2VydmVyIjoiaHR0cHM6Ly9ERVYtTmFkYXYtZ3cuc2VhbGlnaHRzLmNvL2FwaSIsInNsX2ltcGVyX3N1YmplY3QiOiIiLCJpYXQiOjE0Nzk5MTkyMzF9.B2P3hLb7m0N0iB73j2SRWq5neGztEQWdV5InwaFJMRt5UF5sGHfc4ICrWz_2lb9sOYlNsU5LMygl1ubs51prfKS5nwCJ94iHahy4rYFxFkjXCYz1GOXawqwT-_pCS-_pwI6f9LFpVIHipxu3JOyY91dv_UDGU_eVIrCAaPBuJsZyrzNvvq2GoDW1-zh_KnV8MdhBPMStpgqvlAOiqy_UXlxdUVQI-iRJasm3tFkADzr3XUfE_GWlTRo8vAaZ8QWI_WNc6LLU0XLkvl9YCE7-hXGSLKLmugWi8VIHIn5YJPFKoXzOz2_Q54uAg5MOaZWjsDZl3Ab66pkggZkrRikISw7hWksCTOMY7VxB96kdsrJZiHlC4ifI68SfN0RkgokVnEw8FYzVnnwzBI9-TypFJE-rvmn3HFvaUp77CRohrPrNy7HmRg45dqxvGgwuaDce5aCMQa_KiVSIeBxA95siuENjltcd3LYbK_UWTvLRK7yJF-RC-ub1V2EFzRnXbn6MUTR8uTUZKuk2FebDE88_FJ-GQIafLqYgBi6fRenvobeNacTVG3_H9msU_cVlGDwYBVR_Jg_E5u0uU8ppfccUj7zipUth0K9f4I_mDdYCo9FfYpFmU4jAtmQVMFVXzXSovbYQAgFq6MlI1aixynhfU61B0PzYD_Tgs47nnJdWL6s");

		//Assert
		Assert.assertEquals(tokenData.getCustomerId(), "SeaLights");
		Assert.assertEquals(tokenData.getRole(), "agent");
		Assert.assertEquals(tokenData.getServer(), "https://DEV-Nadav-gw.sealights.co/api");
		Assert.assertEquals(tokenData.getSubject(), "SeaLights@agent");
	}

	@Test(expected = IllegalArgumentException.class )
	public void parse_tokenWithInvalidBase64_shouldThrowException() {
		//Arrange, Act, Assert
		TokenData tokenData = TokenData.parse("QQQ@@@.W#$%^&.ZZ@#$%^&");
	}

	@Test
	public void parse_nullToken_shouldReturnEmptyTokenData() {
		//Arrange, Act
		TokenData tokenData = TokenData.parse(null);

		//Assert
		Assert.assertEquals(tokenData.getCustomerId(), null);
		Assert.assertEquals(tokenData.getRole(), null);
		Assert.assertEquals(tokenData.getServer(), null);
		Assert.assertEquals(tokenData.getSubject(), null);
	}

	@Test(expected = IllegalArgumentException.class )
	public void parse_tokenWithInvalidNumberOfParts_shouldReturnEmptyTokenData() {
		//Arrange, Act, Assert
		TokenData tokenData = TokenData.parse("e.y.J.h.XpK.S2I");
	}
}
