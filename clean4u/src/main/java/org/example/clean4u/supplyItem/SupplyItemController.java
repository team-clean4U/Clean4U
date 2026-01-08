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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class SupplyItemController {

    private final SupplyItemService service;

    // http://localhost:8080/supply-items/list
    @GetMapping("/supply-items/list")
    public String supplyItemList(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean lowStock,
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
        model.addAttribute("additionalCss", Arrays.asList("/css/pageLink.css", "/css/supply-item.css"));
        return "supplyItem/list-form";
    }

    // http://localhost:8080/supply-items/{supplyItemId}
    @GetMapping("/supply-items/{supplyItemId}")
    public String detail(@PathVariable Long supplyItemId, Model model) {
        SupplyItemResponse.DetailDTO supplyItem = service.getDetail(supplyItemId);
        model.addAttribute("supplyItem", supplyItem);
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/supply-item.css"));

        return "supplyItem/detail-form";
    }

    // http://localhost:8080/supply-items/save
    @GetMapping("/supply-items/save")
    public String saveForm(Model model) {
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/supply-item.css", "/css/supply-item-history.css", "/css/order.css"));
        return "supplyItem/save-form";
    }

    // http://localhost:8080/supply-items/save
    @PostMapping("/supply-items/save")
    public String saveProc(@Valid SupplyItemRequest.SaveDTO saveDTO, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        service.save(saveDTO, sessionUser);

        return "redirect:/supply-items/list";
    }

    // http://localhost:8080/supply-items/{supplyItemId}/update
    @GetMapping("/supply-items/{supplyItemId}/update")
    public String updateForm(@PathVariable Long supplyItemId, Model model) {
        SupplyItemResponse.UpdateFormDTO supplyItem = service.getFormForUpdate(supplyItemId);
        model.addAttribute("supplyItem", supplyItem);
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/supply-item.css", "/css/supply-item-history.css", "/css/order.css"));
        return "supplyItem/update-form";
    }

    // http://localhost:8080/supply-items/{supplyItemId}/update
    @PutMapping("/supply-items/{supplyItemId}/update")
    public String updateProc(@PathVariable Long supplyItemId, @Valid SupplyItemRequest.UpdateDTO updateDTO, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        service.update(supplyItemId, updateDTO, sessionUser);

        return "redirect:/supply-items/" + supplyItemId;
    }

    // http://localhost:8080/supply-items/{supplyItemId}/deactivate
    @PatchMapping("/supply-items/{supplyItemId}/deactivate")
    public String deactivate(@PathVariable Long supplyItemId) {
        service.deactivate(supplyItemId);
        return "redirect:/supply-items/" + supplyItemId;
    }

    // http://localhost:8080/supply-items/{supplyItemId}/activate
    @PatchMapping("/supply-items/{supplyItemId}/activate")
    public String activate(@PathVariable Long supplyItemId) {
        service.activate(supplyItemId);
        return "redirect:/supply-items/" + supplyItemId;
    }
}
