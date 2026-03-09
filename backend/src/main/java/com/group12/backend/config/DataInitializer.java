package com.group12.backend.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.group12.backend.entity.LocationPoint;
import com.group12.backend.entity.Scooter;
import com.group12.backend.repository.LocationPointRepository;
import com.group12.backend.repository.ScooterRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final LocationPointRepository locationPointRepository;
    private final ScooterRepository scooterRepository;

    public DataInitializer(LocationPointRepository locationPointRepository,
                           ScooterRepository scooterRepository) {
        this.locationPointRepository = locationPointRepository;
        this.scooterRepository = scooterRepository;
    }

    @Override
    public void run(String... args) {
        if (scooterRepository.count() > 0) {
            return;
        }

        LocationPoint lp1 = new LocationPoint();
        lp1.setName("University of Leeds");
        lp1.setLat(53.8067);
        lp1.setLng(-1.5550);
        lp1 = locationPointRepository.save(lp1);

        LocationPoint lp2 = new LocationPoint();
        lp2.setName("Leeds City Centre");
        lp2.setLat(53.7996);
        lp2.setLng(-1.5491);
        lp2 = locationPointRepository.save(lp2);

        LocationPoint lp3 = new LocationPoint();
        lp3.setName("Leeds Train Station");
        lp3.setLat(53.7953);
        lp3.setLng(-1.5490);
        lp3 = locationPointRepository.save(lp3);

        List<Object[]> seedData = List.of(
            new Object[]{"AVAILABLE", 53.8067, -1.5550, new BigDecimal("3.50"), lp1},
            new Object[]{"AVAILABLE", 53.8071, -1.5542, new BigDecimal("3.50"), lp1},
            new Object[]{"AVAILABLE", 53.7996, -1.5491, new BigDecimal("3.00"), lp2},
            new Object[]{"RENTED",    53.7985, -1.5478, new BigDecimal("3.00"), lp2},
            new Object[]{"AVAILABLE", 53.7953, -1.5490, new BigDecimal("2.50"), lp3},
            new Object[]{"AVAILABLE", 53.7960, -1.5483, new BigDecimal("2.50"), lp3},
            new Object[]{"MAINTENANCE", 53.8010, -1.5510, new BigDecimal("3.00"), lp2},
            new Object[]{"AVAILABLE", 53.8045, -1.5530, new BigDecimal("3.50"), lp1}
        );

        for (Object[] row : seedData) {
            Scooter s = new Scooter();
            s.setStatus((String) row[0]);
            s.setLocationLat((Double) row[1]);
            s.setLocationLng((Double) row[2]);
            s.setHourRate((BigDecimal) row[3]);
            s.setLocationPoint((LocationPoint) row[4]);
            scooterRepository.save(s);
        }
    }
}
