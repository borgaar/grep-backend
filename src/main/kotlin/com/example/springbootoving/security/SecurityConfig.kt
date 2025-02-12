package com.example.springbootoving.security

import com.example.springbootoving.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig(private val userRepository: UserRepository) {

    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username ->
            val user = userRepository.findByUsername(username)
                .orElseThrow { RuntimeException("User not found") }

            User.withUsername(user.username)
                .password(user.password)
                .build()
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }  // Disable CSRF for simplicity (enable in production)
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // No session, only JWT
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()  // Allow register & login
                    .anyRequest().authenticated() // Protect other endpoints
            }
        return http.build()
    }
}
