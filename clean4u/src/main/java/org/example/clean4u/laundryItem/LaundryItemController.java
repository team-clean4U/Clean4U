package org.example.clean4u.laundryItem;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class LaundryItemController {

    private final LaundryItemRepository repository;

    // http://localhost:8080/laundry-item
    @GetMapping("/laundry-item")
    public String laundryItemList(Model model) {
        List<LaundryItem> laundryItemList = repository.findAll();
        model.addAttribute("laundryItemList", laundryItemList);
        return "laundryItem/list-form";
    }

    // http://localhost:8080/laundry-item/{id}
    @GetMapping("/laundry-item/{id}")
    public String detail(@PathVariable Long id, Model model) {
        LaundryItem laundryItem = repository.findById(id);
        if (laundryItem == null) {
            throw new RuntimeException("세탁물을 찾을 수 없습니다. : " + id);
        }
        model.addAttribute("laundryItem", laundryItem);

        return "laundryItem/detail-form";
    }

    // http://localhost:8080/laundry-item/save
    @GetMapping("/laundry-item/save")
    public String saveForm() {
        return "laundryItem/save-form";
    }

    @PostMapping("/laundry-item/save")
    public String saveProc(LaundryItemRequest.SaveDTO saveDTO) {
        LaundryItem laundryItem = saveDTO.toEntity();
        repository.save(laundryItem);
        return "redirect:/laundry-item";
    }

    // http://localhost:8080/laundry-item/{id}/update
    @GetMapping("/laundry-item/{id}/update")
    public String updateForm(@PathVariable Long id, Model model) {
        LaundryItem laundryItem = repository.findById(id);
        if (laundryItem == null) {
            throw new RuntimeException("수정할 세탁물을 찾을 수 없습니다.");
        }
        model.addAttribute("laundryItem", laundryItem);

        LaundryCategory category = laundryItem.getCategory();
        model.addAttribute("isTop", category == LaundryCategory.TOP);
        model.addAttribute("isBottom", category == LaundryCategory.BOTTOM);
        model.addAttribute("isOuter", category == LaundryCategory.OUTER);
        model.addAttribute("isUnderwear", category == LaundryCategory.UNDEREWEAR);
        model.addAttribute("isBedding", category == LaundryCategory.BEDDING);
        model.addAttribute("isAccessory", category == LaundryCategory.ACCESSORY);
        model.addAttribute("isSpecial", category == LaundryCategory.SPECIAL);
        model.addAttribute("isEtc", category == LaundryCategory.ETC);

        return "laundryItem/update-form";
    }

    @PostMapping("/laundry-item/{id}/update")
    public String updateProc(@PathVariable Long id, LaundryItemRequest.UpdateDTO updateDTO) {
        try {
            repository.updateById(id, updateDTO);
        } catch (Exception e) {
            throw new RuntimeException("세탁물 수정을 실패했습니다.");
        }
        return "redirect:/laundry-item";
    }

    @PostMapping("/laundry-item/{id}/delete")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/laundry-item";
    }
}
