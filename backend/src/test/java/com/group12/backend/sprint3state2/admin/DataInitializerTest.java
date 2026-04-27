package com.group12.backend.sprint3state2.admin;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.group12.backend.config.DataInitializer;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.BillingService;
import com.group12.backend.service.LegacyScooterTypeRepairService;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataInitializer")
class DataInitializerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private BillingService billingService;
    @Mock
    private LegacyScooterTypeRepairService legacyScooterTypeRepairService;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    @DisplayName("run_alwaysExecutesBillingSetupAndLegacyRepair")
    void run_alwaysExecutesBillingSetupAndLegacyRepair() throws Exception {
        when(userRepository.findByEmail("admin@admin.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("admin123")).thenReturn("encoded-password");

        dataInitializer.run();

        verify(billingService).getCurrentRule();
        verify(legacyScooterTypeRepairService).repairLegacyGen1Types();
    }
}
