package vn.techmaster.nowj.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "detected_risk")
public class DetectedRiskInfo extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private ContractInfo contract;

    @Column(name = "category", length = 100, nullable = false)
    private String category;

    @Column(name = "description", length = 500, nullable = false)
    private String description;

    @Column(name = "severity", length = 50, nullable = false)
    private String severity;

    @Column(name = "relevant_context", columnDefinition = "TEXT", nullable = false)
    private String relevantContext;

    @Column(name = "explanation", columnDefinition = "TEXT", nullable = false)
    private String explanation;

    @Column(name = "suggestion", columnDefinition = "TEXT", nullable = false)
    private String suggestion;
}
