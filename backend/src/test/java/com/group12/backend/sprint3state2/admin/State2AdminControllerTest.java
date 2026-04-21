package com.group12.backend.sprint3state2.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.group12.backend.controller.AdminController;
import com.group12.backend.dto.PopularRentalDateDTO;
import com.group12.backend.security.AdminAccessGuard;
import com.group12.backend.service.AdminService;
import com.group12.backend.service.BillingService;
import com.group12.backend.service.ScooterService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Sprint3-State2 AdminController")
class State2AdminControllerTest {

    @Mock
    private AdminService adminService;
    @Mock
    private AdminAccessGuard adminAccessGuard;
    @Mock
    private BillingService billingService;
    @Mock
    private ScooterService scooterService;

    private AdminController controller;

    @BeforeEach
    void setUp() {
        controller = new AdminController();
        ReflectionTestUtils.setField(controller, "adminService", adminService);
        ReflectionTestUtils.setField(controller, "adminAccessGuard", adminAccessGuard);
        ReflectionTestUtils.setField(controller, "billingService", billingService);
        ReflectionTestUtils.setField(controller, "scooterService", scooterService);
    }

    @Test
    @DisplayName("getPopularRentalDatesThisWeek_returnsData")
    void getPopularRentalDatesThisWeek_returnsData() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        PopularRentalDateDTO item = new PopularRentalDateDTO();
        item.setRank(1);
        List<PopularRentalDateDTO> data = List.of(item);
        when(adminService.getPopularRentalDatesThisWeek()).thenReturn(data);

        Object body = controller.getPopularRentalDatesThisWeek(request).getBody();
        assertThat(body).isEqualTo(Map.of("data", data));

        verify(adminAccessGuard).requireAdmin(request);
        verify(adminService).getPopularRentalDatesThisWeek();
    }
}
