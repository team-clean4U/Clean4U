package org.example.clean4u.laundryItem;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@RequiredArgsConstructor
@Controller
public class LaundryItemController {

    private final LaundryItemService service;

    // http://localhost:8080/laundry-items
    @GetMapping("/laundry-items")
    public String laundryItemList(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) LaundryCategory category,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
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

        PageResponse<LaundryItemResponse.ListDTO> laundryItemListPage = service.laundryItemList(pageIndex, size, category, name, isActive);
        model.addAttribute("laundryItemList", laundryItemListPage.getContent());
        model.addAttribute("laundryItemPage", laundryItemListPage);
        model.addAttribute("name", name == null ? "" : name);
        model.addAttribute("category", category);
        model.addAttribute("isActive", isActive);
        model.addAttribute("isActiveTrue", Boolean.TRUE.equals(isActive));
        model.addAttribute("isActiveFalse", Boolean.FALSE.equals(isActive));
        model.addAttribute("queryString", queryString);
        model.addAttribute("additionalCss", Arrays.asList("/css/pageLink.css"));

        return "laundryItem/list-form";
    }

    // http://localhost:8080/laundry-items/{laundryItemId}
    @GetMapping("/laundry-items/{laundryItemId}")
    public String detail(@PathVariable Long laundryItemId, Model model) {
        LaundryItemResponse.DetailDTO laundryItem = service.getDetail(laundryItemId);
        model.addAttribute("laundryItem", laundryItem);
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css"));

        return "laundryItem/detail-form";
    }

    // http://localhost:8080/laundry-items/new
    @GetMapping("/laundry-items/new")
    public String saveForm(Model model) {
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css"));
        return "laundryItem/save-form";
    }

    // http://localhost:8080/laundry-items/new
    @PostMapping("/laundry-items/new")
    public String saveProc(@Valid LaundryItemRequest.SaveDTO saveDTO) {
        service.save(saveDTO);
        return "redirect:/laundry-items";
    }

    // http://localhost:8080/laundry-items/{laundryItemId}/edit
    @GetMapping("/laundry-items/{laundryItemId}/edit")
    public String updateForm(@PathVariable Long laundryItemId, Model model) {
        LaundryItemResponse.UpdateFormDTO laundryItem = service.getFormForUpdate(laundryItemId);
        model.addAttribute("laundryItem", laundryItem);

        LaundryCategory category = laundryItem.getCategory();
        model.addAttribute("isTop", category == LaundryCategory.TOP);
        model.addAttribute("isBottom", category == LaundryCategory.BOTTOM);
        model.addAttribute("isOuter", category == LaundryCategory.OUTER);
        model.addAttribute("isBedding", category == LaundryCategory.BEDDING);
        model.addAttribute("isAccessory", category == LaundryCategory.ACCESSORY);
        model.addAttribute("isSpecial", category == LaundryCategory.SPECIAL);
        model.addAttribute("isEtc", category == LaundryCategory.ETC);
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css"));

        return "laundryItem/update-form";
    }

    // http://localhost:8080/laundry-items/{laundryItemId}/status
    @PostMapping("/laundry-items/{laundryItemId}/status")
    public String updateStatus(@PathVariable Long laundryItemId, @RequestParam Boolean isActive) {
        if (isActive) {
            service.activate(laundryItemId);
        } else {
            service.deactivate(laundryItemId);
        }
        
        return "redirect:/laundry-items/" + laundryItemId;
    }
}
