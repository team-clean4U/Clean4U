package org.example.clean4u.orderStatusHistory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.order.OrderRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class OrderStatusHistoryController {
    private final OrderStatusHistoryService historyService;

    @GetMapping("/order-status-histories/list")
    public String orderStatusHistoryList(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @ModelAttribute OrderRequest.SearchDTO searchDTO,
            HttpServletRequest request
    ) {
        int pageIndex = Math.max(0, page - 1);
        String queryString = request.getQueryString();

        if(queryString != null) {
            queryString = queryString.replaceAll("(page=\\d+)", "");
            queryString = queryString.replaceAll("(&size=\\d+)", "");
            if(!queryString.isBlank()) {
                queryString = "&" + queryString;
            }
        }

        PageResponse<OrderStatusHistoryResponse.DetailDTO> statusHistoryListPage =
                historyService.orderStatusHistoryList(pageIndex, size, searchDTO);

        model.addAttribute("statusHistoryList", statusHistoryListPage.getContent());
        model.addAttribute("statusHistoryListPage", statusHistoryListPage);
        model.addAttribute("searchView", searchDTO);
        model.addAttribute("queryString", queryString);
        model.addAttribute("additionalCss", Arrays.asList("/css/pageLink.css", "/css/order.css", "/css/supply-item-history.css"));

        return "orderStatusHistory/list-form";
    }
}
