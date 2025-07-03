package com.project.api.domain.menu;

import com.project.api.domain.store.Store;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메뉴 카테고리 Entity
 */
@Entity
@Table(name = "menu_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Builder
    private MenuCategory(Store store, String name) {
        this.store = store;
        this.name = name;
    }

    public void changeName(String newName) {
        this.name = newName;
    }
} 