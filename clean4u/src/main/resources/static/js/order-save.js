document.addEventListener("DOMContentLoaded", () => {
    const dataEl = document.getElementById("customers-data");
    if(!dataEl) {
        console.log("customers-data not found");
        return;
    }
    const customers = JSON.parse(dataEl.textContent);
    console.log(customers[0]);

    const idInput = document.getElementById("customer-id");
    const nameInput = document.getElementById("customer-name");
    const phoneSelect = document.getElementById("customer-phone");

    if(!nameInput || !idInput) {
        console.log("customer inputs not found");
        return;
    }

    nameInput.addEventListener("input", () => {
        const matched = customers.filter(c => c.name === nameInput.value.trim());

        phoneSelect.innerHTML = '<option value="">휴대폰 번호 선택</option>';
        phoneSelect.disabled = false;
        idInput.value = '';

        if (matched.length === 1) {
            idInput.value = matched[0].customerId;
            phoneSelect.innerHTML = `
                <option value="${matched[0].customerId}">
                    ${matched[0].phone}
                </option>
            `;
        } else if (matched.length > 1) {
            phoneSelect.disabled = false;
            matched.forEach(customer => {
                        const option = document.createElement("option");
                        option.value = customer.customerId;
                        option.textContent = customer.phone;
                        phoneSelect.appendChild(option);
                    });
        } else {
            const option = document.createElement("option");
            phoneSelect.innerHTML = `
                            <option value="">
                                미등록 고객입니다.
                            </option>
                        `;
        }

        phoneSelect.addEventListener("change", () => {
           idInput.value = phoneSelect.value;
        });
    });
});
