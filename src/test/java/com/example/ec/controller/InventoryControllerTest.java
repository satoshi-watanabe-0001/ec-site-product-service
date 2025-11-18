package com.example.ec.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ec.dto.InventorySearchRequest;
import com.example.ec.dto.InventorySearchResponse;
import com.example.ec.service.InventoryService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InventoryController.class)
@DisplayName("InventoryController統合テスト")
class InventoryControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private InventoryService inventoryService;

  @Test
  @WithMockUser
  @DisplayName("正常系: デフォルトパラメータで検索")
  void searchInventoryWithDefaultParamsReturnsOk() throws Exception {
    InventorySearchResponse.InventoryItem item =
        InventorySearchResponse.InventoryItem.builder()
            .inventoryId(1L)
            .productId(1L)
            .productName("テスト商品")
            .productDescription("テスト商品の説明")
            .price(new BigDecimal("1000.00"))
            .categoryId(10L)
            .categoryName("テストカテゴリ")
            .stockQuantity(100)
            .reservedQuantity(10)
            .availableQuantity(90)
            .isInStock(true)
            .lastUpdated(Instant.now())
            .build();

    InventorySearchResponse.Pagination pagination =
        InventorySearchResponse.Pagination.builder().page(1).perPage(20).total(1L).pages(1).build();

    InventorySearchResponse.Meta meta =
        InventorySearchResponse.Meta.builder().pagination(pagination).build();

    InventorySearchResponse.DataWrapper data =
        InventorySearchResponse.DataWrapper.builder().items(Arrays.asList(item)).meta(meta).build();

    InventorySearchResponse response =
        InventorySearchResponse.builder()
            .success(true)
            .message("Inventory data retrieved successfully")
            .data(data)
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    when(inventoryService.searchInventory(any(InventorySearchRequest.class))).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/inventory/search"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Inventory data retrieved successfully"))
        .andExpect(jsonPath("$.data.items").isArray())
        .andExpect(jsonPath("$.data.items[0].product_name").value("テスト商品"))
        .andExpect(jsonPath("$.data.items[0].stock_quantity").value(100))
        .andExpect(jsonPath("$.data.items[0].is_in_stock").value(true))
        .andExpect(jsonPath("$.data.meta.pagination.page").value(1))
        .andExpect(jsonPath("$.data.meta.pagination.per_page").value(20))
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.request_id").exists());
  }

  @Test
  @WithMockUser
  @DisplayName("正常系: キーワード指定で検索")
  void searchInventoryWithKeywordReturnsFilteredResults() throws Exception {
    InventorySearchResponse.InventoryItem item =
        InventorySearchResponse.InventoryItem.builder()
            .inventoryId(1L)
            .productId(1L)
            .productName("テスト商品")
            .productDescription("テスト商品の説明")
            .price(new BigDecimal("1000.00"))
            .categoryId(10L)
            .categoryName("テストカテゴリ")
            .stockQuantity(100)
            .reservedQuantity(10)
            .availableQuantity(90)
            .isInStock(true)
            .lastUpdated(Instant.now())
            .build();

    InventorySearchResponse.Pagination pagination =
        InventorySearchResponse.Pagination.builder().page(1).perPage(20).total(1L).pages(1).build();

    InventorySearchResponse.Meta meta =
        InventorySearchResponse.Meta.builder().pagination(pagination).build();

    InventorySearchResponse.DataWrapper data =
        InventorySearchResponse.DataWrapper.builder().items(Arrays.asList(item)).meta(meta).build();

    InventorySearchResponse response =
        InventorySearchResponse.builder()
            .success(true)
            .message("Inventory data retrieved successfully")
            .data(data)
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    when(inventoryService.searchInventory(any(InventorySearchRequest.class))).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/inventory/search").param("keyword", "テスト"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.items[0].product_name").value("テスト商品"));
  }

  @Test
  @WithMockUser
  @DisplayName("正常系: カテゴリID指定で検索")
  void searchInventoryWithCategoryIdReturnsFilteredResults() throws Exception {
    InventorySearchResponse.InventoryItem item =
        InventorySearchResponse.InventoryItem.builder()
            .inventoryId(1L)
            .productId(1L)
            .productName("テスト商品")
            .productDescription("テスト商品の説明")
            .price(new BigDecimal("1000.00"))
            .categoryId(10L)
            .categoryName("テストカテゴリ")
            .stockQuantity(100)
            .reservedQuantity(10)
            .availableQuantity(90)
            .isInStock(true)
            .lastUpdated(Instant.now())
            .build();

    InventorySearchResponse.Pagination pagination =
        InventorySearchResponse.Pagination.builder().page(1).perPage(20).total(1L).pages(1).build();

    InventorySearchResponse.Meta meta =
        InventorySearchResponse.Meta.builder().pagination(pagination).build();

    InventorySearchResponse.DataWrapper data =
        InventorySearchResponse.DataWrapper.builder().items(Arrays.asList(item)).meta(meta).build();

    InventorySearchResponse response =
        InventorySearchResponse.builder()
            .success(true)
            .message("Inventory data retrieved successfully")
            .data(data)
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    when(inventoryService.searchInventory(any(InventorySearchRequest.class))).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/inventory/search").param("categoryId", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.items[0].category_id").value(10));
  }

  @Test
  @WithMockUser
  @DisplayName("正常系: ページングパラメータ指定")
  void searchInventoryWithPaginationReturnsPagedResults() throws Exception {
    InventorySearchResponse.Pagination pagination =
        InventorySearchResponse.Pagination.builder()
            .page(2)
            .perPage(10)
            .total(50L)
            .pages(5)
            .build();

    InventorySearchResponse.Meta meta =
        InventorySearchResponse.Meta.builder().pagination(pagination).build();

    InventorySearchResponse.DataWrapper data =
        InventorySearchResponse.DataWrapper.builder()
            .items(Collections.emptyList())
            .meta(meta)
            .build();

    InventorySearchResponse response =
        InventorySearchResponse.builder()
            .success(true)
            .message("Inventory data retrieved successfully")
            .data(data)
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    when(inventoryService.searchInventory(any(InventorySearchRequest.class))).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/inventory/search").param("page", "1").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.meta.pagination.page").value(2))
        .andExpect(jsonPath("$.data.meta.pagination.per_page").value(10));
  }

  @Test
  @WithMockUser
  @DisplayName("正常系: ソートパラメータ指定")
  void searchInventoryWithSortReturnsSortedResults() throws Exception {
    InventorySearchResponse.Pagination pagination =
        InventorySearchResponse.Pagination.builder().page(1).perPage(20).total(0L).pages(0).build();

    InventorySearchResponse.Meta meta =
        InventorySearchResponse.Meta.builder().pagination(pagination).build();

    InventorySearchResponse.DataWrapper data =
        InventorySearchResponse.DataWrapper.builder()
            .items(Collections.emptyList())
            .meta(meta)
            .build();

    InventorySearchResponse response =
        InventorySearchResponse.builder()
            .success(true)
            .message("Inventory data retrieved successfully")
            .data(data)
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    when(inventoryService.searchInventory(any(InventorySearchRequest.class))).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/inventory/search").param("sort", "price").param("order", "desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  @Test
  @WithMockUser
  @DisplayName("異常系: 不正なpage値（負の数）")
  void searchInventoryWithNegativePageReturnsBadRequest() throws Exception {
    mockMvc
        .perform(get("/api/v1/inventory/search").param("page", "-1"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"));
  }

  @Test
  @WithMockUser
  @DisplayName("異常系: 不正なsize値（上限超過）")
  void searchInventoryWithOversizedSizeReturnsBadRequest() throws Exception {
    mockMvc
        .perform(get("/api/v1/inventory/search").param("size", "101"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"));
  }

  @Test
  @WithMockUser
  @DisplayName("異常系: 不正なsort値")
  void searchInventoryWithInvalidSortReturnsBadRequest() throws Exception {
    mockMvc
        .perform(get("/api/v1/inventory/search").param("sort", "invalidField"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"));
  }

  @Test
  @WithMockUser
  @DisplayName("異常系: 不正なorder値")
  void searchInventoryWithInvalidOrderReturnsBadRequest() throws Exception {
    mockMvc
        .perform(get("/api/v1/inventory/search").param("order", "invalid"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"));
  }

  @Test
  @WithMockUser
  @DisplayName("異常系: keyword長すぎる")
  void searchInventoryWithTooLongKeywordReturnsBadRequest() throws Exception {
    String longKeyword = "a".repeat(101);
    mockMvc
        .perform(get("/api/v1/inventory/search").param("keyword", longKeyword))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"));
  }

  @Test
  @WithMockUser
  @DisplayName("異常系: categoryIdが文字列（型変換エラー）")
  void searchInventoryWithInvalidCategoryIdTypeReturnsBadRequest() throws Exception {
    mockMvc
        .perform(get("/api/v1/inventory/search").param("categoryId", "invalid"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"));
  }

  @Test
  @WithMockUser
  @DisplayName("正常系: 検索結果0件")
  void searchInventoryNoResultsReturnsEmptyList() throws Exception {
    InventorySearchResponse.Pagination pagination =
        InventorySearchResponse.Pagination.builder().page(1).perPage(20).total(0L).pages(0).build();

    InventorySearchResponse.Meta meta =
        InventorySearchResponse.Meta.builder().pagination(pagination).build();

    InventorySearchResponse.DataWrapper data =
        InventorySearchResponse.DataWrapper.builder()
            .items(Collections.emptyList())
            .meta(meta)
            .build();

    InventorySearchResponse response =
        InventorySearchResponse.builder()
            .success(true)
            .message("Inventory data retrieved successfully")
            .data(data)
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    when(inventoryService.searchInventory(any(InventorySearchRequest.class))).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/inventory/search").param("keyword", "存在しない商品"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.items").isEmpty())
        .andExpect(jsonPath("$.data.meta.pagination.total").value(0));
  }
}
