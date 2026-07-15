package com.rabitah.backend.security;
import com.nimbusds.jose.jwk.source.ImmutableSecret; import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value; import org.springframework.context.annotation.*; import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.config.http.SessionCreationPolicy; import org.springframework.security.crypto.bcrypt.*; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.security.oauth2.jwt.*; import org.springframework.security.web.SecurityFilterChain; import javax.crypto.spec.SecretKeySpec; import java.nio.charset.StandardCharsets;
@Configuration @EnableMethodSecurity public class SecurityConfig {
 private byte[] key(String secret){if(secret==null||secret.length()<32) throw new IllegalStateException("RABITAH_JWT_SECRET must contain at least 32 characters");return secret.getBytes(StandardCharsets.UTF_8);}
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean JwtEncoder jwtEncoder(@Value("${rabitah.security.jwt-secret}") String secret){return new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(new SecretKeySpec(key(secret),"HmacSHA256")));}
 @Bean JwtDecoder jwtDecoder(@Value("${rabitah.security.jwt-secret}") String secret){return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(key(secret),"HmacSHA256")).build();}
 @Bean SecurityFilterChain chain(HttpSecurity http)throws Exception{return http.csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a->a.requestMatchers("/api/v1/auth/**","/actuator/health").permitAll().anyRequest().authenticated()).oauth2ResourceServer(o->o.jwt(j->{})).build();}
}
