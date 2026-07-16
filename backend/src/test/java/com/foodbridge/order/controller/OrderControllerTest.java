package com.foodbridge.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodbridge.common.exception.ResourceNotFoundException;
import com.foodbridge.order.dto.OrderResponse;
import com.foodbridge.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    @WithMockUser(username = "testuser@example.com")
    void getOrderById_Success_ReturnsApiResponse() throws Exception {
        OrderResponse response = OrderResponse.builder()
                .id("order-1")
                .recipientId("user-1")
                .build();

        when(orderService.getOrderById("order-1")).thenReturn(response);

        mockMvc.perform(get("/v1/orders/order-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Order retrieved."))
                .andExpect(jsonPath("$.data.id").value("order-1"))
                .andExpect(jsonPath("$.data.recipientId").value("user-1"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    void getOrderById_NotFound_ReturnsStandardErrorResponse() throws Exception {
        when(orderService.getOrderById("invalid-id"))
                .thenThrow(new ResourceNotFoundException("Order", "id", "invalid-id"));

        mockMvc.perform(get("/v1/orders/invalid-id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Order not found with id: 'invalid-id'"))
                .andExpect(jsonPath("$.error").value("Resource not found"));
    }
}
