package org.example.clean4u._core.config;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.interceptor.AccessInterceptor;
import org.example.clean4u._core.interceptor.AuthInterceptor;
import org.example.clean4u._core.interceptor.SessionFixationInterceptor;
import org.example.clean4u._core.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Value;
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
    private final SessionFixationInterceptor sessionFixationInterceptor;

    @Value("${app.upload.base-path}")
    private String basePath;

    @Value("${app.upload.notice-file-path}")
    private String noticeFilePath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(sessionFixationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/sessions",
                        "/auth/**",
                        "/**/*.css",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico",
                        "/v3/api-docs/**",
                        "/swagger-ui/**"
                );

        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-resources/**"
                );

        registry.addInterceptor(accessInterceptor)
                .addPathPatterns(
                        "/dashboard",
                        "/employees/**",
                        "/laundry-items/**",
                        "/laundry-options/**",
                        "/supply-items/**",
                        "/supply-item-histories/**",
                        "/review/**",
                        "/customers/**",
                        "/orders/**",
                        "/order-status-histories/**",
                        "/notices/**",
                        "/schedules/**",
                        "/overrides/**",
                        "/refunds/**",
                        "/chats/**",
                        "/files/**",
                        "/api/v1/**"
                )
                .excludePathPatterns(
                        "/auth/**",
                        "/api/v1/email/**",
                        "/employees/new",
                        "/password/**",
                        "/login",
                        "/sessions",
                        "/error",
                        "/client",
                        "/client/**",
                        "/**/*.css",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico",
                        "/h2-console/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-resources/**"
                );

        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                        "/admin/employees/**",
                        "/schedules/**",
                        "/schedule-overrides/**",
                        "/overrides/**",
                        "/notices/new",
                        "/notices/*/edit",
                        "/api/v1/admin/option-chart",
                        "/api/v1/admin/category-revenue-chart",
                        "/api/v1/admin/monthly-trend-chart"
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + basePath + "/");

        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + noticeFilePath + "/");
    }
}
