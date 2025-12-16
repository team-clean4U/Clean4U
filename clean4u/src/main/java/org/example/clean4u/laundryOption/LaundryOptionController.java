package org.example.clean4u.laundryOption;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LaundryOptionController {

    private final LaundryOptionService service;

    // http://localhost:8080/laundry-option
    @GetMapping("/laundry-option")
    public String laundryOptionList(
            Model model,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive
    ) {
        List<LaundryOptionResponse.ListDTO> laundryOptionList;

        if (name != null && !name.isBlank() && isActive != null) {
            laundryOptionList = service.findByNameContainingAndIsActive(name, isActive);
        } else if (name != null && !name.isBlank()) {
            laundryOptionList = service.findByNameContaining(name);
        } else if (isActive != null) {
            laundryOptionList = service.findByIsActive(isActive);
        } else {
            laundryOptionList = service.getAllLaundryOptions();
        }
        model.addAttribute("laundryOptionList", laundryOptionList);
        return "laundryOption/list-form";
    }

    // http://localhost:8080/laundry-option/{laundryOptionId}
    @GetMapping("/laundry-option/{laundryOptionId}")
    public String detail(@PathVariable Long laundryOptionId, Model model) {
        LaundryOptionResponse.DetailDTO laundryOption = service.getDetail(laundryOptionId);
        model.addAttribute("laundryOption", laundryOption);

        return "laundryOption/detail-form";
    }

    // http://localhost:8080/laundry-option/save
    @GetMapping("/laundry-option/save")
    public String saveForm() {
        return "laundryOption/save-form";
    }

    // http://localhost:8080/laundry-option/save
    @PostMapping("/laundry-option/save")
    public String saveProc(@Valid LaundryOptionRequest.SaveDTO saveDTO) {
        service.save(saveDTO);
        return "redirect:/laundry-option";
    }

    // http://localhost:8080/laundry-option/{laundryOptionId}/update
    @GetMapping("/laundry-option/{laundryOptionId}/update")
    public String updateForm(@PathVariable Long laundryOptionId, Model model) {
        LaundryOptionResponse.UpdateFormDTO laundryOption = service.getFormForUpdate(laundryOptionId);
        model.addAttribute("laundryOption", laundryOption);
        return "laundryOption/update-form";
    }

    // http://localhost:8080/laundry-option/{laundryOptionId}/update
    @PostMapping("/laundry-option/{laundryOptionId}/update")
    public String updateProc(@PathVariable Long laundryOptionId, @Valid LaundryOptionRequest.UpdateDTO updateDTO) {
        service.update(laundryOptionId, updateDTO);
        return "redirect:/laundry-option/" + laundryOptionId;
    }

    // http://localhost:8080/laundry-option/{laundryOptionId}/delete
    @PostMapping("/laundry-option/{laundryOptionId}/delete")
    public String delete(@PathVariable Long laundryOptionId) {
        service.delete(laundryOptionId);
        return "redirect:/laundry-option";
    }
}
