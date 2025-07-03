package com.project.api.domain.store;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매장 카테고리 Entity
 * 
 * 매장 분류를 위한 카테고리 정보
 */
@Entity
@Table(name = "store_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @Builder
    private StoreCategory(String name) {
        validateName(name);
        this.name = name;
    }

    // == 비즈니스 로직 == //
    
    /**
     * 카테고리 이름 변경
     */
    public void changeName(String newName) {
        validateName(newName);
        this.name = newName;
    }

    /**
     * 특정 카테고리인지 확인
     */
    public boolean isCategory(String categoryName) {
        return this.name.equals(categoryName);
    }

    // == 유효성 검증 == //
    
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리 이름은 필수입니다.");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("카테고리 이름은 50자를 초과할 수 없습니다.");
        }
    }

    // == equals & hashCode == //
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        StoreCategory that = (StoreCategory) obj;
        return categoryId != null && categoryId.equals(that.categoryId);
    }

    @Override
    public int hashCode() {
        return categoryId != null ? categoryId.hashCode() : 0;
    }
} 