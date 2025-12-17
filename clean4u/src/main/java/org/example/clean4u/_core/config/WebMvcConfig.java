package org.example.clean4u._core.config;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.interceptor.AccessInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AccessInterceptor accessInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor)
                .addPathPatterns(
                        "/employee/**",
                        "/laundry-item/**",
                        "/laundry-option/**",
                        "/supply-item/**",
                        "/customer/**",
                        "/order/**"
                )
                .excludePathPatterns(
                        "/login", "/login/**",
                        "/join", "/join/**",
                        "/logout",
                        "/error",
                        "/**/*.css",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico",
                        "/h2-console/**"
                );
    }
}
