package com.example.ec.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ec.dto.InventorySearchRequest;
import com.example.ec.dto.InventorySearchResponse;
import com.example.ec.entity.Inventory;
import com.example.ec.entity.Product;
import com.example.ec.repository.InventoryRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService単体テスト")
class InventoryServiceTest {

  @Mock private InventoryRepository inventoryRepository;

  @InjectMocks private InventoryService inventoryService;

  private Product testProduct;
  private Inventory testInventory;

  @BeforeEach
  void setUp() {
    testProduct =
        Product.builder()
            .id(1L)
            .name("テスト商品")
            .description("テスト商品の説明")
            .price(new BigDecimal("1000.00"))
            .categoryId(10L)
            .categoryName("テストカテゴリ")
            .isActive(true)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    testInventory =
        Inventory.builder()
            .id(1L)
            .product(testProduct)
            .stockQuantity(100)
            .reservedQuantity(10)
            .lastUpdated(Instant.now())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
  }

  @Test
  @DisplayName("正常系: キーワードなし、カテゴリなしで検索")
  void searchInventoryWithoutFiltersReturnsResults() {
    InventorySearchRequest request =
        InventorySearchRequest.builder()
            .keyword(null)
            .categoryId(null)
            .page(0)
            .size(20)
            .sort("name")
            .order("asc")
            .build();

    List<Inventory> inventories = Arrays.asList(testInventory);
    Pageable pageable = PageRequest.of(0, 20, Sort.by("product.name").ascending());
    Page<Inventory> page = new PageImpl<>(inventories, pageable, 1);

    when(inventoryRepository.searchInventory(eq(null), eq(null), any(Pageable.class)))
        .thenReturn(page);

    InventorySearchResponse response = inventoryService.searchInventory(request);

    assertThat(response).isNotNull();
    assertThat(response.getSuccess()).isTrue();
    assertThat(response.getMessage()).isEqualTo("Inventory data retrieved successfully");
    assertThat(response.getData()).isNotNull();
    assertThat(response.getData().getItems()).hasSize(1);
    assertThat(response.getData().getMeta().getPagination().getPage()).isEqualTo(1);
    assertThat(response.getData().getMeta().getPagination().getPerPage()).isEqualTo(20);
    assertThat(response.getData().getMeta().getPagination().getTotal()).isEqualTo(1L);
    assertThat(response.getData().getMeta().getPagination().getPages()).isEqualTo(1);

    verify(inventoryRepository).searchInventory(eq(null), eq(null), any(Pageable.class));
  }

  @Test
  @DisplayName("正常系: キーワード指定で検索")
  void searchInventoryWithKeywordReturnsFilteredResults() {
    InventorySearchRequest request =
        InventorySearchRequest.builder()
            .keyword("テスト")
            .categoryId(null)
            .page(0)
            .size(10)
            .sort("name")
            .order("asc")
            .build();

    List<Inventory> inventories = Arrays.asList(testInventory);
    Page<Inventory> page = new PageImpl<>(inventories);

    when(inventoryRepository.searchInventory(eq("テスト"), eq(null), any(Pageable.class)))
        .thenReturn(page);

    InventorySearchResponse response = inventoryService.searchInventory(request);

    assertThat(response).isNotNull();
    assertThat(response.getSuccess()).isTrue();
    assertThat(response.getData().getItems()).hasSize(1);
    assertThat(response.getData().getItems().get(0).getProductName()).isEqualTo("テスト商品");

    verify(inventoryRepository).searchInventory(eq("テスト"), eq(null), any(Pageable.class));
  }

  @Test
  @DisplayName("正常系: カテゴリID指定で検索")
  void searchInventoryWithCategoryIdReturnsFilteredResults() {
    InventorySearchRequest request =
        InventorySearchRequest.builder()
            .keyword(null)
            .categoryId(10L)
            .page(0)
            .size(20)
            .sort("price")
            .order("desc")
            .build();

    List<Inventory> inventories = Arrays.asList(testInventory);
    Page<Inventory> page = new PageImpl<>(inventories);

    when(inventoryRepository.searchInventory(eq(null), eq(10L), any(Pageable.class)))
        .thenReturn(page);

    InventorySearchResponse response = inventoryService.searchInventory(request);

    assertThat(response).isNotNull();
    assertThat(response.getSuccess()).isTrue();
    assertThat(response.getData().getItems()).hasSize(1);
    assertThat(response.getData().getItems().get(0).getCategoryId()).isEqualTo(10L);

    verify(inventoryRepository).searchInventory(eq(null), eq(10L), any(Pageable.class));
  }

