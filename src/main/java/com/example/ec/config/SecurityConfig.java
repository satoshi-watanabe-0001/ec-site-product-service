package com.example.ec.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * セキュリティ設定
 *
 * <p>Spring Securityの設定を行うクラス。 在庫検索APIは商品検索・閲覧カテゴリのため、認証不要でパブリックアクセス可能とする。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /**
   * セキュリティフィルターチェーンの設定
   *
   * @param http HttpSecurityオブジェクト
   * @return SecurityFilterChain
   * @throws Exception 設定エラー
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/v1/inventory/**")
                    .permitAll()
                    .requestMatchers("/actuator/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated());

    return http.build();
  }
}
