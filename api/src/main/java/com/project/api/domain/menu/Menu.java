package com.project.api.domain.menu;

import com.project.api.domain.store.Store;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 메뉴 도메인 Entity
 * 
 * 매장의 메뉴 정보와 관련 비즈니스 로직을 포함하는 Aggregate Root
 */
@Entity
@Table(name = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long menuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private MenuCategory category;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "is_recommended", nullable = false)
    private boolean isRecommended;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SetMenu> setMenus = new ArrayList<>();

    @Builder
    private Menu(Store store, MenuCategory category, String name, String description, 
                 BigDecimal price, Integer stock, boolean isRecommended, Long menuId) {
        // ID만으로 생성하는 경우 (OrderItem에서 참조용)
        if (menuId != null && name == null) {
            this.menuId = menuId;
            return;
        }
        
        // 일반적인 생성
        validateStore(store);
        validateCategory(category);
        validateName(name);
        validatePrice(price);
        validateStock(stock);
        
        this.store = store;
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock != null ? stock : 0;
        this.isRecommended = isRecommended;
    }

    /**
     * ID 반환 (Service에서 사용)
     */
    public Long getId() {
        return this.menuId;
    }

    // == 비즈니스 로직 == //
    
    /**
     * 메뉴 정보 수정
     */
    public void updateInfo(String name, String description, BigDecimal price) {
        validateName(name);
        validatePrice(price);
        
        this.name = name;
        this.description = description;
        this.price = price;
    }

    /**
     * 재고 수정
     */
    public void updateStock(Integer newStock) {
        validateStock(newStock);
        this.stock = newStock;
    }

    /**
     * 재고 감소
     */
    public void decreaseStock(Integer quantity) {
        validateQuantity(quantity);
        
        if (this.stock < quantity) {
            throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + this.stock + ", 요청 수량: " + quantity);
        }
        
        this.stock -= quantity;
    }

    /**
     * 재고 증가
     */
    public void increaseStock(Integer quantity) {
        validateQuantity(quantity);
        this.stock += quantity;
    }

    /**
     * 추천 메뉴 설정
     */
    public void setRecommended(boolean recommended) {
        this.isRecommended = recommended;
    }

    /**
     * 카테고리 변경
     */
    public void changeCategory(MenuCategory newCategory) {
        validateCategory(newCategory);
        validateCategoryBelongsToStore(newCategory);
        this.category = newCategory;
    }

    /**
     * 메뉴 옵션 추가
     */
    public void addOption(String optionName, BigDecimal additionalPrice) {
        validateOptionName(optionName);
        validateAdditionalPrice(additionalPrice);
        
        // 중복 체크
        boolean exists = options.stream()
                .anyMatch(option -> option.getName().equals(optionName));
        
        if (exists) {
            throw new IllegalArgumentException("이미 존재하는 옵션입니다: " + optionName);
        }
        
        MenuOption option = MenuOption.builder()
                .menu(this)
                .name(optionName)
                .additionalPrice(additionalPrice)
                .build();
                
        this.options.add(option);
    }

    /**
     * 메뉴 옵션 제거
     */
    public void removeOption(Long optionId) {
        options.removeIf(option -> option.getOptionId().equals(optionId));
    }

    /**
     * 세트 메뉴 추가
     */
    public void addSetMenu(Menu includedMenu) {
        validateIncludedMenu(includedMenu);
        
        SetMenu setMenu = SetMenu.builder()
                .menu(this)
                .includedMenu(includedMenu)
                .build();
                
        this.setMenus.add(setMenu);
    }

    /**
     * 주문 가능 여부 확인
     */
    public boolean isAvailable() {
        return stock > 0;
    }

    /**
     * 특정 수량 주문 가능 여부 확인
     */
    public boolean isAvailable(Integer quantity) {
        validateQuantity(quantity);
        return stock >= quantity;
    }

    /**
     * 품절 여부 확인
     */
    public boolean isSoldOut() {
        return stock <= 0;
    }

    /**
     * 옵션이 있는지 확인
     */
    public boolean hasOptions() {
        return !options.isEmpty();
    }

    /**
     * 세트 메뉴인지 확인
     */
    public boolean isSetMenu() {
        return !setMenus.isEmpty();
    }

    /**
     * 특정 가격 이상인지 확인
     */
    public boolean isPriceAbove(BigDecimal targetPrice) {
        return price.compareTo(targetPrice) > 0;
    }

    /**
     * 특정 가격 이하인지 확인
     */
    public boolean isPriceBelow(BigDecimal targetPrice) {
        return price.compareTo(targetPrice) < 0;
    }

    /**
     * 옵션을 포함한 최종 가격 계산
     */
    public BigDecimal calculateTotalPrice(Long optionId) {
        if (optionId == null) {
            return price;
        }
        
        MenuOption option = options.stream()
                .filter(opt -> opt.getOptionId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다: " + optionId));
                
        return price.add(option.getAdditionalPrice());
    }

    // == 유효성 검증 == //
    
    private void validateStore(Store store) {
        if (store == null) {
            throw new IllegalArgumentException("매장 정보는 필수입니다.");
        }
    }

    private void validateCategory(MenuCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("메뉴 카테고리는 필수입니다.");
        }
    }

    private void validateCategoryBelongsToStore(MenuCategory category) {
        if (!category.getStore().getStoreId().equals(this.store.getStoreId())) {
            throw new IllegalArgumentException("해당 카테고리는 이 매장에 속하지 않습니다.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴 이름은 필수입니다.");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("메뉴 이름은 100자를 초과할 수 없습니다.");
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("메뉴 가격은 필수입니다.");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("메뉴 가격은 0보다 커야 합니다.");
        }
        if (price.compareTo(BigDecimal.valueOf(1000000)) > 0) {
            throw new IllegalArgumentException("메뉴 가격은 1,000,000원을 초과할 수 없습니다.");
        }
    }

    private void validateStock(Integer stock) {
        if (stock == null) {
            throw new IllegalArgumentException("재고는 필수입니다.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
    }

    private void validateOptionName(String optionName) {
        if (optionName == null || optionName.trim().isEmpty()) {
            throw new IllegalArgumentException("옵션 이름은 필수입니다.");
        }
        if (optionName.length() > 100) {
            throw new IllegalArgumentException("옵션 이름은 100자를 초과할 수 없습니다.");
        }
    }

    private void validateAdditionalPrice(BigDecimal additionalPrice) {
        if (additionalPrice == null) {
            throw new IllegalArgumentException("추가 가격은 필수입니다.");
        }
        if (additionalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("추가 가격은 0 이상이어야 합니다.");
        }
    }

    private void validateIncludedMenu(Menu includedMenu) {
        if (includedMenu == null) {
            throw new IllegalArgumentException("포함할 메뉴는 필수입니다.");
        }
        if (includedMenu.getMenuId().equals(this.menuId)) {
            throw new IllegalArgumentException("자기 자신을 세트에 포함할 수 없습니다.");
        }
        if (!includedMenu.getStore().getStoreId().equals(this.store.getStoreId())) {
            throw new IllegalArgumentException("같은 매장의 메뉴만 세트에 포함할 수 있습니다.");
        }
    }
} 