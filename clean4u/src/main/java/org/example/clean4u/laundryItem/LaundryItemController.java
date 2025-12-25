package org.example.clean4u.laundryItem;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class LaundryItemController {

    private final LaundryItemService service;

    // http://localhost:8080/laundry-item/list
    @GetMapping("/laundry-item/list")
    public String laundryItemList(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) LaundryCategory category,
            @RequestParam(required = false) String name,
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

        PageResponse<LaundryItemResponse.ListDTO> laundryItemListPage = service.laundryItemList(pageIndex, size, category, name);
        model.addAttribute("laundryItemList", laundryItemListPage.getContent());
        model.addAttribute("laundryItemPage", laundryItemListPage);
        model.addAttribute("name", name == null ? "" : name);
        model.addAttribute("category", category);
        model.addAttribute("queryString", queryString);

        return "laundryItem/list-form";
    }

    // http://localhost:8080/laundry-item/{laundryItemId}
    @GetMapping("/laundry-item/{laundryItemId}")
    public String detail(@PathVariable Long laundryItemId, Model model) {
        LaundryItemResponse.DetailDTO laundryItem = service.getDetail(laundryItemId);
        model.addAttribute("laundryItem", laundryItem);

        return "laundryItem/detail-form";
    }

    // http://localhost:8080/laundry-item/save
    @GetMapping("/laundry-item/save")
    public String saveForm() {
        return "laundryItem/save-form";
    }

    // http://localhost:8080/laundry-item/save
    @PostMapping("/laundry-item/save")
    public String saveProc(@Valid LaundryItemRequest.SaveDTO saveDTO) {
        service.save(saveDTO);
        return "redirect:/laundry-item/list";
    }

    // http://localhost:8080/laundry-item/{laundryItemId}/update
    @GetMapping("/laundry-item/{laundryItemId}/update")
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

        return "laundryItem/update-form";
    }

    // http://localhost:8080/laundry-item/{laundryItemId}/update
    @PostMapping("/laundry-item/{laundryItemId}/update")
    public String updateProc(@PathVariable Long laundryItemId, @Valid LaundryItemRequest.UpdateDTO updateDTO) {
        service.update(laundryItemId, updateDTO);
        return "redirect:/laundry-item/" + laundryItemId;
    }

    // http://localhost:8080/laundry-item/{laundryItemId}/delete
    @PostMapping("/laundry-item/{laundryItemId}/delete")
    public String delete(@PathVariable Long laundryItemId) {
        service.delete(laundryItemId);
        return "redirect:/laundry-item/list";
    }
}
