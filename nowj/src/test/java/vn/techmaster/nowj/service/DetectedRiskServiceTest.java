package vn.techmaster.nowj.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DetectedRiskServiceTest {
    @Mock
    DetectedRiskService detectedRiskService;

    @Test
    void detectedRiskService_exists() {
        assertNotNull(detectedRiskService);
    }
}
