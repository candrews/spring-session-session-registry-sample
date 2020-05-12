package com.example.demo;

import java.util.Optional;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SingleConcurrentSessionConfigurerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ServerProperties serverProperties;

	@Test
	public void testSameUserSecondSessionInvalidatesFirstSession() throws Exception {
		final Cookie sessionCookie1 = mockMvc.perform(
				get("/")
				.with(user("user")))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getCookie(getSessionCookeName());

		final Cookie sessionCookie2 = mockMvc.perform(
				get("/")
				.with(user("user")))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getCookie(getSessionCookeName());

		// sanity check that the sessions are different
		assertThat(sessionCookie1).isNotSameAs(sessionCookie2);

		// session1 should have been invalidated/expired
		// when that happens, a 302 redirect is returned
		mockMvc.perform(
				get("/")
				.cookie(sessionCookie1))
				.andExpect(status().isFound());

		// session2 should not have been expired
		mockMvc.perform(
				get("/")
				.cookie(sessionCookie2))
				.andExpect(status().isOk());
	}

	private String getSessionCookeName() {
		return Optional
			.ofNullable(serverProperties.getServlet().getSession().getCookie().getName())
			.orElse("SESSION");
	}
}
