package org.example.clean4u.supplyItem;

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
public class SupplyItemController {

    private final SupplyItemService service;

    // http://localhost:8080/supply-item
    @GetMapping("/supply-item")
    public String supplyItemList(
            Model model,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean lowStock
    ) {
        List<SupplyItemResponse.ListDTO> supplyItemList;
        if (lowStock != null && lowStock) {
            supplyItemList = service.findLowStockItems();
        } else if (lowStock != null) {
            supplyItemList = service.findSafetyStockItems();
        } else if (name != null && !name.isBlank()) {
            supplyItemList = service.findByNameContaining(name);
        } else {
            supplyItemList = service.getAllSupplyItems();
        }
        model.addAttribute("supplyItemList", supplyItemList);
        model.addAttribute("name", name == null ? "" : name);
        model.addAttribute("lowStock", lowStock);
        model.addAttribute("lowStockTrue", Boolean.TRUE.equals(lowStock));
        model.addAttribute("lowStockFalse", Boolean.FALSE.equals(lowStock));
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
    public String saveProc(@Valid SupplyItemRequest.SaveDTO saveDTO) {
        service.save(saveDTO);
        return "redirect:/supply-item";
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
    public String updateProc(@PathVariable Long supplyItemId, @Valid SupplyItemRequest.UpdateDTO updateDTO) {
        service.update(supplyItemId, updateDTO);
        return "redirect:/supply-item/" + supplyItemId;
    }

    // http://localhost:8080/supply-item/{supplyItemId}/delete
    @PostMapping("/supply-item/{supplyItemId}/delete")
    public String delete(@PathVariable Long supplyItemId) {
        service.delete(supplyItemId);
        return "redirect:/supply-item";
    }
}
