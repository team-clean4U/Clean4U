package org.example.clean4u.laundryOption;

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

@Controller
@RequiredArgsConstructor
public class LaundryOptionController {

    private final LaundryOptionService service;

    // http://localhost:8080/laundry-options
    @GetMapping("/laundry-options")
    public String laundryOptionList(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
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

        PageResponse<LaundryOptionResponse.ListDTO> laundryOptionListPage = service.laundryOptionList(pageIndex, size, isActive, name);
        model.addAttribute("laundryOptionList", laundryOptionListPage.getContent());
        model.addAttribute("laundryOptionPage", laundryOptionListPage);
        model.addAttribute("name", name == null ? "" : name);
        model.addAttribute("isActive", isActive);
        model.addAttribute("isActiveTrue", Boolean.TRUE.equals(isActive));
        model.addAttribute("isActiveFalse", Boolean.FALSE.equals(isActive));
        model.addAttribute("queryString", queryString);
        model.addAttribute("additionalCss", Arrays.asList("/css/pageLink.css", "/css/laundry-option.css"));
        return "laundryOption/list-form";
    }

    // http://localhost:8080/laundry-options/{laundryOptionId}
    @GetMapping("/laundry-options/{laundryOptionId}")
    public String detail(@PathVariable Long laundryOptionId, Model model) {
        LaundryOptionResponse.DetailDTO laundryOption = service.getDetail(laundryOptionId);
        model.addAttribute("laundryOption", laundryOption);
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/update.css"));

        return "laundryOption/detail-form";
    }

    // http://localhost:8080/laundry-options/new
    @GetMapping("/laundry-options/new")
    public String saveForm(Model model) {
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/update.css"));

        return "laundryOption/save-form";
    }

    // http://localhost:8080/laundry-options/new
    @PostMapping("/laundry-options/new")
    public String saveProc(@Valid LaundryOptionRequest.SaveDTO saveDTO) {
        service.save(saveDTO);

        return "redirect:/laundry-options";
    }

    // http://localhost:8080/laundry-options/{laundryOptionId}/edit
    @GetMapping("/laundry-options/{laundryOptionId}/edit")
    public String updateForm(@PathVariable Long laundryOptionId, Model model) {
        LaundryOptionResponse.UpdateFormDTO laundryOption = service.getFormForUpdate(laundryOptionId);

        model.addAttribute("laundryOption", laundryOption);
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/update.css"));

        return "laundryOption/update-form";
    }

    // http://localhost:8080/laundry-options/{laundryOptionId}/status
    @PostMapping("/laundry-options/{laundryOptionId}/status")
    public String updateStatus(@PathVariable Long laundryOptionId, @RequestParam Boolean isActive) {
        if (isActive) {
            service.activate(laundryOptionId);
        } else {
            service.deactivate(laundryOptionId);
        }
        
        return "redirect:/laundry-options/" + laundryOptionId;
    }
}
