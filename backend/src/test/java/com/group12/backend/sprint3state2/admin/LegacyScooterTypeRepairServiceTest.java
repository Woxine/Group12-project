package com.group12.backend.sprint3state2.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group12.backend.entity.Scooter;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.service.LegacyScooterTypeRepairService;

@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyScooterTypeRepairService")
class LegacyScooterTypeRepairServiceTest {

    @Mock
    private ScooterRepository scooterRepository;

    @InjectMocks
    private LegacyScooterTypeRepairService repairService;

    @Test
    @DisplayName("repairLegacyGen1Types_repairsOnlyLegacyIdCandidates")
    @SuppressWarnings("unchecked")
    void repairLegacyGen1Types_repairsOnlyLegacyIdCandidates() {
        Scooter missingType = scooter(1L, null);
        Scooter blankType = scooter(2L, "   ");
        Scooter legacyAlias = scooter(3L, "Fz3");
        Scooter legacyGenVariant = scooter(4L, "gen 1");
        Scooter alreadyCanonical = scooter(5L, "GEN1");
        Scooter otherGeneration = scooter(6L, "GEN2");
        Scooter outsideLegacyRange = scooter(13L, null);

        when(scooterRepository.findAllById(anyList())).thenReturn(List.of(
                missingType,
                blankType,
                legacyAlias,
                legacyGenVariant,
                alreadyCanonical,
                otherGeneration,
                outsideLegacyRange));

        repairService.repairLegacyGen1Types();

        ArgumentCaptor<List<Scooter>> savedCaptor = ArgumentCaptor.forClass(List.class);
        verify(scooterRepository).saveAll(savedCaptor.capture());

        List<Scooter> saved = savedCaptor.getValue();
        assertThat(saved).extracting(Scooter::getId).containsExactly(1L, 2L, 3L, 4L);
        assertThat(saved).extracting(Scooter::getType).containsOnly("GEN1");

        assertThat(alreadyCanonical.getType()).isEqualTo("GEN1");
        assertThat(otherGeneration.getType()).isEqualTo("GEN2");
        assertThat(outsideLegacyRange.getType()).isNull();
    }

    @Test
    @DisplayName("repairLegacyGen1Types_skipsSaveWhenNoRepairNeeded")
    void repairLegacyGen1Types_skipsSaveWhenNoRepairNeeded() {
        when(scooterRepository.findAllById(anyList())).thenReturn(List.of(
                scooter(1L, "GEN1"),
                scooter(2L, "GEN2"),
                scooter(3L, "GEN3")));

        repairService.repairLegacyGen1Types();

        verify(scooterRepository, never()).saveAll(anyList());
    }

    private static Scooter scooter(Long id, String type) {
        Scooter scooter = new Scooter();
        scooter.setId(id);
        scooter.setType(type);
        return scooter;
    }
}
