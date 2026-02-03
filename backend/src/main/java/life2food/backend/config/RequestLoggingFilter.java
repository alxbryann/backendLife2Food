package life2food.backend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Logea Origin y respuesta para depurar 403 Access Denied (CORS).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String origin = req.getHeader("Origin");
        String method = req.getMethod();
        String uri = req.getRequestURI();

        if (uri.startsWith("/api/")) {
            log.info("[CORS DEBUG] {} {} | Origin: {}", method, uri, origin != null ? origin : "(no Origin header)");
        }

        chain.doFilter(request, response);

        if (uri.startsWith("/api/") && res.getStatus() >= 400) {
            log.warn("[CORS DEBUG] {} {} -> {} | Origin was: {}", method, uri, res.getStatus(), origin);
        }
    }
}