  @Test
  @DisplayName("正常系: キーワードとカテゴリID両方指定で検索")
  void searchInventoryWithKeywordAndCategoryIdReturnsFilteredResults() {
    InventorySearchRequest request =
        InventorySearchRequest.builder()
            .keyword("テスト")
            .categoryId(10L)
            .page(0)
            .size(20)
            .sort("name")
            .order("asc")
            .build();

    List<Inventory> inventories = Arrays.asList(testInventory);
    Page<Inventory> page = new PageImpl<>(inventories);

    when(inventoryRepository.searchInventory(eq("テスト"), eq(10L), any(Pageable.class)))
        .thenReturn(page);

    InventorySearchResponse response = inventoryService.searchInventory(request);

    assertThat(response).isNotNull();
    assertThat(response.getSuccess()).isTrue();
    assertThat(response.getData().getItems()).hasSize(1);

    verify(inventoryRepository).searchInventory(eq("テスト"), eq(10L), any(Pageable.class));
  }

  @Test
  @DisplayName("正常系: 検索結果0件")
  void searchInventoryNoResultsReturnsEmptyList() {
    InventorySearchRequest request =
        InventorySearchRequest.builder()
            .keyword("存在しない商品")
            .categoryId(null)
            .page(0)
            .size(20)
            .sort("name")
            .order("asc")
            .build();

    Pageable pageable = PageRequest.of(0, 20, Sort.by("product.name").ascending());
    Page<Inventory> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

    when(inventoryRepository.searchInventory(eq("存在しない商品"), eq(null), any(Pageable.class)))
        .thenReturn(emptyPage);

    InventorySearchResponse response = inventoryService.searchInventory(request);

    assertThat(response).isNotNull();
    assertThat(response.getSuccess()).isTrue();
    assertThat(response.getData().getItems()).isEmpty();
    assertThat(response.getData().getMeta().getPagination().getTotal()).isEqualTo(0L);
    assertThat(response.getData().getMeta().getPagination().getPages()).isEqualTo(0);

    verify(inventoryRepository).searchInventory(eq("存在しない商品"), eq(null), any(Pageable.class));
  }

  @Test
  @DisplayName("正常系: ページ2を取得")
  void searchInventoryPage2ReturnsCorrectPageNumber() {
    InventorySearchRequest request =
        InventorySearchRequest.builder()
            .keyword(null)
            .categoryId(null)
            .page(1)
            .size(20)
            .sort("name")
            .order("asc")
            .build();

    List<Inventory> inventories = Arrays.asList(testInventory);
    Pageable pageable = PageRequest.of(1, 20, Sort.by("product.name").ascending());
    Page<Inventory> page = new PageImpl<>(inventories, pageable, 50);

    when(inventoryRepository.searchInventory(eq(null), eq(null), any(Pageable.class)))
        .thenReturn(page);

    InventorySearchResponse response = inventoryService.searchInventory(request);

    assertThat(response).isNotNull();
    assertThat(response.getData().getMeta().getPagination().getPage()).isEqualTo(2);
    assertThat(response.getData().getMeta().getPagination().getTotal()).isEqualTo(50L);
    assertThat(response.getData().getMeta().getPagination().getPages()).isEqualTo(3);
  }

  @Test
  @DisplayName("正常系: 降順ソート")
  void searchInventoryDescendingOrderReturnsSortedResults() {
    InventorySearchRequest request =
        InventorySearchRequest.builder()
            .keyword(null)
            .categoryId(null)
            .page(0)
            .size(20)
            .sort("price")
            .order("desc")
            .build();

    List<Inventory> inventories = Arrays.asList(testInventory);
    Page<Inventory> page = new PageImpl<>(inventories);

    when(inventoryRepository.searchInventory(eq(null), eq(null), any(Pageable.class)))
        .thenReturn(page);

    InventorySearchResponse response = inventoryService.searchInventory(request);

    assertThat(response).isNotNull();
    assertThat(response.getSuccess()).isTrue();

    verify(inventoryRepository).searchInventory(eq(null), eq(null), any(Pageable.class));
  }

