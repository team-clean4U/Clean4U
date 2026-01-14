package org.example.clean4u.employee;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception403;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping
@RestController
public class EmployeeApiController {

    private final EmployeeService employeeService;

    @PutMapping("/api/v1/employees/me")
    public ResponseEntity<ApiResponse<EmployeeResponse.UpdateDTO>> updateProc(
            @Valid EmployeeRequest.UpdateDTD updateDTD,
            HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        try {
            EmployeeResponse.UpdateDTO updateEmployee = employeeService.updateProc(updateDTD, sessionUser.getId());
            session.setAttribute("sessionUser", updateEmployee);
            return ResponseEntity.ok(ApiResponse.ok(updateEmployee));
        } catch (Exception403 e) {
            throw e;
        } catch (Exception404 e) {
            throw e;
        }
    }

    @DeleteMapping("/sessions")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
