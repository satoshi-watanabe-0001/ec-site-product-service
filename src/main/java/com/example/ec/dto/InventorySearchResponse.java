package com.example.ec.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 在庫検索レスポンスDTO
 *
 * <p>在庫検索APIのレスポンスを表すDTO。 組織標準のAPIレスポンスフォーマットに準拠。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySearchResponse {

  private Boolean success;
  private String message;
  private DataWrapper data;
  private Instant timestamp;
  private String requestId;

  /**
   * データラッパークラス
   *
   * <p>在庫アイテムのリストとメタ情報を含む。
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DataWrapper {
    private List<InventoryItem> items;
    private Meta meta;
  }

  /**
   * 在庫アイテムDTO
   *
   * <p>個別の在庫情報を表すDTO。
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class InventoryItem {
    private Long inventoryId;
    private Long productId;
    private String productName;
    private String productDescription;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
    private Integer stockQuantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private Boolean isInStock;
    private Instant lastUpdated;
  }

  /**
   * メタ情報クラス
   *
   * <p>ページネーション情報を含む。
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Meta {
    private Pagination pagination;
  }

  /** ページネーション情報クラス */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Pagination {
    private Integer page;
    private Integer perPage;
    private Long total;
    private Integer pages;
  }
}
