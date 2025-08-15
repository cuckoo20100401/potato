package org.potato.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.potato.util.Result;
import org.potato.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class TokenUtils {

	public static String secret = "https://www.debian.org/";
	public static long token_expired_minutes = 600;
	public static long refresh_token_expired_minutes = token_expired_minutes*2;
	public static final Algorithm algorithm = Algorithm.HMAC256(secret);

	public static String createToken(SecurityUser securityUser) {
    	
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("owner", "UniversalSecurity");
        
        String token = JWT.create()
        		.withHeader(headerClaims)
        		.withIssuer("potato")
        		.withIssuedAt(Instant.now())
				.withExpiresAt(Instant.now().plus(token_expired_minutes, ChronoUnit.MINUTES))
        		.withClaim("id", securityUser.getId())
        		.withClaim("username", securityUser.getUsername())
				.withClaim("nickname", securityUser.getNickname())
				.withClaim("clientType", securityUser.getClientType())
				.withClaim("roles", securityUser.getRoles())
				.withClaim("perms", securityUser.getPerms())
				.withClaim("extra", securityUser.getExtra())
        		.sign(algorithm);
        return token;
    }

	public static String createRefreshToken(SecurityUser securityUser) {
    	
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("owner", "UniversalSecurity");
        
        String token = JWT.create()
        		.withHeader(headerClaims)
        		.withIssuer("potato")
        		.withIssuedAt(Instant.now())
        		.withExpiresAt(Instant.now().plus(refresh_token_expired_minutes, ChronoUnit.MINUTES))
				.withClaim("id", securityUser.getId())
				.withClaim("username", securityUser.getUsername())
				.withClaim("clientType", securityUser.getClientType())
        		.sign(algorithm);
        return token;
    }

	public static Result verify(String token) {

		if (StringUtils.isNullOrEmpty(token)) {
			return Result.failure().code(-1).message("token is required");
		}

    	try {
    		JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT jwt = verifier.verify(token);
			Map<String, Claim> claims = jwt.getClaims();
			return Result.success().addPayload("claims", claims);
		} catch (TokenExpiredException e) {
			return Result.failure().code(-2).message("token is expired");
		} catch (JWTVerificationException e) {
			return Result.failure().code(-3).message("token is invalid");
		}
    }
}