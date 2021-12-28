package ua.kpi.iasa.sc.newsservice.security.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class TokenUtility {
    public static Algorithm getAlgo(){
        return Algorithm.HMAC256(System.getenv("SECRET_KEY").getBytes());
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