  @Test
  @DisplayName("正常系: 在庫数でソート")
  void searchInventorySortByStockQuantityReturnsSortedResults() {
    InventorySearchRequest request =
        InventorySearchRequest.builder()
            .keyword(null)
            .categoryId(null)
            .page(0)
            .size(20)
            .sort("stockQuantity")
            .order("asc")
            .build();

    List<Inventory> inventories = Arrays.asList(testInventory);
    Page<Inventory> page = new PageImpl<>(inventories);

    when(inventoryRepository.searchInventory(eq(null), eq(null), any(Pageable.class)))
        .thenReturn(page);

    InventorySearchResponse response = inventoryService.searchInventory(request);

    assertThat(response).isNotNull();
    assertThat(response.getSuccess()).isTrue();
    assertThat(response.getData().getItems().get(0).getStockQuantity()).isEqualTo(100);
    assertThat(response.getData().getItems().get(0).getAvailableQuantity()).isEqualTo(90);
    assertThat(response.getData().getItems().get(0).getIsInStock()).isTrue();
  }

  @Test
  @DisplayName("正常系: 最終更新日時でソート")
  void searchInventorySortByLastUpdatedReturnsSortedResults() {
    InventorySearchRequest request =
        InventorySearchRequest.builder()
            .keyword(null)
            .categoryId(null)
            .page(0)
            .size(20)
            .sort("lastUpdated")
            .order("desc")
            .build();

    List<Inventory> inventories = Arrays.asList(testInventory);
    Page<Inventory> page = new PageImpl<>(inventories);

    when(inventoryRepository.searchInventory(eq(null), eq(null), any(Pageable.class)))
        .thenReturn(page);

    InventorySearchResponse response = inventoryService.searchInventory(request);

    assertThat(response).isNotNull();
    assertThat(response.getSuccess()).isTrue();
    assertThat(response.getData().getItems().get(0).getLastUpdated()).isNotNull();
  }

  @Test
  @DisplayName("正常系: DTOフィールドマッピング確認")
  void searchInventoryVerifyDtoMapping() {
    InventorySearchRequest request =
        InventorySearchRequest.builder()
            .keyword(null)
            .categoryId(null)
            .page(0)
            .size(20)
            .sort("name")
            .order("asc")
            .build();

    List<Inventory> inventories = Arrays.asList(testInventory);
    Page<Inventory> page = new PageImpl<>(inventories);

    when(inventoryRepository.searchInventory(eq(null), eq(null), any(Pageable.class)))
        .thenReturn(page);

    InventorySearchResponse response = inventoryService.searchInventory(request);

    assertThat(response).isNotNull();
    assertThat(response.getTimestamp()).isNotNull();
    assertThat(response.getRequestId()).isNotNull();

    InventorySearchResponse.InventoryItem item = response.getData().getItems().get(0);
    assertThat(item.getInventoryId()).isEqualTo(1L);
    assertThat(item.getProductId()).isEqualTo(1L);
    assertThat(item.getProductName()).isEqualTo("テスト商品");
    assertThat(item.getProductDescription()).isEqualTo("テスト商品の説明");
    assertThat(item.getPrice()).isEqualByComparingTo(new BigDecimal("1000.00"));
    assertThat(item.getCategoryId()).isEqualTo(10L);
    assertThat(item.getCategoryName()).isEqualTo("テストカテゴリ");
    assertThat(item.getStockQuantity()).isEqualTo(100);
    assertThat(item.getReservedQuantity()).isEqualTo(10);
    assertThat(item.getAvailableQuantity()).isEqualTo(90);
    assertThat(item.getIsInStock()).isTrue();
    assertThat(item.getLastUpdated()).isNotNull();
  }
}
