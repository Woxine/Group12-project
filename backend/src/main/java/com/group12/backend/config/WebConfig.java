package com.group12.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.group12.backend.interceptor.AuthenticationInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/v1/**") // 拦截所有 API
                .excludePathPatterns(
                    // 放行白名单
                    "/api/v1/auth/login",
                    "/api/v1/users",        // 注册接口
                    "/api/v1/scooters",     // 获取滑板车列表（假设公开）
                    "/api/v1/scooters/**/location", // 获取特定车辆位置（假设公开）
                    "/error", // Spring Boot 默认错误路径
                    
                    // Swagger UI 放行路径
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**"
                );
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 对所有 API 路径应用跨域设置
        registry.addMapping("/**")
                // -------------------------------------------------------------------
                // [前端配置说明]
                // 这里配置允许访问后端的源地址 (Origin)。
                // 1. 本地网页调试 (Vite 默认端口): "http://localhost:5173"
                // 2. HBuilderX 内置浏览器: 通常也是 localhost 的某个端口
                // 3. 真机调试/App环境: App 发出的请求 Origin 可能为空或特定标识，
                //    如果是开发阶段图方便，可以使用 allowedOriginPatterns("*") 允许所有。
                // -------------------------------------------------------------------
                .allowedOrigins("http://localhost:5173", "http://localhost:8080")
                // 或者使用下面的通配符配置（开发阶段推荐，但生产环境不安全）：
                // .allowedOriginPatterns("*")

                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
