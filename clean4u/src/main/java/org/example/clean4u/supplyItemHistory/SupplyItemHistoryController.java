package org.example.clean4u.supplyItemHistory;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.supplyItem.SupplyItemService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class SupplyItemHistoryController {
    private final SupplyItemHistoryService service;
    private final SupplyItemService supplyItemService;
    private final ObjectMapper objectMapper;

    // http://localhost:8080/supply-item-history/list
    @GetMapping("/supply-item-history/list")
    public String supplyItemHistoryList(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) Type type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String itemName,
            HttpSession session,
            HttpServletRequest request
    ) {
        int pageIndex = Math.max(0, page - 1);

        String queryString = request.getQueryString();

        if (queryString != null) {
            queryString = queryString.replaceAll("(&page=\\d+)", "");
            queryString = queryString.replaceAll("(&size=\\d+)", "");
            if (!queryString.isBlank()) {
                queryString = "&" + queryString;
            }
        }

        PageResponse<SupplyItemHistoryResponse.ListDTO> supplyItemHistoryListPage = service.supplyItemHistoryList(pageIndex, size, type, fromDate, toDate);
        model.addAttribute("supplyItemHistoryList", supplyItemHistoryListPage.getContent());
        model.addAttribute("supplyItemHistoryPage", supplyItemHistoryListPage);
        model.addAttribute("queryString", queryString);
        model.addAttribute("type", type != null ? type.name() : null);
        model.addAttribute("typeDescription", type != null ? type.getDescription() : null);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("itemName", itemName != null ? itemName : "");
        return "supplyItemHistory/list-form";
    }

    // http://localhost:8080/supply-item-history/{historyId}
    @GetMapping("/supply-item-history/{historyId}")
    public String detail(@PathVariable Long historyId, Model model) {
        SupplyItemHistoryResponse.GroupDetailDTO supplyItemHistory = service.getDetail(historyId);
        model.addAttribute("supplyItemHistory", supplyItemHistory);
        return "supplyItemHistory/detail-form";
    }

    // http://localhost:8080/supply-item-history/save
    @GetMapping("/supply-item-history/save")
    public String saveForm(Model model) {
        model.addAttribute("supplyItems", supplyItemService.getAllSupplyItems());
        try {
            String supplyItemsJson = objectMapper.writeValueAsString(supplyItemService.getAllSupplyItems());
            model.addAttribute("supplyItemsJson", supplyItemsJson);
        } catch (Exception e) {
            model.addAttribute("supplyItemsJson", "[]");
        }
        return "supplyItemHistory/save-form";
    }

    // http://localhost:8080/supply-item-history/save
    @PostMapping("/supply-item-history/save")
    public String saveProc(@Valid SupplyItemHistoryRequest.SaveDTO saveDTO, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        service.save(saveDTO, sessionUser);
        return "redirect:/supply-item-history/list";
    }

}
