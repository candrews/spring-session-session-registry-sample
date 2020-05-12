package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;


@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration<S extends Session> extends WebSecurityConfigurerAdapter {

//	@Autowired
//	private FindByIndexNameSessionRepository<S> sessionRepository;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.anyRequest()
			.authenticated()
			.and()
			.formLogin()
			.and()
			.sessionManagement()
			.maximumSessions(1).expiredUrl("/")
			
			// according to https://docs.spring.io/spring-session/docs/current/reference/html5/#spring-security-concurrent-sessions
			// this line should be necessary - but it apparently is not.
			//.sessionRegistry(sessionRegistry())
			
			;
	}

	@Autowired
	public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("user")
			.password("{noop}password")
			.roles("USER");
	}

//	@Bean
//	public SpringSessionBackedSessionRegistry<S> sessionRegistry() {
//		return new SpringSessionBackedSessionRegistry<>(sessionRepository);
//	}
}
