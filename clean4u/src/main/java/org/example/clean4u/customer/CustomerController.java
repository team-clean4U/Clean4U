package org.example.clean4u.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class CustomerController {
    private final CustomerPersistRepository repository;


}
