package com.trading.security;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.trading.constants.JwtConstant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenValidator extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String jwt = request.getHeader(JwtConstant.JWT_HEADER);
		if (null != jwt) {
			jwt = jwt.substring(7);

			try {
				SecretKey secretKey = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
				System.out.println("JwtTokenValidator||secretKey:" + secretKey);
				
				Claims claims = Jwts.parserBuilder()
								.setSigningKey(secretKey)
								.build()
								.parseClaimsJws(jwt)
								.getBody();
				System.out.println("JwtTokenValidator||claims:" + claims);

				String email = String.valueOf(claims.get("email"));
				System.out.println("JwtTokenValidator||email:" + email);

				String authorities = String.valueOf(claims.get("authorities"));
				System.out.println("JwtTokenValidator||authorities:" + authorities);
				
				List<GrantedAuthority> authoritiesList = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
				System.out.println("JwtTokenValidator||authoritiesList:" + authoritiesList);
				
				Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authoritiesList);
				System.out.println("JwtTokenValidator||authentication:" + authentication);
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
			} catch (Exception e) {
				throw new BadCredentialsException("Invalid Token...");
			}
		}
		
		filterChain.doFilter(request, response);
	}

}
