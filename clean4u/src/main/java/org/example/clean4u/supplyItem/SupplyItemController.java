package org.example.clean4u.supplyItem;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.employee.Employee;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SupplyItemController {

    private final SupplyItemService service;

    // http://localhost:8080/supply-item/list
    @GetMapping("/supply-item/list")
    public String supplyItemList(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean lowStock,
            HttpSession session,
            HttpServletRequest request
    ) {
        int pageIndex = Math.max(0, page - 1);

        String queryString = request.getQueryString();

        if (queryString != null) {
            queryString = queryString.replaceAll("(page=\\d+)", "");
            queryString = queryString.replaceAll("(&size=\\d+)", "");
            if (!queryString.isBlank()) {
                queryString = "&" + queryString;
            }
        }

        PageResponse<SupplyItemResponse.ListDTO> supplyItemListPage = service.supplyItemList(pageIndex, size, lowStock, name);
        model.addAttribute("supplyItemList", supplyItemListPage.getContent());
        model.addAttribute("supplyItemPage", supplyItemListPage);
        model.addAttribute("name", name == null ? "" : name);
        model.addAttribute("lowStock", lowStock);
        model.addAttribute("lowStockTrue", Boolean.TRUE.equals(lowStock));
        model.addAttribute("lowStockFalse", Boolean.FALSE.equals(lowStock));
        model.addAttribute("queryString", queryString);
        return "supplyItem/list-form";
    }

    // http://localhost:8080/supply-item/{supplyItemId}
    @GetMapping("/supply-item/{supplyItemId}")
    public String detail(@PathVariable Long supplyItemId, Model model) {
        SupplyItemResponse.DetailDTO supplyItem = service.getDetail(supplyItemId);
        model.addAttribute("supplyItem", supplyItem);

        return "supplyItem/detail-form";
    }

    // http://localhost:8080/supply-item/save
    @GetMapping("/supply-item/save")
    public String saveForm() {
        return "supplyItem/save-form";
    }

    // http://localhost:8080/supply-item/save
    @PostMapping("/supply-item/save")
    public String saveProc(@Valid SupplyItemRequest.SaveDTO saveDTO, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        service.save(saveDTO, sessionUser);

        return "redirect:/supply-item/list";
    }

    // http://localhost:8080/supply-item/{supplyItemId}/update
    @GetMapping("/supply-item/{supplyItemId}/update")
    public String updateForm(@PathVariable Long supplyItemId, Model model) {
        SupplyItemResponse.UpdateFormDTO supplyItem = service.getFormForUpdate(supplyItemId);
        model.addAttribute("supplyItem", supplyItem);
        return "supplyItem/update-form";
    }

    // http://localhost:8080/supply-item/{supplyItemId}/update
    @PostMapping("/supply-item/{supplyItemId}/update")
    public String updateProc(@PathVariable Long supplyItemId, @Valid SupplyItemRequest.UpdateDTO updateDTO, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        service.update(supplyItemId, updateDTO, sessionUser);

        return "redirect:/supply-item/" + supplyItemId;
    }

    // http://localhost:8080/supply-item/{supplyItemId}/delete
    @PostMapping("/supply-item/{supplyItemId}/deactivate")
    public String deactivate(@PathVariable Long supplyItemId) {
        service.deactivate(supplyItemId);
        return "redirect:/supply-item/" + supplyItemId;
    }

    // http://localhost:8080/supply-item/{supplyItemId}/delete
    @PostMapping("/supply-item/{supplyItemId}/activate")
    public String activate(@PathVariable Long supplyItemId) {
        service.activate(supplyItemId);
        return "redirect:/supply-item/" + supplyItemId;
    }
}
