package org.example.clean4u.supplyItem;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SupplyItemController {

    private final SupplyItemRepository repository;

    @GetMapping("/supply-item")
    public String supplyItemList(Model model) {
        List<SupplyItem> supplyItemList = repository.findAll();
        model.addAttribute("supplyItemList", supplyItemList);
        return "supplyItem/list-form";
    }

    @GetMapping("/supply-item/{id}")
    public String supplyItem(@PathVariable Long id, Model model) {
        SupplyItem supplyItem = repository.findById(id);
        if (supplyItem == null) {
            throw new RuntimeException("자재를 찾을 수 없습니다. : " + id);
        }
        model.addAttribute("supplyItem", supplyItem);

        return "supplyItem/detail-form";
    }

    @GetMapping("/supply-item/save")
    public String saveForm() {
        return "supplyItem/save-form";
    }

    @PostMapping("/supply-item/save")
    public String saveProc(SupplyItemRequest.SaveDTO saveDTO) {
        SupplyItem supplyItem = saveDTO.toEntity();
        repository.save(supplyItem);
        return "redirect:/supply-item";
    }

    @GetMapping("/supply-item/{id}/update")
    public String updateForm(@PathVariable Long id, Model model) {
        SupplyItem supplyItem = repository.findById(id);
        if (supplyItem == null) {
            throw new RuntimeException("수정할 자재를 찾을 수 없습니다.");
        }
        model.addAttribute("supplyItem", supplyItem);
        return "supplyItem/update-form";
    }

    @PostMapping("/supply-item/{id}/update")
    public String updateProc(@PathVariable Long id, SupplyItemRequest.UpdateDTO updateDTO) {
        try {
            repository.updateById(id, updateDTO);
        } catch (Exception e) {
            throw new RuntimeException("자재 수정을 실패했습니다.");
        }
        return "redirect:/supply-item/{id}";
    }

    @PostMapping("/supply-item/{id}/delete")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/supply-item";
    }
}
