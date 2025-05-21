package vn.techmaster.nowj.service.impl;

import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import vn.techmaster.nowj.entity.ContractInfo;
import vn.techmaster.nowj.entity.DetectedRiskInfo;
import vn.techmaster.nowj.entity.UserInfo;
import vn.techmaster.nowj.error.AppException;
import vn.techmaster.nowj.error.BadRequestException;
import vn.techmaster.nowj.error.ResourceNotFoundException;
import vn.techmaster.nowj.model.dto.DetectedRiskDTO;
import vn.techmaster.nowj.repository.ContractInfoRepository;
import vn.techmaster.nowj.repository.DetectedRiskRepository;
import vn.techmaster.nowj.repository.UserRepository;
import vn.techmaster.nowj.utils.AIDetectRisk;
import vn.techmaster.nowj.utils.SmartTextExtractor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractInfoServiceImplTest {
    @Mock ContractInfoRepository contractInfoRepository;
    @Mock DetectedRiskRepository detectedRiskRepository;
    @Mock Tika tika;
    @Mock ModelMapper modelMapper;
    @Mock AIDetectRisk aiDetectRisk;
    @Mock SmartTextExtractor smartTextExtractor;
    @Mock UserRepository userRepository;
    @Mock MultipartFile multipartFile;
    @Mock Authentication authentication;

    @InjectMocks ContractInfoServiceImpl contractInfoService;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void saveContractFile_success() throws Exception {
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(tika.parseToString(any(java.io.InputStream.class))).thenReturn("contract text");
        when(multipartFile.getOriginalFilename()).thenReturn("file.pdf");
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getSize()).thenReturn(123L);
        UserInfo user = new UserInfo(); user.setEmail("test@a.com");
        when(authentication.getName()).thenReturn("test@a.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(contractInfoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(aiDetectRisk.analyzeContractRisks(anyString())).thenReturn(Collections.emptyList());
        when(detectedRiskRepository.saveAll(any())).thenReturn(Collections.emptyList());

        ContractInfo result = contractInfoService.saveContractFile(multipartFile);
        assertNotNull(result);
        assertEquals("file.pdf", result.getFilename());
        assertEquals("contract text", result.getExtractedText());
        assertEquals(user, result.getUser());
    }

    @Test
    void saveContractFile_fail_throwsAppException() throws Exception {
        when(multipartFile.getInputStream()).thenThrow(new IOException("IO error"));
        assertThrows(AppException.class, () -> contractInfoService.saveContractFile(multipartFile));
    }

    @Test
    void getContractDetail_success() {
        DetectedRiskInfo riskInfo = new DetectedRiskInfo();
        DetectedRiskDTO dto = new DetectedRiskDTO();
        when(detectedRiskRepository.findAllByContract_Id(1L)).thenReturn(List.of(riskInfo));
        when(modelMapper.map(riskInfo, DetectedRiskDTO.class)).thenReturn(dto);
        List<DetectedRiskDTO> result = contractInfoService.getContractDetail(1L);
        assertEquals(1, result.size());
        assertSame(dto, result.get(0));
    }

    @Test
    void getContractDetail_nullId_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> contractInfoService.getContractDetail(null));
    }

    @Test
    void deleteContract_success() {
        ContractInfo contract = new ContractInfo();
        when(contractInfoRepository.findById(1L)).thenReturn(Optional.of(contract));
        doNothing().when(detectedRiskRepository).deleteAllByContract_Id(1L);
        doNothing().when(contractInfoRepository).delete(contract);
        assertDoesNotThrow(() -> contractInfoService.deleteContract(1L));
    }

    @Test
    void deleteContract_notFound_throwsResourceNotFound() {
        when(contractInfoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> contractInfoService.deleteContract(1L));
    }

    @Test
    void saveContractImage_success() throws Exception {
        when(smartTextExtractor.getTextFromImage(multipartFile)).thenReturn("image text");
        when(multipartFile.getOriginalFilename()).thenReturn("img.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getSize()).thenReturn(456L);
        UserInfo user = new UserInfo(); user.setEmail("img@a.com");
        when(authentication.getName()).thenReturn("img@a.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(contractInfoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(aiDetectRisk.analyzeContractRisks(anyString())).thenReturn(Collections.emptyList());
        when(detectedRiskRepository.saveAll(any())).thenReturn(Collections.emptyList());

        ContractInfo result = contractInfoService.saveContractImage(multipartFile);
        assertNotNull(result);
        assertEquals("img.jpg", result.getFilename());
        assertEquals("image text", result.getExtractedText());
        assertEquals(user, result.getUser());
    }

    @Test
    void saveContractImage_fail_throwsAppException() throws Exception {
        when(smartTextExtractor.getTextFromImage(multipartFile)).thenThrow(new RuntimeException("OCR error"));
        assertThrows(AppException.class, () -> contractInfoService.saveContractImage(multipartFile));
    }

    @Test
    void getAllContracts_success() {
        ContractInfo contract = new ContractInfo();
        when(contractInfoRepository.findAll()).thenReturn(List.of(contract));
        List<ContractInfo> result = contractInfoService.getAllContracts();
        assertEquals(1, result.size());
        assertSame(contract, result.get(0));
    }

    @Test
    void getAllContracts_empty_throwsResourceNotFound() {
        when(contractInfoRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> contractInfoService.getAllContracts());
    }
}
