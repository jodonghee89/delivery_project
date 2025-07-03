package com.project.api.domain.store;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 매장 통계 Entity
 * 
 * 매장의 매출, 주문, 평점 등의 통계 정보
 */
@Entity
@Table(name = "store_statistics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreStatistics {

    @Id
    @Column(name = "store_id")
    private Long storeId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(name = "total_sales", precision = 15, scale = 2)
    private BigDecimal totalSales;

    @Column(name = "total_orders")
    private Integer totalOrders;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Builder
    private StoreStatistics(Store store, BigDecimal totalSales, Integer totalOrders, BigDecimal averageRating) {
        validateStore(store);
        
        this.store = store;
        this.storeId = store.getStoreId();
        this.totalSales = totalSales != null ? totalSales : BigDecimal.ZERO;
        this.totalOrders = totalOrders != null ? totalOrders : 0;
        this.averageRating = averageRating != null ? averageRating : BigDecimal.ZERO;
    }

    // == 정적 팩토리 메서드 == //
    
    /**
     * 초기 통계 생성
     */
    public static StoreStatistics createInitial(Store store) {
        return StoreStatistics.builder()
                .store(store)
                .totalSales(BigDecimal.ZERO)
                .totalOrders(0)
                .averageRating(BigDecimal.ZERO)
                .build();
    }

    // == 비즈니스 로직 == //
    
    /**
     * 통계 정보 업데이트
     */
    public void updateStatistics(BigDecimal totalSales, Integer totalOrders, BigDecimal averageRating) {
        validateTotalSales(totalSales);
        validateTotalOrders(totalOrders);
        validateAverageRating(averageRating);
        
        this.totalSales = totalSales;
        this.totalOrders = totalOrders;
        this.averageRating = averageRating.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 매출 추가
     */
    public void addSales(BigDecimal salesAmount) {
        validateSalesAmount(salesAmount);
        this.totalSales = this.totalSales.add(salesAmount);
    }

    /**
     * 주문 수 증가
     */
    public void incrementOrderCount() {
        this.totalOrders = this.totalOrders + 1;
    }

    /**
     * 평점 업데이트
     */
    public void updateAverageRating(BigDecimal newRating) {
        validateAverageRating(newRating);
        this.averageRating = newRating.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 주문당 평균 매출 계산
     */
    public BigDecimal getAverageOrderValue() {
        if (totalOrders == 0) {
            return BigDecimal.ZERO;
        }
        return totalSales.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);
    }

    /**
     * 매출이 있는지 확인
     */
    public boolean hasSales() {
        return totalSales.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 주문이 있는지 확인
     */
    public boolean hasOrders() {
        return totalOrders > 0;
    }

    /**
     * 평점이 설정되어 있는지 확인
     */
    public boolean hasRating() {
        return averageRating.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 좋은 평점인지 확인 (4.0 이상)
     */
    public boolean hasGoodRating() {
        return averageRating.compareTo(BigDecimal.valueOf(4.0)) >= 0;
    }

    /**
     * 우수 매장인지 확인 (평점 4.5 이상, 주문 10개 이상)
     */
    public boolean isExcellentStore() {
        return averageRating.compareTo(BigDecimal.valueOf(4.5)) >= 0 && 
               totalOrders >= 10;
    }

    // == 유효성 검증 == //
    
    private void validateStore(Store store) {
        if (store == null) {
            throw new IllegalArgumentException("매장 정보는 필수입니다.");
        }
    }

    private void validateTotalSales(BigDecimal totalSales) {
        if (totalSales == null) {
            throw new IllegalArgumentException("총 매출은 필수입니다.");
        }
        if (totalSales.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("총 매출은 0 이상이어야 합니다.");
        }
    }

    private void validateTotalOrders(Integer totalOrders) {
        if (totalOrders == null) {
            throw new IllegalArgumentException("총 주문 수는 필수입니다.");
        }
        if (totalOrders < 0) {
            throw new IllegalArgumentException("총 주문 수는 0 이상이어야 합니다.");
        }
    }

    private void validateAverageRating(BigDecimal averageRating) {
        if (averageRating == null) {
            throw new IllegalArgumentException("평균 평점은 필수입니다.");
        }
        if (averageRating.compareTo(BigDecimal.ZERO) < 0 || 
            averageRating.compareTo(BigDecimal.valueOf(5)) > 0) {
            throw new IllegalArgumentException("평균 평점은 0.0에서 5.0 사이여야 합니다.");
        }
    }

    private void validateSalesAmount(BigDecimal salesAmount) {
        if (salesAmount == null) {
            throw new IllegalArgumentException("매출 금액은 필수입니다.");
        }
        if (salesAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("매출 금액은 0보다 커야 합니다.");
        }
    }
} 