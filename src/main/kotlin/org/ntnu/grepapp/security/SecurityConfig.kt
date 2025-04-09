package org.ntnu.grepapp.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.ntnu.grepapp.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.filter.OncePerRequestFilter

@Configuration
class SecurityConfig(private val userRepository: UserRepository) {

  @Bean
  fun userDetailsService(): UserDetailsService {
    return UserDetailsService { username ->
      val user = userRepository.find(username)!!

      User.withUsername(user.phone).password(user.passwordHash).build()
    }
  }

  @Bean
  fun passwordEncoder(): PasswordEncoder {
    return BCryptPasswordEncoder()
  }

  @Bean
  fun securityFilterChain(
          http: HttpSecurity,
          corsConfigurationSource: CorsConfigurationSource
  ): SecurityFilterChain {
    http
            .cors {}
            .csrf { it.disable() } // Disable CSRF for simplicity (enable in production)
            .sessionManagement {
              it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            } // No session, only JWT
            .authorizeHttpRequests {
              it.requestMatchers(
                              "/api/auth/register",
                              "/api/auth/login",
                              "/v3/api-docs",
                              "/v3/api-docs/*",
                              "/swagger-ui/*"
                      )
                      .permitAll() // Allow register & login
                      .anyRequest()
                      .authenticated() // Protect other endpoints
            }
            .addFilterBefore(
                    JWTAuthenticationFilter(),
                    UsernamePasswordAuthenticationFilter::class.java
            )
    return http.build()
  }
}

@Component
class JWTAuthenticationFilter() : OncePerRequestFilter() {

  override fun doFilterInternal(
          request: HttpServletRequest,
          response: HttpServletResponse,
          filterChain: FilterChain
  ) {
    val jwt = extractJwtFromRequest(request)
    if (jwt == null) {
      filterChain.doFilter(request, response)
      return
    }

    val userEmail = extractUserIdFromJWT(jwt)
    if (userEmail == null) {
      filterChain.doFilter(request, response)
      return
    }

    val auth =
            UsernamePasswordAuthenticationToken(
                    userEmail,
                    jwt,
                    listOf(SimpleGrantedAuthority("USER"))
            )
    SecurityContextHolder.getContext().authentication = auth
    filterChain.doFilter(request, response)
  }

  private fun extractJwtFromRequest(request: HttpServletRequest): String? {
    val bearerToken = request.getHeader("Authorization")
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7)
    }
    return null
  }

  private fun extractUserIdFromJWT(jwt: String): String? {
    try {
      val jwt = JwtUtil().extractPhone(jwt)
      return jwt
    } catch (e: Exception) {
      // TODO: Log exception
      return null
    }
  }
}
