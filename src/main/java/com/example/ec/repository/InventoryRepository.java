package com.example.ec.repository;

import com.example.ec.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 在庫リポジトリ
 *
 * <p>在庫情報のデータアクセスを提供するリポジトリインターフェース。
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

  /**
   * キーワードとカテゴリで在庫を検索する
   *
   * <p>商品名または説明にキーワードが含まれ、指定されたカテゴリに属する在庫を検索する。 キーワードまたはカテゴリIDがnullの場合は、その条件を無視する。
   *
   * @param keyword 検索キーワード（商品名または説明に含まれる文字列）
   * @param categoryId カテゴリID
   * @param pageable ページネーション情報
   * @return 検索結果のページ
   */
  @Query(
      """
        SELECT i FROM Inventory i
        JOIN FETCH i.product p
        WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:categoryId IS NULL OR p.categoryId = :categoryId)
        AND p.isActive = true
        """)
  Page<Inventory> searchInventory(
      @Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);
}
