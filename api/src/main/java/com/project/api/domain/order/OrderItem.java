package com.project.api.domain.order;

import com.project.api.domain.menu.Menu;
import com.project.api.domain.menu.MenuOption;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 주문 아이템 Entity
 * 
 * 주문에 포함된 개별 메뉴 아이템 정보
 */
@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private MenuOption menuOption;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Builder
    private OrderItem(Order order, Menu menu, MenuOption menuOption, Integer quantity, BigDecimal unitPrice) {
        validateOrder(order);
        validateMenu(menu);
        validateQuantity(quantity);
        validateUnitPrice(unitPrice);
        
        this.order = order;
        this.menu = menu;
        this.menuOption = menuOption;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // == 비즈니스 로직 == //
    
    /**
     * 수량 변경
     */
    public void changeQuantity(Integer newQuantity) {
        validateQuantity(newQuantity);
        this.quantity = newQuantity;
    }

    /**
     * 총 가격 계산 (수량 * 단가 + 옵션 가격)
     */
    public BigDecimal getTotalPrice() {
        BigDecimal basePrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        
        if (menuOption != null) {
            BigDecimal optionPrice = menuOption.getAdditionalPrice().multiply(BigDecimal.valueOf(quantity));
            return basePrice.add(optionPrice);
        }
        
        return basePrice;
    }

    /**
     * 메뉴 이름 조회 (옵션 포함)
     */
    public String getMenuNameWithOption() {
        String menuName = menu.getName();
        if (menuOption != null) {
            return menuName + " (" + menuOption.getName() + ")";
        }
        return menuName;
    }

    /**
     * 옵션이 있는지 확인
     */
    public boolean hasOption() {
        return menuOption != null;
    }

    // == 유효성 검증 == //
    
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("주문 정보는 필수입니다.");
        }
    }

    private void validateMenu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("메뉴 정보는 필수입니다.");
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        if (quantity > 99) {
            throw new IllegalArgumentException("수량은 99개를 초과할 수 없습니다.");
        }
    }

    private void validateUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("단가는 0보다 커야 합니다.");
        }
    }
} 