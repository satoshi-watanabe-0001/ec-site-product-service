package com.example.ec.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 在庫エンティティ
 *
 * <p>商品の在庫情報を管理するエンティティクラス。 在庫数、予約済み数量、利用可能数量を管理する。
 */
@Entity
@Table(name = "inventory", schema = "product_schema")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(name = "stock_quantity", nullable = false)
  private Integer stockQuantity;

  @Column(name = "reserved_quantity", nullable = false)
  private Integer reservedQuantity;

  @Column(name = "last_updated", nullable = false)
  private Instant lastUpdated;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  /**
   * 利用可能な在庫数を計算する
   *
   * @return 利用可能な在庫数（在庫数 - 予約済み数量）
   */
  public Integer getAvailableQuantity() {
    return stockQuantity - reservedQuantity;
  }

  /**
   * 在庫が利用可能かどうかを判定する
   *
   * @return 利用可能な在庫が1以上の場合true
   */
  public Boolean isInStock() {
    return getAvailableQuantity() > 0;
  }

  @PrePersist
  protected void onCreate() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
    lastUpdated = now;
    if (reservedQuantity == null) {
      reservedQuantity = 0;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    Instant now = Instant.now();
    updatedAt = now;
    lastUpdated = now;
  }
}
