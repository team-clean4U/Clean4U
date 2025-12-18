package org.example.clean4u.workschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    @GetMapping("/schedule")
    public String save() {

        return "workschedule/save-form";
    }

    @PostMapping("/schedule")
    public String saveProc(WorkScheduleRequest.SaveDTO saveDTO) {
        workScheduleService.saveProc(saveDTO);

        return "redirect:/schedule/list";
    }

    @GetMapping("/schedule/list")
    public String scheduleList(Model model) {
        List<WorkScheduleResponse.ListDTO> scheduleList = workScheduleService.scheduleList();
        model.addAttribute("scheduleList", scheduleList);

        return "workschedule/list-form";
    }
}
