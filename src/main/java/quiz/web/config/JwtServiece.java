package quiz.web.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
class JwtServiece {
    private static final String KEY = "AAAAB3NzaC1yc2EAAAADAQABAAAAgQCF39/p3LHf5buzA8G+Mj8l32uJADlqeTIGxE2HW500DFgP/ZT3oZODMopyrVzwoVB7PikqbgaT/sba3TdLNUQjkN5ogN3aUBIyATNDNHsQOfy60VbgHqV0HWt1b6gYD6Tf5xKiS/E6lXBYeHpSK5uCdXcm4ihyba1wqvLmLalnnQ==";

    private Key getSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(KEY);
        return Keys.hmacShaKeyFor(bytes);
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    private <T> T extractClaim(String jwt, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(jwt);
        return claimResolver.apply(claims);
    }
    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }
    public String generateJWT(UserDetails userDetails) {
        return generateJWT(new HashMap<>(), userDetails);
    }
    public boolean isTokenValid(String jwt) {
        return extractClaim(jwt, Claims::getExpiration).before(new Date());
    }
    public String generateJWT(
            Map<String, Object> claims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .compact();
    }
}
