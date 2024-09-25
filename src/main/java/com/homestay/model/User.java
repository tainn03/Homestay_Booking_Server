package com.homestay.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String email;
    String password;
    String fullName;
    String status;
    String phone;
    LocalDate dob;
    String gender;
    String address;
    String cccd;
    String businessLicense;
    String nationality;
    String bankName;
    String bankNum;
    String bankUsername;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Image avatar;

    @ManyToOne
    @JoinColumn(name = "role", nullable = false)
    Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Homestay> homestays;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Booking> bookings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Token> tokens; // Một user có thể có nhiều token

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
