//package heading.ground.security.jwt;
//
//import heading.ground.security.user.MyUserDetails;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
////@Service
//@Slf4j
//public class JwtUtil {
//
//    private final String secret_key = "secret";
//
//    public Long extractId(String token) {
//        String s = extractClaim(token, Claims::getId);
//        return Long.parseLong(s);
//    }
//
//    public String extractUsername(String token) {
//        String username = extractClaim(token, Claims::getSubject);
//        return username;
//    }
//
//    public Date extractExpiration(String token) {
//        Date expiration = extractClaim(token, Claims::getExpiration);
//        return expiration;
//    }
//
//    private boolean isExpired(String token) {
//        boolean isEx = extractExpiration(token).before(new Date());
//        return isEx;
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    public Claims extractAllClaims(String token) {
//        return Jwts.parser().setSigningKey(secret_key).parseClaimsJws(token).getBody();
//    }
//
//    public String generateToken(MyUserDetails principal) {
//        HashMap<String, Object> claim = new HashMap<>();
//        return createToken(claim, principal.getUsername(), principal.getId());
//    }
//
//    private String createToken(Map<String, Object> claim, String subject, Long id) {
//        log.info("create Call");
//        return Jwts.builder()
//                .setClaims(claim)
//                .setId(String.valueOf(id))
//                .setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 20)))
//                .signWith(SignatureAlgorithm.HS256, secret_key).compact();
//    }
//
//    public boolean isValidated(String token, MyUserDetails principal) {
//        Long id = principal.getId();
//        String username = principal.getUsername();
//        return (extractUsername(token).equals(username) && extractId(token) == id && !isExpired(token));
//    }
//}
