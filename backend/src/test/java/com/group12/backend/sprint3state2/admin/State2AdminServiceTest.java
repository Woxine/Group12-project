package com.group12.backend.sprint3state2.admin;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group12.backend.service.impl.AdminServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Sprint3-State2 AdminService TODO")
class State2AdminServiceTest {

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    @DisplayName("getPopularRentalDatesThisWeek_todoThrows")
    void getPopularRentalDatesThisWeek_todoThrows() {
        assertThatThrownBy(() -> adminService.getPopularRentalDatesThisWeek())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("getPopularRentalDatesThisWeek");
    }
}
