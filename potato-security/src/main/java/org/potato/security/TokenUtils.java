package org.potato.security;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.potato.util.Result;
import org.potato.util.StringUtils;

public class TokenUtils {

	public static String secret = "https://www.debian.org/";
	public static long token_expired_minutes = 600;
	public static long refresh_token_expired_minutes = token_expired_minutes*2;
	public static final Algorithm algorithm = Algorithm.HMAC256(secret);


	/**
	 * 创建Token
	 *
	 * <p>
	 *     可以将AuthUser中的所有信息（除过accessToken）放入token中，而不必关心这些属性中是否有值，是否有值取决于AuthenticationService.findAuthUserByUsername(String username)的实现。
	 *     这样做的好处就是token中存放了丰富的信息，而不需要通过用户ID再去Redis中取用户信息了
	 * </p>
	 * @param user
	 * @return
	 */
	public static String createToken(AuthUser user) {
    	
    	ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).plusMinutes(token_expired_minutes);
		long since1970Seconds = zonedDateTime.toEpochSecond();
		Date expiredDate = new Date(since1970Seconds*1000);
    	
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("owner", "UniversalSecurity");
        
        String token = JWT.create()
        		.withHeader(headerClaims)
        		.withIssuer("auth0")
        		.withIssuedAt(new Date())
        		.withExpiresAt(expiredDate)
        		.withClaim("user.id", user.getId())
        		.withClaim("user.username", user.getUsername())
				.withClaim("user.nickname", user.getNickname())
				.withClaim("user.cellphone", user.getCellphone())
				.withClaim("user.telephone", user.getTelephone())
				.withClaim("user.email", user.getEmail())
				.withClaim("user.companyId", user.getCompanyId())
				.withArrayClaim("user.manageGroupIds", user.getManageGroupIds())
        		.withArrayClaim("user.roles", user.getRoles())
        		.withArrayClaim("user.perms", user.getPerms())
				.withClaim("user.clientType", user.getClientType())
        		.sign(algorithm);
        return token;
    }
	
	/**
	 * 创建refresh_token
	 *
	 * @param user
	 * @return
	 */
	public static String createRefreshToken(AuthUser user) {
    	
    	ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).plusMinutes(refresh_token_expired_minutes);
		long since1970Seconds = zonedDateTime.toEpochSecond();
		Date expiredDate = new Date(since1970Seconds*1000);
    	
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("owner", "UniversalSecurity");
        
        String token = JWT.create()
        		.withHeader(headerClaims)
        		.withIssuer("auth0")
        		.withIssuedAt(new Date())
        		.withExpiresAt(expiredDate)
        		.withClaim("user.id", user.getId())
				.withClaim("user.username", user.getUsername())
        		.sign(algorithm);
        return token;
    }
	
	/**
	 * 检验token
	 *
	 * @param token
	 * @return
	 */
	public static Result verify(String token) {

		if (StringUtils.isNullOrEmpty(token)) {
			return Result.failure().code(-1).message("token is not exist");
		}

    	try {
    		JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT jwt = verifier.verify(token);
			Map<String, Claim> claims = jwt.getClaims();
			return Result.success().addPayload("claims", claims);
		} catch (TokenExpiredException e) {
			return Result.failure().code(-2).message("token is expired");
		} catch (SignatureVerificationException e) {
			return Result.failure().code(-3).message("token签名校验失败，比如secret有误");
		} catch (JWTVerificationException e) {
			return Result.failure().code(-4).message("其它校验异常");
		}
    }
}