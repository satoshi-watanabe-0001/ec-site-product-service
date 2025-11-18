package com.example.ec.service;

import com.example.ec.dto.InventorySearchRequest;
import com.example.ec.dto.InventorySearchResponse;
import com.example.ec.dto.InventorySearchResponse.DataWrapper;
import com.example.ec.dto.InventorySearchResponse.InventoryItem;
import com.example.ec.dto.InventorySearchResponse.Meta;
import com.example.ec.dto.InventorySearchResponse.Pagination;
import com.example.ec.entity.Inventory;
import com.example.ec.repository.InventoryRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 在庫サービス
 *
 * <p>在庫検索のビジネスロジックを提供するサービスクラス。
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

  private final InventoryRepository inventoryRepository;

  /**
   * 在庫を検索する
   *
   * <p>キーワード、カテゴリ、ページネーション、ソート条件に基づいて在庫を検索する。
   *
   * @param request 検索リクエスト
   * @return 検索結果レスポンス
   */
  public InventorySearchResponse searchInventory(InventorySearchRequest request) {
    log.info(
        "Inventory search started: keyword={}, categoryId={}, page={}, size={}, sort={}, order={}",
        request.getKeyword(),
        request.getCategoryId(),
        request.getPage(),
        request.getSize(),
        request.getSort(),
        request.getOrder());

    Pageable pageable = createPageable(request);

    Page<Inventory> inventoryPage =
        inventoryRepository.searchInventory(
            request.getKeyword(), request.getCategoryId(), pageable);

    List<InventoryItem> items =
        inventoryPage.getContent().stream()
            .map(this::convertToInventoryItem)
            .collect(Collectors.toList());

    Pagination pagination =
        Pagination.builder()
            .page(inventoryPage.getNumber())
            .perPage(inventoryPage.getSize())
            .total(inventoryPage.getTotalElements())
            .pages(inventoryPage.getTotalPages())
            .build();

    Meta meta = Meta.builder().pagination(pagination).build();

    DataWrapper data = DataWrapper.builder().items(items).meta(meta).build();

    log.info(
        "Inventory search completed: resultCount={}, totalElements={}, totalPages={}",
        items.size(),
        inventoryPage.getTotalElements(),
        inventoryPage.getTotalPages());

    return InventorySearchResponse.builder()
        .success(true)
        .message("Inventory data retrieved successfully")
        .data(data)
        .timestamp(Instant.now())
        .requestId(UUID.randomUUID().toString())
        .build();
  }

  /**
   * ページネーション設定を作成する
   *
   * @param request 検索リクエスト
   * @return Pageableオブジェクト
   */
  private Pageable createPageable(InventorySearchRequest request) {
    Sort.Direction direction =
        "desc".equalsIgnoreCase(request.getOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;

    String sortField = mapSortField(request.getSort());

    Sort sort = Sort.by(direction, sortField);

    return PageRequest.of(request.getPage(), request.getSize(), sort);
  }

  /**
   * ソートフィールドをエンティティのフィールド名にマッピングする
   *
   * @param sortField リクエストのソートフィールド
   * @return エンティティのフィールド名
   */
  private String mapSortField(String sortField) {
    return switch (sortField) {
      case "name" -> "product.name";
      case "price" -> "product.price";
      case "stockQuantity" -> "stockQuantity";
      case "lastUpdated" -> "lastUpdated";
      default -> "product.name";
    };
  }

  /**
   * InventoryエンティティをInventoryItemDTOに変換する
   *
   * @param inventory 在庫エンティティ
   * @return 在庫アイテムDTO
   */
  private InventoryItem convertToInventoryItem(Inventory inventory) {
    return InventoryItem.builder()
        .inventoryId(inventory.getId())
        .productId(inventory.getProduct().getId())
        .productName(inventory.getProduct().getName())
        .productDescription(inventory.getProduct().getDescription())
        .price(inventory.getProduct().getPrice())
        .categoryId(inventory.getProduct().getCategoryId())
        .categoryName(inventory.getProduct().getCategoryName())
        .stockQuantity(inventory.getStockQuantity())
        .reservedQuantity(inventory.getReservedQuantity())
        .availableQuantity(inventory.getAvailableQuantity())
        .isInStock(inventory.isInStock())
        .lastUpdated(inventory.getLastUpdated())
        .build();
  }
}
