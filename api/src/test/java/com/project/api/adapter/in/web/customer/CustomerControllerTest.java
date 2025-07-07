package com.project.api.adapter.in.web.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.api.adapter.in.web.customer.dto.CreateCustomerRequest;
import com.project.api.adapter.in.web.customer.dto.CreateAddressRequest;
import com.project.api.adapter.in.web.customer.dto.UpdateCustomerRequest;
import com.project.api.adapter.in.web.customer.dto.UpdateAddressRequest;
import com.project.api.domain.customer.Customer;
import com.project.api.domain.customer.CustomerAddress;
import com.project.api.port.out.customer.CustomerRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Customer REST API 통합 테스트
 * 실제 스프링 컨텍스트와 데이터베이스를 사용한 통합 테스트
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class CustomerControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("고객 생성 - 성공")
    void createCustomer_Success() throws Exception {
        // Given
        CreateCustomerRequest request = new CreateCustomerRequest(
                "홍길동",
                "hongildong@example.com",
                "010-1234-5678",
                "password123!",
                "서울특별시 강남구 테헤란로 123",
                "456호",
                "12345"
        );

        // When & Then
        mockMvc.perform(post("/delivery/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.email").value("hongildong@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("010-1234-5678"))
                .andExpect(jsonPath("$.addresses").isArray())
                .andExpect(jsonPath("$.addresses[0].address").value("서울특별시 강남구 테헤란로 123"))
                .andExpect(jsonPath("$.addresses[0].isDefault").value(true));
    }

    @Test
    @DisplayName("고객 생성 - 유효성 검증 실패")
    void createCustomer_ValidationFailed() throws Exception {
        // Given
        CreateCustomerRequest request = new CreateCustomerRequest(
                "", // 이름 빈값
                "invalid-email", // 잘못된 이메일
                "010-1234", // 잘못된 전화번호
                "123", // 짧은 비밀번호
                null,
                null,
                null
        );

        // When & Then
        mockMvc.perform(post("/delivery/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("고객 정보 조회 - 성공")
    void getCustomer_Success() throws Exception {
        // Given
        Customer customer = Customer.builder()
                .name("홍길동")
                .email("hongildong@example.com")
                .phoneNumber("010-1234-5678")
                .password("password123!")
                .build();
        Customer savedCustomer = customerRepository.save(customer);

        // When & Then
        mockMvc.perform(get("/delivery/customers/{customerId}", savedCustomer.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.email").value("hongildong@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("010-1234-5678"));
    }

    @Test
    @DisplayName("고객 정보 조회 - 존재하지 않는 고객")
    void getCustomer_NotFound() throws Exception {
        // Given
        Long nonExistentId = 99999L;

        // When & Then
        mockMvc.perform(get("/delivery/customers/{customerId}", nonExistentId))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // RuntimeException으로 인한 500 에러
    }

    @Test
    @DisplayName("고객 정보 수정 - 성공")
    void updateCustomer_Success() throws Exception {
        // Given
        Customer customer = Customer.builder()
                .name("홍길동")
                .email("hongildong@example.com")
                .phoneNumber("010-1234-5678")
                .password("password123!")
                .build();
        Customer savedCustomer = customerRepository.save(customer);

        UpdateCustomerRequest request = new UpdateCustomerRequest(
                "홍길동_수정",
                "hongildong_new@example.com",
                "010-9876-5432"
        );

        // When & Then
        mockMvc.perform(put("/delivery/customers/{customerId}", savedCustomer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("홍길동_수정"))
                .andExpect(jsonPath("$.email").value("hongildong_new@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("010-9876-5432"));
    }

    @Test
    @DisplayName("고객 탈퇴 - 성공")
    void deleteCustomer_Success() throws Exception {
        // Given
        Customer customer = Customer.builder()
                .name("홍길동")
                .email("hongildong@example.com")
                .phoneNumber("010-1234-5678")
                .password("password123!")
                .build();
        Customer savedCustomer = customerRepository.save(customer);

        // When & Then
        mockMvc.perform(delete("/delivery/customers/{customerId}", savedCustomer.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("배달 주소 추가 - 성공")
    void addCustomerAddress_Success() throws Exception {
        // Given
        Customer customer = Customer.builder()
                .name("홍길동")
                .email("hongildong@example.com")
                .phoneNumber("010-1234-5678")
                .password("password123!")
                .build();
        Customer savedCustomer = customerRepository.save(customer);

        CreateAddressRequest request = new CreateAddressRequest(
                "부산광역시 해운대구 해운대로 123",
                "789호",
                "48000",
                "집",
                true
        );

        // When & Then
        mockMvc.perform(post("/delivery/customers/{customerId}/addresses", savedCustomer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value("부산광역시 해운대구 해운대로 123"))
                .andExpect(jsonPath("$.addressDetail").value("789호"))
                .andExpect(jsonPath("$.zipCode").value("48000"))
                .andExpect(jsonPath("$.nickname").value("집"))
                .andExpect(jsonPath("$.isDefault").value(true));
    }

    @Test
    @DisplayName("배달 주소 목록 조회 - 성공")
    void getCustomerAddresses_Success() throws Exception {
        // Given
        Customer customer = Customer.builder()
                .name("홍길동")
                .email("hongildong@example.com")
                .phoneNumber("010-1234-5678")
                .password("password123!")
                .build();

        CustomerAddress address1 = CustomerAddress.builder()
                .address("서울특별시 강남구 테헤란로 123")
                .addressDetail("456호")
                .zipCode("12345")
                .nickname("집")
                .isDefault(true)
                .build();

        CustomerAddress address2 = CustomerAddress.builder()
                .address("부산광역시 해운대구 해운대로 456")
                .addressDetail("789호")
                .zipCode("48000")
                .nickname("회사")
                .isDefault(false)
                .build();

        customer.addAddress(address1);
        customer.addAddress(address2);
        Customer savedCustomer = customerRepository.save(customer);

        // When & Then
        mockMvc.perform(get("/delivery/customers/{customerId}/addresses", savedCustomer.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].address").value("서울특별시 강남구 테헤란로 123"))
                .andExpect(jsonPath("$[0].isDefault").value(true))
                .andExpect(jsonPath("$[1].address").value("부산광역시 해운대구 해운대로 456"))
                .andExpect(jsonPath("$[1].isDefault").value(false));
    }

    @Test
    @DisplayName("배달 주소 수정 - 성공")
    void updateCustomerAddress_Success() throws Exception {
        // Given
        Customer customer = Customer.builder()
                .name("홍길동")
                .email("hongildong@example.com")
                .phoneNumber("010-1234-5678")
                .password("password123!")
                .build();

        CustomerAddress address = CustomerAddress.builder()
                .address("서울특별시 강남구 테헤란로 123")
                .addressDetail("456호")
                .zipCode("12345")
                .nickname("집")
                .isDefault(true)
                .build();

        customer.addAddress(address);
        Customer savedCustomer = customerRepository.save(customer);
        Long addressId = savedCustomer.getAddresses().get(0).getId();

        UpdateAddressRequest request = new UpdateAddressRequest(
                "서울특별시 강남구 테헤란로 456",
                "789호",
                "54321",
                "집_수정",
                false
        );

        // When & Then
        mockMvc.perform(put("/delivery/customers/{customerId}/addresses/{addressId}", 
                savedCustomer.getId(), addressId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("서울특별시 강남구 테헤란로 456"))
                .andExpect(jsonPath("$.addressDetail").value("789호"))
                .andExpect(jsonPath("$.zipCode").value("54321"))
                .andExpect(jsonPath("$.nickname").value("집_수정"))
                .andExpect(jsonPath("$.isDefault").value(false));
    }

    @Test
    @DisplayName("배달 주소 삭제 - 성공")
    void deleteCustomerAddress_Success() throws Exception {
        // Given
        Customer customer = Customer.builder()
                .name("홍길동")
                .email("hongildong@example.com")
                .phoneNumber("010-1234-5678")
                .password("password123!")
                .build();

        CustomerAddress address = CustomerAddress.builder()
                .address("서울특별시 강남구 테헤란로 123")
                .addressDetail("456호")
                .zipCode("12345")
                .nickname("집")
                .isDefault(false)
                .build();

        customer.addAddress(address);
        Customer savedCustomer = customerRepository.save(customer);
        Long addressId = savedCustomer.getAddresses().get(0).getId();

        // When & Then
        mockMvc.perform(delete("/delivery/customers/{customerId}/addresses/{addressId}", 
                savedCustomer.getId(), addressId))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
} 