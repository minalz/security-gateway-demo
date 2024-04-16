package cn.minalz.filter;

import cn.minalz.model.AjaxResult;
import cn.minalz.model.HttpStatus;
import cn.minalz.service.TokenService;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private TokenService tokenService;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        System.out.println("===" + path);

        //登录和注册请求直接放行
        if (antPathMatcher.match("/login", path) ||
                antPathMatcher.match("/register", path)) {
            return chain.filter(exchange);
        }

        //api接口，异步请求，校验用户必须登录
        if (antPathMatcher.match("/api/**", path)) {
            String userName = this.getUserName(request);
            System.out.println("获取登录用户信息:" + userName);
            if (StringUtils.isEmpty(userName)) {
                ServerHttpResponse response = exchange.getResponse();
                return out(response, HttpStatus.UNAUTHORIZED);
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * api接口鉴权失败返回数据
     *
     * @param response
     * @return
     */
    private Mono<Void> out(ServerHttpResponse response, int httpStatus) {
        AjaxResult ajaxResult = AjaxResult.success("授权失败咯");
        byte[] bits = JSONObject.toJSONString(ajaxResult).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 获取当前登录用户信息
     *
     * @param request
     * @return
     */
    private String getUserName(ServerHttpRequest request) {
        String token = "";
        List<String> tokenList = request.getHeaders().get("token");
        if (null != tokenList) {
            token = tokenList.get(0);
        }
        System.out.println("获取到的token:" + token);
        if (!StringUtils.isEmpty(token)) {
            // 通过获取到的token获取用户名并检查redis中是否保留登录信息
            // TODO
            // 如果redis中不存在用户登录信息则直接返回null
            return tokenService.getUsernameFromToken(token);
        }
        return null;
    }
}
