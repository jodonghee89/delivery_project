package com.project.api.domain.store;

import com.project.api.domain.menu.Menu;
import com.project.api.domain.menu.MenuCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 매장 도메인 Entity
 * 
 * 배달 매장의 핵심 정보와 비즈니스 로직을 포함하는 Aggregate Root
 */
@Entity
@Table(name = "stores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private StoreOwner owner;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private StoreCategory category;

    @Column(name = "address", columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuCategory> menuCategories = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();

    @OneToOne(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private StoreStatistics statistics;

    @Builder
    private Store(StoreOwner owner, String name, String description, StoreCategory category, 
                  String address, String phone, Long storeId) {
        // ID만으로 생성하는 경우 (Order에서 참조용)
        if (storeId != null && name == null) {
            this.storeId = storeId;
            return;
        }
        
        // 일반적인 생성
        validateOwner(owner);
        validateName(name);
        validateCategory(category);
        validateAddress(address);
        
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.category = category;
        this.address = address;
        this.phone = phone;
        this.rating = BigDecimal.ZERO;
        
        // 통계 객체 초기화
        this.statistics = StoreStatistics.createInitial(this);
    }

    /**
     * ID 반환 (Service에서 사용)
     */
    public Long getId() {
        return this.storeId;
    }

    // == 비즈니스 로직 == //
    
    /**
     * 매장 정보 수정
     */
    public void updateInfo(String name, String description, String address, String phone) {
        validateName(name);
        validateAddress(address);
        
        this.name = name;
        this.description = description;
        this.address = address;
        this.phone = phone;
    }

    /**
     * 매장 카테고리 변경
     */
    public void changeCategory(StoreCategory newCategory) {
        validateCategory(newCategory);
        this.category = newCategory;
    }

    /**
     * 메뉴 카테고리 추가
     */
    public void addMenuCategory(String categoryName) {
        validateMenuCategoryName(categoryName);
        
        // 중복 체크
        boolean exists = menuCategories.stream()
                .anyMatch(mc -> mc.getName().equals(categoryName));
        
        if (exists) {
            throw new IllegalArgumentException("이미 존재하는 메뉴 카테고리입니다: " + categoryName);
        }
        
        MenuCategory menuCategory = MenuCategory.builder()
                .store(this)
                .name(categoryName)
                .build();
                
        this.menuCategories.add(menuCategory);
    }

    /**
     * 메뉴 추가
     */
    public void addMenu(MenuCategory menuCategory, String menuName, String description, 
                        BigDecimal price, Integer stock, boolean isRecommended) {
        validateMenuCategory(menuCategory);
        
        Menu menu = Menu.builder()
                .store(this)
                .category(menuCategory)
                .name(menuName)
                .description(description)
                .price(price)
                .stock(stock)
                .isRecommended(isRecommended)
                .build();
                
        this.menus.add(menu);
    }

    /**
     * 평점 업데이트
     */
    public void updateRating(BigDecimal newRating) {
        validateRating(newRating);
        this.rating = newRating.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 통계 업데이트
     */
    public void updateStatistics(BigDecimal totalSales, Integer totalOrders, BigDecimal averageRating) {
        if (this.statistics == null) {
            this.statistics = StoreStatistics.createInitial(this);
        }
        this.statistics.updateStatistics(totalSales, totalOrders, averageRating);
        this.rating = averageRating;
    }

    /**
     * 추천 메뉴 조회
     */
    public List<Menu> getRecommendedMenus() {
        return menus.stream()
                .filter(Menu::isRecommended)
                .toList();
    }

    /**
     * 특정 카테고리의 메뉴 조회
     */
    public List<Menu> getMenusByCategory(Long categoryId) {
        return menus.stream()
                .filter(menu -> menu.getCategory().getCategoryId().equals(categoryId))
                .toList();
    }

    /**
     * 주문 가능한 메뉴 조회
     */
    public List<Menu> getAvailableMenus() {
        return menus.stream()
                .filter(Menu::isAvailable)
                .toList();
    }

    /**
     * 매장 운영 가능 여부 확인
     */
    public boolean isOperational() {
        return !menus.isEmpty() && hasAvailableMenus();
    }

    /**
     * 주문 가능한 메뉴가 있는지 확인
     */
    public boolean hasAvailableMenus() {
        return menus.stream().anyMatch(Menu::isAvailable);
    }

    /**
     * 평점이 설정되어 있는지 확인
     */
    public boolean hasRating() {
        return rating != null && rating.compareTo(BigDecimal.ZERO) > 0;
    }

    // == 유효성 검증 == //
    
    private void validateOwner(StoreOwner owner) {
        if (owner == null) {
            throw new IllegalArgumentException("매장 사장 정보는 필수입니다.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("매장 이름은 필수입니다.");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("매장 이름은 100자를 초과할 수 없습니다.");
        }
    }

    private void validateCategory(StoreCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("매장 카테고리는 필수입니다.");
        }
    }

    private void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("매장 주소는 필수입니다.");
        }
    }

    private void validateMenuCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴 카테고리 이름은 필수입니다.");
        }
        if (categoryName.length() > 50) {
            throw new IllegalArgumentException("메뉴 카테고리 이름은 50자를 초과할 수 없습니다.");
        }
    }

    private void validateMenuCategory(MenuCategory menuCategory) {
        if (menuCategory == null) {
            throw new IllegalArgumentException("메뉴 카테고리는 필수입니다.");
        }
        
        boolean belongsToThisStore = menuCategories.stream()
                .anyMatch(mc -> mc.getCategoryId().equals(menuCategory.getCategoryId()));
                
        if (!belongsToThisStore) {
            throw new IllegalArgumentException("해당 메뉴 카테고리는 이 매장에 속하지 않습니다.");
        }
    }

    private void validateRating(BigDecimal rating) {
        if (rating == null) {
            throw new IllegalArgumentException("평점은 필수입니다.");
        }
        if (rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(BigDecimal.valueOf(5)) > 0) {
            throw new IllegalArgumentException("평점은 0.0에서 5.0 사이여야 합니다.");
        }
    }
} 