package com.project.api.port.in.store;

import com.project.api.domain.store.Store;
import com.project.api.domain.store.StoreStatistics;
import java.util.List;

/**
 * 매장 관리 Use Case
 * 매장 등록, 수정, 조회, 삭제 등의 기능을 제공합니다.
 */
public interface ManageStoreUseCase {
    
    /**
     * 새로운 매장을 등록합니다.
     * 
     * @param name 매장명
     * @param address 매장 주소
     * @param phoneNumber 매장 전화번호
     * @param categoryId 매장 카테고리 ID
     * @param ownerId 매장 사장 ID
     * @return 등록된 매장 정보
     */
    Store createStore(String name, String address, String phoneNumber, Long categoryId, Long ownerId);
    
    /**
     * 매장 정보를 조회합니다.
     * 
     * @param storeId 매장 ID
     * @return 매장 정보
     */
    Store getStore(Long storeId);
    
    /**
     * 매장 정보를 수정합니다.
     * 
     * @param storeId 매장 ID
     * @param name 수정할 매장명 (null이면 수정하지 않음)
     * @param address 수정할 주소 (null이면 수정하지 않음)
     * @param phoneNumber 수정할 전화번호 (null이면 수정하지 않음)
     * @param categoryId 수정할 카테고리 ID (null이면 수정하지 않음)
     * @return 수정된 매장 정보
     */
    Store updateStore(Long storeId, String name, String address, String phoneNumber, Long categoryId);
    
    /**
     * 매장을 삭제합니다.
     * 
     * @param storeId 매장 ID
     */
    void deleteStore(Long storeId);
    
    /**
     * 매장 목록을 조회합니다.
     * 
     * @param categoryId 카테고리 ID (null이면 전체)
     * @param searchKeyword 검색 키워드 (null이면 전체)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 매장 목록
     */
    List<Store> getStores(Long categoryId, String searchKeyword, int page, int size);
    
    /**
     * 매장 통계 정보를 조회합니다.
     * 
     * @param storeId 매장 ID
     * @param year 조회할 년도
     * @param month 조회할 월 (null이면 전체 년도)
     * @return 매장 통계 정보
     */
    StoreStatistics getStoreStatistics(Long storeId, int year, Integer month);
} 