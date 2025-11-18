package com.example.ec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Product Serviceアプリケーション
 *
 * <p>EC Site Product Serviceのメインアプリケーションクラス。 在庫管理機能を含む商品サービスを提供する。
 */
@SpringBootApplication
public class ProductServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProductServiceApplication.class, args);
  }
}
