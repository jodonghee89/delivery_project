package com.project.api.adapter.in.web.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.api.adapter.in.web.order.dto.CreateOrderRequest;
import com.project.api.adapter.in.web.order.dto.UpdateOrderStatusRequest;
import com.project.api.adapter.in.web.order.dto.ReorderRequest;
import com.project.api.domain.customer.Customer;
import com.project.api.domain.order.Order;
import com.project.api.domain.order.OrderStatus;
import com.project.api.port.out.customer.CustomerRepository;
import com.project.api.port.out.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Order REST API 통합 테스트
 * 실제 스프링 컨텍스트와 데이터베이스를 사용한 통합 테스트
 */
@SpringBootTest(classes = com.project.api.TestApiApplication.class)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class OrderControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        // 테스트용 고객 생성
        testCustomer = Customer.builder()
                .name("테스트고객")
                .email("test@example.com")
                .phoneNumber("010-1234-5678")
                .password("password123!")
                .build();
        testCustomer = customerRepository.save(testCustomer);
    }

    @Test
    @DisplayName("주문 생성 - 성공")
    void createOrder_Success() throws Exception {
        // Given
        CreateOrderRequest.OrderItemRequest orderItem = new CreateOrderRequest.OrderItemRequest(
                1L, // menuId
                2,  // quantity
                15000, // price
                "매운맛으로 해주세요" // specialRequests
        );

        CreateOrderRequest request = new CreateOrderRequest(
                testCustomer.getId(),        // customerId
                1L,                         // storeId
                "서울특별시 강남구 테헤란로 123", // deliveryAddress
                "CREDIT_CARD",              // paymentMethod
                List.of(orderItem),         // orderItems
                "문 앞에 놓아주세요"          // specialRequests
        );

        // When & Then
        mockMvc.perform(post("/delivery/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(testCustomer.getId()))
                .andExpect(jsonPath("$.storeId").value(1L))
                .andExpect(jsonPath("$.orderStatus").value("PENDING"))
                .andExpect(jsonPath("$.deliveryAddress").value("서울특별시 강남구 테헤란로 123"))
                .andExpect(jsonPath("$.paymentMethod").value("CREDIT_CARD"))
                .andExpect(jsonPath("$.orderItems").isArray())
                .andExpect(jsonPath("$.orderItems[0].menuId").value(1L))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(2));
    }

    @Test
    @DisplayName("주문 생성 - 유효성 검증 실패")
    void createOrder_ValidationFailed() throws Exception {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
                null,                       // customerId 누락
                null,                       // storeId 누락
                "",                         // 빈 배달 주소
                "INVALID_PAYMENT",          // 잘못된 결제 방법
                Collections.emptyList(),    // 빈 주문 항목
                null                        // specialRequests
        );

        // When & Then
        mockMvc.perform(post("/delivery/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 조회 - 성공")
    void getOrder_Success() throws Exception {
        // Given
        Order order = Order.builder()
                .customerId(testCustomer.getId())
                .storeId(1L)
                .deliveryAddress("서울특별시 강남구 테헤란로 123")
                .memo("문 앞에 놓아주세요")
                .paymentMethodStr("CREDIT_CARD")
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(30000)
                .build();
        Order savedOrder = orderRepository.save(order);

        // When & Then
        mockMvc.perform(get("/delivery/orders/{orderId}", savedOrder.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(savedOrder.getId()))
                .andExpect(jsonPath("$.customerId").value(testCustomer.getId()))
                .andExpect(jsonPath("$.storeId").value(1L))
                .andExpect(jsonPath("$.orderStatus").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(30000));
    }

    @Test
    @DisplayName("주문 조회 - 존재하지 않는 주문")
    void getOrder_NotFound() throws Exception {
        // Given
        Long nonExistentId = 99999L;

        // When & Then
        mockMvc.perform(get("/delivery/orders/{orderId}", nonExistentId))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // RuntimeException으로 인한 500 에러
    }

    @Test
    @DisplayName("고객별 주문 목록 조회 - 성공")
    void getOrdersByCustomer_Success() throws Exception {
        // Given
        Order order1 = Order.builder()
                .customerId(testCustomer.getId())
                .storeId(1L)
                .deliveryAddress("서울특별시 강남구 테헤란로 123")
                .paymentMethodStr("CREDIT_CARD")
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(25000)
                .build();

        Order order2 = Order.builder()
                .customerId(testCustomer.getId())
                .storeId(2L)
                .deliveryAddress("서울특별시 강남구 테헤란로 456")
                .paymentMethodStr("CASH")
                .orderStatus(OrderStatus.COMPLETED)
                .totalAmount(35000)
                .build();

        orderRepository.save(order1);
        orderRepository.save(order2);

        // When & Then
        mockMvc.perform(get("/delivery/orders/customer/{customerId}", testCustomer.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].customerId").value(testCustomer.getId()))
                .andExpect(jsonPath("$[1].customerId").value(testCustomer.getId()));
    }

    @Test
    @DisplayName("매장별 주문 목록 조회 - 성공")
    void getOrdersByStore_Success() throws Exception {
        // Given
        Long storeId = 1L;
        Order order1 = Order.builder()
                .customerId(testCustomer.getId())
                .storeId(storeId)
                .deliveryAddress("서울특별시 강남구 테헤란로 123")
                .paymentMethodStr("CREDIT_CARD")
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(25000)
                .build();

        Order order2 = Order.builder()
                .customerId(testCustomer.getId())
                .storeId(storeId)
                .deliveryAddress("서울특별시 강남구 테헤란로 456")
                .paymentMethodStr("CASH")
                .orderStatus(OrderStatus.PREPARING)
                .totalAmount(35000)
                .build();

        orderRepository.save(order1);
        orderRepository.save(order2);

        // When & Then
        mockMvc.perform(get("/delivery/orders/store/{storeId}", storeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].storeId").value(storeId))
                .andExpect(jsonPath("$[1].storeId").value(storeId));
    }

    @Test
    @DisplayName("주문 상태 업데이트 - 성공")
    void updateOrderStatus_Success() throws Exception {
        // Given
        Order order = Order.builder()
                .customerId(testCustomer.getId())
                .storeId(1L)
                .deliveryAddress("서울특별시 강남구 테헤란로 123")
                .paymentMethodStr("CREDIT_CARD")
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(30000)
                .build();
        Order savedOrder = orderRepository.save(order);

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(
                "CONFIRMED",
                "주문 확인 완료"
        );

        // When & Then
        mockMvc.perform(put("/delivery/orders/{orderId}/status", savedOrder.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CONFIRMED"));
    }

    @Test
    @DisplayName("주문 취소 - 성공")
    void cancelOrder_Success() throws Exception {
        // Given
        Order order = Order.builder()
                .customerId(testCustomer.getId())
                .storeId(1L)
                .deliveryAddress("서울특별시 강남구 테헤란로 123")
                .paymentMethodStr("CREDIT_CARD")
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(30000)
                .build();
        Order savedOrder = orderRepository.save(order);

        // When & Then
        mockMvc.perform(delete("/delivery/orders/{orderId}", savedOrder.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("재주문 - 성공")
    void reorder_Success() throws Exception {
        // Given
        Order originalOrder = Order.builder()
                .customerId(testCustomer.getId())
                .storeId(1L)
                .deliveryAddress("서울특별시 강남구 테헤란로 123")
                .paymentMethodStr("CREDIT_CARD")
                .orderStatus(OrderStatus.COMPLETED)
                .totalAmount(30000)
                .build();
        Order savedOrder = orderRepository.save(originalOrder);

        ReorderRequest request = new ReorderRequest(
                "서울특별시 강남구 테헤란로 789",
                "빨리 배달해주세요"
        );

        // When & Then
        mockMvc.perform(post("/delivery/orders/{orderId}/reorder", savedOrder.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(testCustomer.getId()))
                .andExpect(jsonPath("$.storeId").value(1L))
                .andExpect(jsonPath("$.orderStatus").value("PENDING"))
                .andExpect(jsonPath("$.deliveryAddress").value("서울특별시 강남구 테헤란로 789"))
                .andExpect(jsonPath("$.paymentMethod").value("CASH"));
    }

    // ========== 실패 케이스 테스트 ==========

    @Test
    @DisplayName("주문 생성 - 존재하지 않는 고객")
    void createOrder_CustomerNotFound() throws Exception {
        // Given
        CreateOrderRequest.OrderItemRequest orderItem = new CreateOrderRequest.OrderItemRequest(
                1L, 2, 15000, "매운맛으로 해주세요"
        );

        CreateOrderRequest request = new CreateOrderRequest(
                99999L,                     // 존재하지 않는 고객 ID
                1L,
                "서울특별시 강남구 테헤란로 123",
                "CREDIT_CARD",
                List.of(orderItem),
                "문 앞에 놓아주세요"
        );

        // When & Then
        mockMvc.perform(post("/delivery/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // RuntimeException으로 인한 500 에러
    }

    @Test
    @DisplayName("주문 생성 - 빈 주문 항목 리스트")
    void createOrder_EmptyOrderItems() throws Exception {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
                testCustomer.getId(),
                1L,
                "서울특별시 강남구 테헤란로 123",
                "CREDIT_CARD",
                Collections.emptyList(),    // 빈 주문 항목
                "문 앞에 놓아주세요"
        );

        // When & Then
        mockMvc.perform(post("/delivery/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 - 잘못된 결제 방법")
    void createOrder_InvalidPaymentMethod() throws Exception {
        // Given
        CreateOrderRequest.OrderItemRequest orderItem = new CreateOrderRequest.OrderItemRequest(
                1L, 2, 15000, "매운맛으로 해주세요"
        );

        CreateOrderRequest request = new CreateOrderRequest(
                testCustomer.getId(),
                1L,
                "서울특별시 강남구 테헤란로 123",
                "INVALID_PAYMENT",          // 잘못된 결제 방법
                List.of(orderItem),
                "문 앞에 놓아주세요"
        );

        // When & Then
        mockMvc.perform(post("/delivery/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 - 음수 수량")
    void createOrder_NegativeQuantity() throws Exception {
        // Given
        CreateOrderRequest.OrderItemRequest orderItem = new CreateOrderRequest.OrderItemRequest(
                1L, -1, 15000, "매운맛으로 해주세요"  // 음수 수량
        );

        CreateOrderRequest request = new CreateOrderRequest(
                testCustomer.getId(),
                1L,
                "서울특별시 강남구 테헤란로 123",
                "CREDIT_CARD",
                List.of(orderItem),
                "문 앞에 놓아주세요"
        );

        // When & Then
        mockMvc.perform(post("/delivery/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 - 음수 가격")
    void createOrder_NegativePrice() throws Exception {
        // Given
        CreateOrderRequest.OrderItemRequest orderItem = new CreateOrderRequest.OrderItemRequest(
                1L, 2, -15000, "매운맛으로 해주세요"  // 음수 가격
        );

        CreateOrderRequest request = new CreateOrderRequest(
                testCustomer.getId(),
                1L,
                "서울특별시 강남구 테헤란로 123",
                "CREDIT_CARD",
                List.of(orderItem),
                "문 앞에 놓아주세요"
        );

        // When & Then
        mockMvc.perform(post("/delivery/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 - 빈 배달 주소")
    void createOrder_EmptyDeliveryAddress() throws Exception {
        // Given
        CreateOrderRequest.OrderItemRequest orderItem = new CreateOrderRequest.OrderItemRequest(
                1L, 2, 15000, "매운맛으로 해주세요"
        );

        CreateOrderRequest request = new CreateOrderRequest(
                testCustomer.getId(),
                1L,
                "",                         // 빈 배달 주소
                "CREDIT_CARD",
                List.of(orderItem),
                "문 앞에 놓아주세요"
        );

        // When & Then
        mockMvc.perform(post("/delivery/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 상태 업데이트 - 존재하지 않는 주문")
    void updateOrderStatus_OrderNotFound() throws Exception {
        // Given
        Long nonExistentId = 99999L;
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest("CONFIRMED", "상태 불량");

        // When & Then
        mockMvc.perform(put("/delivery/orders/{orderId}/status", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // RuntimeException으로 인한 500 에러
    }

    @Test
    @DisplayName("주문 상태 업데이트 - 잘못된 상태")
    void updateOrderStatus_InvalidStatus() throws Exception {
        // Given
        Order order = Order.builder()
                .customerId(testCustomer.getId())
                .storeId(1L)
                .deliveryAddress("서울특별시 강남구 테헤란로 123")
                .paymentMethodStr("CREDIT_CARD")
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(30000)
                .build();
        Order savedOrder = orderRepository.save(order);

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest("INVALID_STATUS", "단순변심");

        // When & Then
        mockMvc.perform(put("/delivery/orders/{orderId}/status", savedOrder.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 상태 업데이트 - 이미 완료된 주문")
    void updateOrderStatus_AlreadyCompleted() throws Exception {
        // Given
        Order order = Order.builder()
                .customerId(testCustomer.getId())
                .storeId(1L)
                .deliveryAddress("서울특별시 강남구 테헤란로 123")
                .paymentMethodStr("CREDIT_CARD")
                .orderStatus(OrderStatus.COMPLETED)  // 이미 완료된 상태
                .totalAmount(30000)
                .build();
        Order savedOrder = orderRepository.save(order);

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest("CONFIRMED", "단순변심");

        // When & Then
        mockMvc.perform(put("/delivery/orders/{orderId}/status", savedOrder.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest()); // 비즈니스 로직 위반
    }

    @Test
    @DisplayName("주문 취소 - 존재하지 않는 주문")
    void cancelOrder_OrderNotFound() throws Exception {
        // Given
        Long nonExistentId = 99999L;

        // When & Then
        mockMvc.perform(delete("/delivery/orders/{orderId}", nonExistentId))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // RuntimeException으로 인한 500 에러
    }

    @Test
    @DisplayName("주문 취소 - 이미 완료된 주문")
    void cancelOrder_AlreadyCompleted() throws Exception {
        // Given
        Order order = Order.builder()
                .customerId(testCustomer.getId())
                .storeId(1L)
                .deliveryAddress("서울특별시 강남구 테헤란로 123")
                .paymentMethodStr("CREDIT_CARD")
                .orderStatus(OrderStatus.COMPLETED)  // 이미 완료된 상태
                .totalAmount(30000)
                .build();
        Order savedOrder = orderRepository.save(order);

        // When & Then
        mockMvc.perform(delete("/delivery/orders/{orderId}", savedOrder.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest()); // 비즈니스 로직 위반
    }

    @Test
    @DisplayName("주문 취소 - 이미 취소된 주문")
    void cancelOrder_AlreadyCancelled() throws Exception {
        // Given
        Order order = Order.builder()
                .customerId(testCustomer.getId())
                .storeId(1L)
                .deliveryAddress("서울특별시 강남구 테헤란로 123")
                .paymentMethodStr("CREDIT_CARD")
                .orderStatus(OrderStatus.CANCELLED)  // 이미 취소된 상태
                .totalAmount(30000)
                .build();
        Order savedOrder = orderRepository.save(order);

        // When & Then
        mockMvc.perform(delete("/delivery/orders/{orderId}", savedOrder.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest()); // 비즈니스 로직 위반
    }

    @Test
    @DisplayName("재주문 - 존재하지 않는 주문")
    void reorder_OrderNotFound() throws Exception {
        // Given
        Long nonExistentId = 99999L;
        ReorderRequest request = new ReorderRequest(
                "서울특별시 강남구 테헤란로 789",
                //"CASH",
                "빨리 배달해주세요"
        );

        // When & Then
        mockMvc.perform(post("/delivery/orders/{orderId}/reorder", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // RuntimeException으로 인한 500 에러
    }

    @Test
    @DisplayName("재주문 - 취소된 주문")
    void reorder_CancelledOrder() throws Exception {
        // Given
        Order originalOrder = Order.builder()
                .customerId(testCustomer.getId())
                .storeId(1L)
                .deliveryAddress("서울특별시 강남구 테헤란로 123")
                .paymentMethodStr("CREDIT_CARD")
                .orderStatus(OrderStatus.CANCELLED)  // 취소된 주문
                .totalAmount(30000)
                .build();
        Order savedOrder = orderRepository.save(originalOrder);

        ReorderRequest request = new ReorderRequest(
                "서울특별시 강남구 테헤란로 789",
                //"CASH",
                "빨리 배달해주세요"
        );

        // When & Then
        mockMvc.perform(post("/delivery/orders/{orderId}/reorder", savedOrder.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest()); // 비즈니스 로직 위반
    }

    @Test
    @DisplayName("고객별 주문 목록 조회 - 존재하지 않는 고객")
    void getOrdersByCustomer_CustomerNotFound() throws Exception {
        // Given
        Long nonExistentCustomerId = 99999L;

        // When & Then
        mockMvc.perform(get("/delivery/orders/customer/{customerId}", nonExistentCustomerId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0)); // 빈 배열 반환
    }

    @Test
    @DisplayName("매장별 주문 목록 조회 - 존재하지 않는 매장")
    void getOrdersByStore_StoreNotFound() throws Exception {
        // Given
        Long nonExistentStoreId = 99999L;

        // When & Then
        mockMvc.perform(get("/delivery/orders/store/{storeId}", nonExistentStoreId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0)); // 빈 배열 반환
    }

    @Test
    @DisplayName("잘못된 HTTP 메서드 - Method Not Allowed")
    void wrongHttpMethod_MethodNotAllowed() throws Exception {
        // When & Then
        mockMvc.perform(patch("/delivery/orders/1"))  // PATCH 메서드는 지원하지 않음
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("잘못된 JSON 형식 - Bad Request")
    void invalidJsonFormat_BadRequest() throws Exception {
        // Given
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/delivery/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Content-Type 누락 - Unsupported Media Type")
    void missingContentType_UnsupportedMediaType() throws Exception {
        // Given
        CreateOrderRequest.OrderItemRequest orderItem = new CreateOrderRequest.OrderItemRequest(
                1L, 2, 15000, "매운맛으로 해주세요"
        );

        CreateOrderRequest request = new CreateOrderRequest(
                testCustomer.getId(),
                1L,
                "서울특별시 강남구 테헤란로 123",
                "CREDIT_CARD",
                List.of(orderItem),
                "문 앞에 놓아주세요"
        );

        // When & Then
        mockMvc.perform(post("/delivery/orders")
                // .contentType(MediaType.APPLICATION_JSON) 생략
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

} 