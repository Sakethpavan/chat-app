package com.example.demo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.ZonedDateTime;
import java.util.UUID;

@Document(collection = "users")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @Field(targetType = FieldType.STRING)
    private UUID userId;

    @NotBlank
    @Size(max = 20)
    @Indexed(unique = true)
    private String username;

    @NotBlank
    @Size(max = 30)
    @Email
    private String email;


    @Size(min = 8, max = 128)
    private String password;

    @DBRef
    private Role role;
    private boolean accountNonLocked = true;
    private boolean accountNonExpired = true;
    private boolean credentialNonExpired = true;
    private boolean enabled = true;
    private ZonedDateTime credentialsExpiryDate;
    private ZonedDateTime accountExpiryDate;
    private String twoFactorSecret;
    private boolean isTwoFactorEnabled = false;
    private String signUpMethod; // Tracks how the user signed up (OAuth2, manual, etc.)

    // Fields for OAuth2 Login
    private String oauthProvider;  // e.g., "google", "facebook"
    private String oauthProviderUserId;  // The unique user ID from OAuth provider

    // Optional: Store OAuth tokens if needed (for further OAuth interaction)
    private String oauthAccessToken;  // OAuth access token, optional
    private String oauthRefreshToken;  // OAuth refresh token, optional

    @CreatedDate
    private ZonedDateTime createdAt;

    @LastModifiedDate
    private ZonedDateTime lastUpdatedAt;

    @LastModifiedBy
    private ZonedDateTime lastModifiedBy;

}
