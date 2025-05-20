package vn.techmaster.nowj.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "provider")
    /**
     * Tên provider xác thực (ví dụ: 'google', 'facebook', 'github', 'local')
     */
    private String provider;

    @Column(name = "provider_id")
    /**
     * ID từ provider (Google ID, Facebook ID, v.v.). Nếu là local thì có thể null.
     */
    private String providerId;

    @Column(name = "role")
    private String role = "USER";

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ContractInfo> contracts = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastLogin = LocalDateTime.now();
    }
}
