package com.thiCK.Nhom16.config;

import com.thiCK.Nhom16.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Chỉ dùng cho dev/demo
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserService userService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authProvider
    ) throws Exception {
        http
            .authenticationProvider(authProvider)
            .authorizeHttpRequests(auth -> auth
                // Các URL public (kể cả profile để show modal/login)
                .requestMatchers(
                    "/", 
                    "/user/profile",
                    "/login", 
                    "/register",
                    "/css/**", "/js/**", "/images/**", "/webjars/**",
                    "/error", "/fragments/**"
                ).permitAll()
                // Chỉnh sửa profile, đổi mật khẩu (cần phải auth)
                .requestMatchers(
                    "/user/edit", "/user/change_password"
                ).authenticated()
                // Danh sách & xóa user chỉ ADMIN
                .requestMatchers("/user/list", "/user/delete/**")
                    .hasRole("ADMIN")
                // Dashboard & Quản trị chung ADMIN & QUANTRIVIEN
                .requestMatchers("/dashboard/**", "/quantri/**")
                    .hasAnyRole("ADMIN", "QUANTRIVIEN")
                // Admin page chỉ ADMIN
                .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                // Các request khác bắt buộc login
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customAuthenticationSuccessHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    /**
     * Sau login:
     * - ADMIN & QUANTRIVIEN -> /dashboard
     * - USER -> /user/profile
     */
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    Authentication authentication
            ) throws IOException, ServletException {
                Collection<? extends GrantedAuthority> authorities =
                    authentication.getAuthorities();

                String redirectUrl = "/user/profile"; // default USER
                for (GrantedAuthority auth : authorities) {
                    String role = auth.getAuthority();
                    if ("ROLE_ADMIN".equals(role) || "ROLE_QUANTRIVIEN".equals(role)) {
                        redirectUrl = "/dashboard";
                        break;
                    }
                }

                response.sendRedirect(redirectUrl);
            }
        };
    }
}
