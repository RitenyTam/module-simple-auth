package com.riteny.config.anno;

import com.riteny.simpleauth.SimpleAuthService;
import com.riteny.simpleauth.entity.SimpleAuthUser;
import com.riteny.simpleauth.exception.AuthException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * @descr:
 * @author: Gavin
 * @DATE: 2020/12/29 11:09
 */
public class SimpleAuthArgumentTypeResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private SimpleAuthService simpleAuthService;

    public SimpleAuthArgumentTypeResolver(SimpleAuthService simpleAuthService) {
        this.simpleAuthService = simpleAuthService;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(SimpleAuthResolverAnno.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);

        String tokenStr = request.getHeader("Authorization");
        if (StringUtils.isEmpty(tokenStr)) {
            throw new AuthException("Authorization header is required .");
        }

        String token = tokenStr.split(" ")[1];
        if (StringUtils.isEmpty(token)) {
            throw new AuthException("Token is required .");
        }

        SimpleAuthUser simpleAuthUser = simpleAuthService.readToken(token);

        if (simpleAuthUser == null) {
            throw new AuthException("Invalid token.");
        }

        return simpleAuthUser;
    }
}
