package com.example.springbootoving.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import java.util.Base64

@Component
class JwtUtil {

    private val secretKey = Keys.hmacShaKeyFor(Base64.getEncoder().encode("d0711dd32236d7b6b0bd7e987df696bc06256358b4eb3736f11575ca5f4f704ed7eb40bbce20e0c2f2efc23ebdc00893e6fc770fb3c176d719bcc7b2ad3aab92".toByteArray()))


    fun generateToken(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(secretKey)
            .compact()
    }

    fun extractUsername(token: String): String {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
            .parseClaimsJws(token)
            .body.subject
    }

}