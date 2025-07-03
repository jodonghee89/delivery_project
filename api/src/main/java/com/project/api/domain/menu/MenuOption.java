package com.project.api.domain.menu;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 메뉴 옵션 Entity
 */
@Entity
@Table(name = "menu_options")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "additional_price", precision = 10, scale = 2)
    private BigDecimal additionalPrice;

    @Builder
    private MenuOption(Menu menu, String name, BigDecimal additionalPrice) {
        this.menu = menu;
        this.name = name;
        this.additionalPrice = additionalPrice != null ? additionalPrice : BigDecimal.ZERO;
    }

    public void updateInfo(String name, BigDecimal additionalPrice) {
        this.name = name;
        this.additionalPrice = additionalPrice;
    }
} 