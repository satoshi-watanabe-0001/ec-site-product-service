package com.example.ec.controller;

import com.example.ec.dto.InventorySearchRequest;
import com.example.ec.dto.InventorySearchResponse;
import com.example.ec.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 在庫コントローラー
 *
 * <p>在庫検索APIのエンドポイントを提供するコントローラークラス。 組織のコーディング規約に従い、薄いController設計を採用。
 */
@RestController
@RequestMapping("/api/v1/inventory")
@Validated
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

  private final InventoryService inventoryService;

  /**
   * 在庫検索API
   *
   * <p>キーワード、カテゴリ、ページネーション、ソート条件に基づいて在庫を検索する。
   *
   * @param request 検索リクエストパラメータ
   * @return 検索結果レスポンス
   */
  @GetMapping("/search")
  public ResponseEntity<InventorySearchResponse> searchInventory(
      @ModelAttribute @Valid InventorySearchRequest request) {

    log.info(
        "Inventory search request received: keyword={}, categoryId={}, page={}, size={}",
        request.getKeyword(),
        request.getCategoryId(),
        request.getPage(),
        request.getSize());

    InventorySearchResponse response = inventoryService.searchInventory(request);

    return ResponseEntity.ok(response);
  }
}
