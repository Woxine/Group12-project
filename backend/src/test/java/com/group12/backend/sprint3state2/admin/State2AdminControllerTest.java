package com.group12.backend.sprint3state2.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.group12.backend.dto.BulkScooterUpdateRequest;
import com.group12.backend.dto.BillingSettingsResponse;
import com.group12.backend.dto.PopularRentalDateDTO;
import com.group12.backend.dto.ScooterBulkApplyResponse;
import com.group12.backend.dto.ScooterBulkPreviewResponse;
import com.group12.backend.dto.UpdateBillingSettingsRequest;
import com.group12.backend.security.AdminAccessGuard;
import com.group12.backend.service.AdminService;
import com.group12.backend.service.BillingRule;
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

    @Test
    @DisplayName("getPopularRentalDates_withRange_returnsData")
    void getPopularRentalDates_withRange_returnsData() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 7);
        PopularRentalDateDTO item = new PopularRentalDateDTO();
        item.setRank(1);
        List<PopularRentalDateDTO> data = List.of(item);
        when(adminService.getPopularRentalDates(startDate, endDate)).thenReturn(data);

        Object body = controller.getPopularRentalDates(startDate, endDate, request).getBody();
        assertThat(body).isEqualTo(Map.of("data", data));

        verify(adminAccessGuard).requireAdmin(request);
        verify(adminService).getPopularRentalDates(startDate, endDate);
    }

    @Test
    @DisplayName("previewBulkUpdateByType_returnsData")
    void previewBulkUpdateByType_returnsData() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BulkScooterUpdateRequest body = new BulkScooterUpdateRequest();
        body.setType("GEN1");
        body.setHour_rate(java.math.BigDecimal.valueOf(4.2));

        ScooterBulkPreviewResponse response = new ScooterBulkPreviewResponse();
        response.setType("GEN1");
        response.setMatchedCount(3);
        when(scooterService.previewBulkUpdateByType(body)).thenReturn(response);

        Object result = controller.previewBulkUpdateByType(body, request).getBody();
        assertThat(result).isEqualTo(Map.of("data", response));

        verify(adminAccessGuard).requireAdmin(request);
        verify(scooterService).previewBulkUpdateByType(body);
    }

    @Test
    @DisplayName("applyBulkUpdateByType_returnsData")
    void applyBulkUpdateByType_returnsData() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        BulkScooterUpdateRequest body = new BulkScooterUpdateRequest();
        body.setType("GEN2");
        body.setStatus("MAINTENANCE");
        body.setConfirm_risky(true);

        ScooterBulkApplyResponse response = new ScooterBulkApplyResponse();
        response.setType("GEN2");
        response.setUpdatedCount(5);
        when(scooterService.applyBulkUpdateByType(body)).thenReturn(response);

        Object result = controller.applyBulkUpdateByType(body, request).getBody();
        assertThat(result).isEqualTo(Map.of("data", response));

        verify(adminAccessGuard).requireAdmin(request);
        verify(scooterService).applyBulkUpdateByType(body);
    }

    @Test
    @DisplayName("updateBillingSettings_patchStudentRate_returnsData")
    void updateBillingSettings_patchStudentRate_returnsData() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", "101");
        UpdateBillingSettingsRequest body = new UpdateBillingSettingsRequest();
        body.setStudentDiscountRate(0.72);

        BillingRule rule = new BillingRule(
                new BigDecimal("24"),
                new BigDecimal("72"),
                new BigDecimal("0.85"),
                new BigDecimal("0.75"),
                new BigDecimal("0.72"),
                new BigDecimal("0.80"),
                new BigDecimal("0.78"),
                LocalDateTime.of(2026, 4, 26, 20, 0)
        );
        when(billingService.updateSettings(
                eq(null),
                eq(null),
                eq(BigDecimal.valueOf(0.72)),
                eq(null),
                eq(null),
                eq(101L)
        )).thenReturn(rule);

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) controller.updateBillingSettings(body, request).getBody();
        assertThat(result).isNotNull();
        assertThat(result).containsKey("data");
        BillingSettingsResponse data = (BillingSettingsResponse) result.get("data");
        assertThat(data.getStudentDiscountRate()).isEqualTo(0.72d);
        assertThat(data.getSeniorDiscountRate()).isEqualTo(0.80d);
        assertThat(data.getFrequentDiscountRate()).isEqualTo(0.78d);

        verify(adminAccessGuard).requireAdmin(request);
    }
}
