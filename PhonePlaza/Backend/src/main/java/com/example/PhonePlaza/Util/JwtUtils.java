package com.example.PhonePlaza.Util;

import com.example.PhonePlaza.Entity.User;
import com.example.PhonePlaza.ExceptionAndHandler.AccessDeniedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private static String secret = "ThisIsSecretxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
            "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    private static long expiryDuration = 60 * 60;

    public String generateJwt(User user) {

        long milliTime = System.currentTimeMillis();
        long expiryTime = milliTime + expiryDuration * 1000;

        Date issuedAt = new Date(milliTime);
        Date expiryAt = new Date(expiryTime);

        //claims s
        ClaimsBuilder claims = Jwts.claims()
                .setIssuer(user.getUserId().toString())
                .setIssuedAt(issuedAt)
                .setExpiration(expiryAt);

        //optional claims
        claims.add("type", user.getUserType());
        claims.add("name", user.getUserName());
        claims.add("emailId", user.getEmail());


        //generate jwt using claims
        return Jwts.builder()
                .setClaims(claims.build())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();//Compact is to  convert into String, because we can not return a jwt builder
    }

    //    Verifying the jwt token
    public Claims verify(String authorization) throws Exception {
        try {
            Claims claims = Jwts.parser().setSigningKey(secret).build().parseClaimsJws(authorization).getBody();
            return claims;
        }
        catch (Exception e) {
            throw new
                    AccessDeniedException("Access Denied");

        }
    }
}

