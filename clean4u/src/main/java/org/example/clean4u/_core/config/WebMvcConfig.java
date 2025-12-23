package org.example.clean4u._core.config;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.interceptor.AccessInterceptor;
import org.example.clean4u._core.interceptor.AuthInterceptor;
import org.example.clean4u._core.interceptor.SessionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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
                        "/customer/**",
                        "/order/**",
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
}
