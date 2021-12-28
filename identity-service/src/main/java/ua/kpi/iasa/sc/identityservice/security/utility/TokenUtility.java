package ua.kpi.iasa.sc.identityservice.security.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import ua.kpi.iasa.sc.identityservice.repository.model.Role;

import java.util.*;
import java.util.stream.Collectors;

public final class TokenUtility {
    private final String email;
    private final long id;
    private final List<String> roles;

    public TokenUtility(String email, long id, List<String> roles) {
        this.email = email;
        this.id = id;
        this.roles = roles;
    }

    public static Algorithm getAlgo(){
        return Algorithm.HMAC256(System.getenv("SECRET_KEY").getBytes());
    }

    public Map<String, String> generateTokens(String issuer){
        Algorithm algo = getAlgo();
        String access_token = JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + Integer.parseInt(System.getenv("ACCESS_TOKEN_LIFE_millis"))))
                .withIssuer(issuer)
                .withClaim("roles", roles)
                .withClaim("id", id)
                .sign(algo);
        String refresh_token = JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + Integer.parseInt(System.getenv("REFRESH_TOKEN_LIFE_millis"))))
                .withIssuer(issuer)
                .sign(algo);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        return tokens;
    }

    public Map<String, String> generateAccessToken(String issuer){
        Algorithm algo = getAlgo();
        String access_token = JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + Integer.parseInt(System.getenv("ACCESS_TOKEN_LIFE_millis"))))
                .withIssuer(issuer)
                .withClaim("roles", roles)
                .withClaim("id", id)
                .sign(algo);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        return tokens;
    }

    public static DecodedJWT verifyToken(String bearerToken) throws RuntimeException{
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            String token = bearerToken.substring("Bearer ".length());
            Algorithm algo = TokenUtility.getAlgo();
            JWTVerifier verifier = JWT.require(algo).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT;
        }
        else{
            throw new RuntimeException("Bearer token isn't in appropriate format!");
        }
    }
}
