package com.example.demo.config;

import com.example.demo.model.AppUserDetails;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import com.example.demo.util.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final AuthService authService;
    private final JwtUtils jwtUtils;
    @Value("${frontend-app.redirect-url}")
    private String frontendRedirectUrl;
    private final String GOOGLE_REGISTRATION_ID = "google";


    private String username;
    private String idAttributeKey;

    public OAuth2LoginSuccessHandler(AuthService authService,JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    private final Logger logger = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String clientRegistrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        if(clientRegistrationId.equalsIgnoreCase(GOOGLE_REGISTRATION_ID)) {
            DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oauth2User.getAttributes();
            String email = attributes.getOrDefault("email", "").toString();
            this.username = attributes.getOrDefault("name", "").toString();
            this.idAttributeKey = "sub";


            authService.findByEmail(email)
                    .ifPresentOrElse(user -> {
                        Authentication securityAuth = getAuthentication(attributes, oAuth2AuthenticationToken);
                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
                    }, () -> {
                        User newUser = User.builder()
                                .email(email)
                                .username(username)
                                .signUpMethod(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())
                                .build();
                        authService.registerUser(newUser);
                        Authentication securityAuth = getAuthentication(attributes, oAuth2AuthenticationToken);
                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
                    });

        }
        this.setAlwaysUseDefaultTargetUrl(true);

        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = (String) attributes.get("email");
        logger.debug("username: {} email : {}", username, email);

        Set<SimpleGrantedAuthority> authorities = oauth2User.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toSet());
        User user = authService.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found"));

        authorities.add(new SimpleGrantedAuthority("DEV")); // TO BE DONE

        AppUserDetails userDetails = AppUserDetails.builder()
                .username(user.getUsername())
                .authorities(authorities)
                .is2faEnabled(false)
                .build();

        String jwtToken = jwtUtils.generateTokenFromUserDetails(userDetails);

        // Redirect to the frontend with the JWT token
        String targetUrl = UriComponentsBuilder.fromUriString(frontendRedirectUrl)
                .queryParam("token", jwtToken)
                .build().toUriString();
        this.setDefaultTargetUrl(targetUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private Authentication getAuthentication(Map<String, Object> attributes, OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("DEV")), // TO BE DONE
                attributes,
                idAttributeKey
        );
        return new OAuth2AuthenticationToken(
                oauthUser,
                List.of(new SimpleGrantedAuthority("DEV")), // TO BE DONE
                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
        );
    }


}
