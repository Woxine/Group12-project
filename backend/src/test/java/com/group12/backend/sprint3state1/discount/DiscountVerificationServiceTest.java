package com.group12.backend.sprint3state1.discount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.group12.backend.dto.DiscountVerificationSubmissionResponse;
import com.group12.backend.entity.DiscountVerificationSubmission;
import com.group12.backend.entity.User;
import com.group12.backend.repository.DiscountVerificationSubmissionRepository;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.DiscountDocumentStorage;
import com.group12.backend.service.DiscountVerificationConstants;
import com.group12.backend.service.impl.DiscountVerificationServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("DiscountVerificationService")
class DiscountVerificationServiceTest {

    @Mock
    private DiscountVerificationSubmissionRepository submissionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DiscountDocumentStorage storage;

    @InjectMocks
    private DiscountVerificationServiceImpl service;

    @Test
    @DisplayName("submit creates pending submission with incremented version")
    void submit_createsPendingSubmission() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        DiscountVerificationSubmission latest = new DiscountVerificationSubmission();
        latest.setVersion(2);
        when(submissionRepository.findTopByUser_IdAndTypeOrderByVersionDesc(1L, DiscountVerificationConstants.TYPE_STUDENT))
                .thenReturn(Optional.of(latest));

        when(storage.store(eq(1L), eq(DiscountVerificationConstants.TYPE_STUDENT), eq(3), any()))
                .thenReturn(new DiscountDocumentStorage.StoredDiscountDocument("1/student/v3/demo.pdf", "demo.pdf", "application/pdf", 1024));

        when(submissionRepository.save(any(DiscountVerificationSubmission.class)))
                .thenAnswer(invocation -> {
                    DiscountVerificationSubmission s = invocation.getArgument(0);
                    s.setId(10L);
                    return s;
                });

        MockMultipartFile file = new MockMultipartFile("file", "demo.pdf", "application/pdf", "abc".getBytes());
        Object result = service.submit(1L, "STUDENT", file);
        assertThat(result).isInstanceOf(DiscountVerificationSubmissionResponse.class);
        DiscountVerificationSubmissionResponse response = (DiscountVerificationSubmissionResponse) result;
        assertThat(response.getStatus()).isEqualTo(DiscountVerificationConstants.STATUS_PENDING);
        assertThat(response.getVersion()).isEqualTo(3);
    }

    @Test
    @DisplayName("approve updates pending submission to approved")
    void approve_updatesStatus() {
        DiscountVerificationSubmission pending = new DiscountVerificationSubmission();
        pending.setId(20L);
        pending.setStatus(DiscountVerificationConstants.STATUS_PENDING);
        User user = new User();
        user.setId(1L);
        pending.setUser(user);
        pending.setVersion(1);
        when(submissionRepository.findById(20L)).thenReturn(Optional.of(pending));
        when(submissionRepository.save(any(DiscountVerificationSubmission.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Object result = service.approve(20L, 99L);
        DiscountVerificationSubmissionResponse response = (DiscountVerificationSubmissionResponse) result;
        assertThat(response.getStatus()).isEqualTo(DiscountVerificationConstants.STATUS_APPROVED);
        assertThat(response.getReviewerUserId()).isEqualTo(99L);
    }
}
