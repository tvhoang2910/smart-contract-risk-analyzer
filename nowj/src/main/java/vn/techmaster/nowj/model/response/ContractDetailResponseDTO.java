package vn.techmaster.nowj.model.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import vn.techmaster.nowj.model.dto.DetectedRiskDTO;

@Getter
@Setter
public class ContractDetailResponseDTO {
    private String filenameString;
    private List<DetectedRiskDTO> detectedRisks;
}
