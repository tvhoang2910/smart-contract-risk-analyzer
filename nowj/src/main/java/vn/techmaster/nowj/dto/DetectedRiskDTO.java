package vn.techmaster.nowj.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetectedRiskDTO {

    private String category;

    private String description;

    private String severity;

    private String relevantContext;

    private String explanation;

    private String suggestion;

}
    