package com.example.ec.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 在庫検索リクエストDTO
 *
 * <p>在庫検索APIのリクエストパラメータを表すDTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySearchRequest {

  @Size(max = 100, message = "キーワードは100文字以内で指定してください")
  private String keyword;

  @Min(value = 1, message = "カテゴリIDは1以上の値を指定してください")
  private Long categoryId;

  @Min(value = 0, message = "ページ番号は0以上の値を指定してください")
  @Builder.Default
  private Integer page = 0;

  @Min(value = 1, message = "ページサイズは1以上の値を指定してください")
  @Max(value = 100, message = "ページサイズは100以下の値を指定してください")
  @Builder.Default
  private Integer size = 20;

  @Pattern(
      regexp = "^(name|price|stockQuantity|lastUpdated)$",
      message = "ソート項目はname、price、stockQuantity、lastUpdatedのいずれかを指定してください")
  @Builder.Default
  private String sort = "name";

  @Pattern(regexp = "^(asc|desc)$", message = "ソート順序はascまたはdescを指定してください")
  @Builder.Default
  private String order = "asc";
}
