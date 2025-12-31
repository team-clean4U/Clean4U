package org.example.clean4u._core.config;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.interceptor.AccessInterceptor;
import org.example.clean4u._core.interceptor.AuthInterceptor;
import org.example.clean4u._core.interceptor.SessionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AccessInterceptor accessInterceptor;
    private final SessionInterceptor sessionInterceptor;
    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**");

        registry.addInterceptor(accessInterceptor)
                .addPathPatterns(
                        "/employee/**",
                        "/me/**",
                        "/laundry-item/**",
                        "/laundry-option/**",
                        "/supply-item/**",
                        "/supply-item-history/**",
                        "/customer/**",
                        "/order/**",
                        "/order-status-history/**",
                        "/notice/**"
                )
                .excludePathPatterns(
                        "/login", "/login/**",
                        "/join", "/join/**",
                        "/logout",
                        "/notice/list/**",
                        "/notice/{id:\\d+}",
                        "/error",
                        "/**/*.css",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico",
                        "/h2-console/**"
                );

        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/employee/**", "/schedule/**", "/override/**");

    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///C:/uploads");
    }
}
