package com.example.springbootoving.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import java.util.Base64

@Component
class JwtUtil {

    private val secretKey = Keys.hmacShaKeyFor(Base64.getEncoder().encode("superduperhemmeligenøkkelsomingenfinnerutavuansetthva".toByteArray()))


    fun generateToken(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 5))
            .signWith(secretKey)
            .compact()
    }

    fun extractUsername(token: String): String {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
            .parseClaimsJws(token)
            .body.subject
    }
}