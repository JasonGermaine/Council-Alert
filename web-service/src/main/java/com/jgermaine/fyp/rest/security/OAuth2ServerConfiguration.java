package com.jgermaine.fyp.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import com.jgermaine.fyp.rest.config.Secret;

@Configuration
public class OAuth2ServerConfiguration {
	
	private static final String RESOURCE_ID = "council-alert-oauth";
	 
	@Configuration
	@EnableResourceServer
	@Order(2)
	protected static class ResourceServerConfiguration extends
			ResourceServerConfigurerAdapter {

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			// @formatter:off
			resources
				.resourceId(RESOURCE_ID);
			// @formatter:on
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeRequests()
					 // Matchers to apply different levels of security to
					.antMatchers("/api/report/**").access("#oauth2.hasScope('write')")
					.antMatchers("/api/user/**").access("#oauth2.hasScope('write')")
					.antMatchers("/api/employee/**").access("#oauth2.hasScope('write')")
					.antMatchers("/api/citizen/report").access("#oauth2.hasScope('write')")
					.antMatchers("/api/admin/**").access("#oauth2.hasScope('trust') and hasRole('ADMIN')")
					.antMatchers("/displayReport.html", "/displayEmployee.html",
							"/addEmployee.html", "/profile.html").access("#oauth2.hasScope('trust') and hasRole('ADMIN')");
			// @formatter:on
		}

	}

	@Configuration
	@EnableAuthorizationServer
	//@Order(2)
	protected static class AuthorizationServerConfiguration extends
			AuthorizationServerConfigurerAdapter {

		private TokenStore tokenStore = new InMemoryTokenStore();

		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints)
				throws Exception {
			// @formatter:off
			endpoints
				.tokenStore(this.tokenStore)
				.authenticationManager(this.authenticationManager);
			// @formatter:on
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			// @formatter:off
			clients
				.inMemory()
				
				// Angular Client 
				.withClient("angular-client")
						.authorizedGrantTypes("password", "refresh_token")
						.authorities("ADMIN")
						.scopes("read", "write", "trust")
						.resourceIds(RESOURCE_ID)
						.secret(Secret.ANGULAR_SECRET)
				.and()
					
				// Android
				.withClient("android-client")
						 .resourceIds(RESOURCE_ID)
						 .authorizedGrantTypes("password", "refresh_token")
						 .authorities("ADMIN")
						 .scopes("read", "write")
						 .secret(Secret.ANDROID_SECRET)
				.and()
				.withClient("android-client")
						.resourceIds(RESOURCE_ID)
	 			        .authorizedGrantTypes("password", "refresh_token")
	 			        .authorities("USER")
	 			        .scopes("read", "write")
	 			        .secret(Secret.ANDROID_SECRET);
			// @formatter:on			
		}

		@Bean
		@Primary
		public DefaultTokenServices tokenServices() {
			DefaultTokenServices tokenServices = new DefaultTokenServices();
			tokenServices.setSupportRefreshToken(true);
			tokenServices.setTokenStore(this.tokenStore);
			return tokenServices;
		}
	}
}