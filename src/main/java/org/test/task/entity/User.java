package org.test.task.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.core.userdetails.User.builder;

@Entity
@Table(name = "users")
@Data
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean active = true;

    @CreatedDate
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<BankCard> cards;

    public UserDetails toUserDetails() {
        return builder()
                .username(this.username)
                .password(this.password)
                .roles(this.role.name().replace("ROLE_", ""))
                .build();
    }
}
