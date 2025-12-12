package org.example.clean4u.laundryOption;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LaundryOptionController {

    private final LaundryOptionRepository repository;

    // http://localhost:8080/laundry-option
    @GetMapping("/laundry-option")
    public String laundryOptionList(Model model) {
        List<LaundryOption> laundryOptionList = repository.findAll();
        model.addAttribute("laundryOptionList", laundryOptionList);
        return "/laundryOption/list-form";
    }

    // http://localhost:8080/laundry-option/{id}
    @GetMapping("/laundry-option/{id}")
    public String detail(@PathVariable Long id, Model model) {
        LaundryOption laundryOption = repository.findById(id);
        if (laundryOption == null) {
            throw new RuntimeException("세탁물 옵션을 찾을 수 없습니다. : " + id);
        }
        model.addAttribute("laundryOption", laundryOption);

        return "/laundryOption/detail-form";
    }

    // http://localhost:8080/laundry-option/save
    @GetMapping("/laundry-option/save")
    public String saveForm() {
        return "laundryOption/save-form";
    }

    @PostMapping("/laundry-option/save")
    public String saveProc(LaundryOptionRequest.SaveDTO saveDTO) {
        LaundryOption laundryOption = saveDTO.toEntity();
        repository.save(laundryOption);
        return "redirect:/laundry-option";
    }

    // http://localhost:8080/laundry-option/{id}/update
    @GetMapping("/laundry-option/{id}/update")
    public String updateForm(@PathVariable Long id, Model model) {
        LaundryOption laundryOption = repository.findById(id);
        if (laundryOption == null) {
            throw new RuntimeException("수정할 세탁물 옵션을 찾을 수 없습니다.");
        }
        model.addAttribute("laundryOption", laundryOption);
        return "laundryOption/update-form";
    }

    @PostMapping("/laundry-option/{id}/update")
    public String updateProc(@PathVariable Long id, LaundryOptionRequest.UpdateDTO updateDTO) {
        try {
            repository.updateById(id, updateDTO);
        } catch (Exception e) {
            throw new RuntimeException("세탁물 옵션 수정을 실패했습니다.");
        }
        return "redirect:/laundry-option/{id}";
    }

    // http://localhost:8080/laundry-option/{id}/delete
    @PostMapping("/laundry-option/{id}/delete")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/laundry-option";
    }
}
