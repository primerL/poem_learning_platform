package edu.fudan.poetryconference.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // 使用Lambda表达式禁用CSRF保护
                .authorizeRequests(authorize -> authorize
//                        .requestMatchers("/api/users/register", "/api/users/login").permitAll()  // 允许无需认证访问注册和登录接口
//                        .requestMatchers("/api/question/**").permitAll()
//                        .requestMatchers("/api/chat").permitAll()
//                        .requestMatchers("/ws").permitAll()
                        .anyRequest().permitAll()  // 暂时打开所有访问权限
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

