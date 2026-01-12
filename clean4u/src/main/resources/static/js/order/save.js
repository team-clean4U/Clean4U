document.addEventListener("DOMContentLoaded", () => {
    const dataEl = document.getElementById("customers-data");
    if (!dataEl) return;

    const customers = JSON.parse(dataEl.textContent);
    const idInput = document.getElementById("customer-id");
    const nameInput = document.getElementById("customer-name");
    const phoneWrapper = document.getElementById("customer-phone-wrapper");
    const phoneDropdown = document.getElementById("customer-phone-dropdown");
    const phoneSelected = document.querySelector("#customer-phone-select .category-selected");

    if (!nameInput || !idInput || !phoneWrapper || !phoneDropdown) return;

    if (typeof bindDropdown === 'function') {
        bindDropdown(phoneWrapper);
    }

    function updatePhoneList(matched) {

        idInput.value = '';
        phoneSelected.textContent = '휴대폰 번호 선택';
        phoneDropdown.innerHTML = '';

        if (matched.length === 0) {
            phoneSelected.textContent = '미등록 고객입니다.';
            phoneDropdown.innerHTML = '<li data-id="" data-phone="">미등록 고객입니다.</li>';
        } else if (matched.length === 1) {
            idInput.value = matched[0].customerId;
            phoneSelected.textContent = matched[0].phone;
            phoneDropdown.innerHTML =
                `<li data-id="${matched[0].customerId}" data-phone="${matched[0].phone}" class="selected">${matched[0].phone}</li>`;
        } else {
            matched.forEach(customer => {
                const li = document.createElement("li");
                li.setAttribute("data-id", customer.customerId);
                li.setAttribute("data-phone", customer.phone);
                li.textContent = customer.phone;
                phoneDropdown.appendChild(li);
            });
        }
    }

    nameInput.addEventListener("input", () => {
        const matched = customers.filter(c => c.name === nameInput.value.trim());
        updatePhoneList(matched);
    });

    phoneDropdown.addEventListener("click", (e) => {
        const li = e.target.closest("li");
        if(!li) return;

        const phone = li.dataset.phone;
        const id = li.dataset.id;

       if(!phone || !id) return;

       idInput.value = id;
       phoneSelected.textContent = phone;

       phoneDropdown.querySelectorAll("li").forEach(el =>
            el.classList.remove("selected")
       );
       li.classList.add("selected");

       phoneWrapper
            .querySelector(".category-dropdown")
            .classList.remove("active");

       phoneWrapper
           .querySelector(".category-select")
           .classList.remove("active");
    });

    const input = document.getElementById("laundryImage");
    const fileName = document.getElementById("fileName");
    const imageLabel = document.getElementById("imageLabel");

    input.addEventListener("change", () => {
        const oldBtn = document.getElementById("imageDelete");
        if(oldBtn) oldBtn.remove();

        if(input.files && input.files[0]) {
            imageLabel.innerHTML = '<i class="fa-solid fa-edit"></i> 파일 수정';
            fileName.textContent = input.files[0].name;
        } else {
            fileName.textContent = " 선택된 파일 없음";
        }
    });
});