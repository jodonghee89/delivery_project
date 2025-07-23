package com.project.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;

/**
 * 웹 UI 컨트롤러 (독립 실행)
 * API 서버와 분리되어 독립적으로 작동합니다.
 */
@Controller
public class WebController {

    /**
     * 대시보드 메인 페이지
     */
    @GetMapping("/")
    public String dashboard(Model model) {
        // API 연동 없이 기본 데이터로 테스트
        model.addAttribute("customerCount", 0);
        model.addAttribute("orderCount", 0);
        model.addAttribute("totalSales", 0);
        model.addAttribute("totalCustomers", 0);
        model.addAttribute("totalOrders", 0);
        model.addAttribute("recentCustomers", Collections.emptyList());
        model.addAttribute("recentOrders", Collections.emptyList());
        return "simple-dashboard";
    }

    /**
     * 테스트 페이지
     */
    @GetMapping("/test")
    public String test() {
        return "test";
    }

    /**
     * 고객 목록 페이지
     */
    @GetMapping("/customers")
    public String customers(Model model) {
        model.addAttribute("customers", Collections.emptyList());
        model.addAttribute("totalCustomers", 0);
        return "customers/list";
    }

    /**
     * 주문 목록 페이지
     */
    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", Collections.emptyList());
        model.addAttribute("totalOrders", 0);
        return "orders/list";
    }

    /**
     * API 테스트 페이지
     */
    @GetMapping("/api-test")
    public String apiTest(Model model) {
        model.addAttribute("apiBaseUrl", "http://localhost:8080/delivery");
        return "api-test";
    }
} 