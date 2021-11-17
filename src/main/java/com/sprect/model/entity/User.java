package com.sprect.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprect.model.Role;
import com.sprect.model.StatusUser;
import com.sprect.utils.DefaultString;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idUser;

    @Column(nullable = false, unique = true)
    @Length(min = 3, max = 24, message = DefaultString.FAILED_VALIDATE_USERNAME)
    private String username;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = DefaultString.PATTERN_EMAIL,
            message = DefaultString.FAILED_VALIDATE_EMAIL)
    private String email;

    @Column(nullable = false)
    @Pattern(regexp = DefaultString.PATTERN_FIRST_NAME,
            message = DefaultString.FAILED_VALIDATE_FIRST_NAME)
    private String firstName;

    @Column(nullable = false)
    @Pattern(regexp = DefaultString.PATTERN_LAST_NAME,
            message = DefaultString.FAILED_VALIDATE_LAST_NAME)
    private String lastName;

    @Column(nullable = false)
    @Pattern(regexp = DefaultString.PATTERN_PASSWORD,
            message = DefaultString.FAILED_VALIDATE_PASSWORD)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @CreationTimestamp
    private LocalDate registrationDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Transient
    private URL urlAvatar;

    @Column(columnDefinition = "boolean default false")
    @JsonIgnore
    private boolean avatar;

    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private StatusUser status;

    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private Role role;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(getRole().toString()));
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return getStatus() != StatusUser.BLOCKED;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return getStatus() != StatusUser.NOT_ACTIVE;
    }
}
