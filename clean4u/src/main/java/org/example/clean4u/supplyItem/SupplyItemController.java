package org.example.clean4u.supplyItem;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception401;
import org.example.clean4u.employee.Employee;
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

    // http://localhost:8080/supply-item
    @GetMapping("/supply-item")
    public String supplyItemList(Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }
        List<SupplyItem> supplyItemList = repository.findAll();
        model.addAttribute("supplyItemList", supplyItemList);
        return "supplyItem/list-form";
    }

    // http://localhost:8080/supply-item/{id}
    @GetMapping("/supply-item/{id}")
    public String supplyItem(@PathVariable Long id, Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }
        SupplyItem supplyItem = repository.findById(id);
        model.addAttribute("supplyItem", supplyItem);

        return "supplyItem/detail-form";
    }

    // http://localhost:8080/supply-item/save
    @GetMapping("/supply-item/save")
    public String saveForm(HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }
        return "supplyItem/save-form";
    }

    // http://localhost:8080/supply-item/save
    @PostMapping("/supply-item/save")
    public String saveProc(@Valid SupplyItemRequest.SaveDTO saveDTO, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }
        SupplyItem supplyItem = saveDTO.toEntity();
        repository.save(supplyItem);
        return "redirect:/supply-item";
    }

    // http://localhost:8080/supply-item/{id}/update
    @GetMapping("/supply-item/{id}/update")
    public String updateForm(@PathVariable Long id, Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }
        SupplyItem supplyItem = repository.findById(id);
        model.addAttribute("supplyItem", supplyItem);
        return "supplyItem/update-form";
    }

    // http://localhost:8080/supply-item/{id}/update
    @PostMapping("/supply-item/{id}/update")
    public String updateProc(@PathVariable Long id, @Valid SupplyItemRequest.UpdateDTO updateDTO, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }
        repository.updateById(id, updateDTO);
        return "redirect:/supply-item/{id}";
    }

    // http://localhost:8080/supply-item/{id}/delete
    @PostMapping("/supply-item/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }
        repository.deleteById(id);
        return "redirect:/supply-item";
    }
}
