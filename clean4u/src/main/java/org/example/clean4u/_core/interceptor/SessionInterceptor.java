package org.example.clean4u._core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.supplyItem.SupplyItemService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class SessionInterceptor implements HandlerInterceptor {
    private final SupplyItemService supplyItemService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            HttpSession session = request.getSession(false);

            if (session != null) {
                Employee sessionUser = (Employee) session.getAttribute("sessionUser");
                modelAndView.addObject("sessionUser", sessionUser);
                
                if (sessionUser != null) {
                    try {
                        var lowStockItems = supplyItemService.getLowStockItems();
                        modelAndView.addObject("lowStockItems", lowStockItems);
                        modelAndView.addObject("lowStockCount", lowStockItems.size());
                    } catch (Exception e) {
                        modelAndView.addObject("lowStockItems", Collections.emptyList());
                        modelAndView.addObject("lowStockCount", 0);
                    }
                }
            }
        }
    }
}
