document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("updateForm");
    if (form) {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();

            const customerId = window.location.pathname.match(/\/(\d+)\/edit/)[1];
            const phoneInput = document.getElementById("phone");

            if (!/^010-\d{4}-\d{4}$/.test(phoneInput.value)) {
                alert("연락처 형식이 올바르지 않습니다.");
                phoneInput.focus();
                return;
            }

            const formData = new FormData(form);
            const data = {
                name: formData.get("name"),
                phone: formData.get("phone"),
                birth: formData.get("birth"),
                memo: formData.get("memo")
            };

            try {
                const response = await fetch(`/api/v1/customers/${customerId}`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(data)
                });

                if (response.ok) {
                    alert("수정이 완료되었습니다");
                    window.location.href = `/customers/${customerId}`;

                } else {
                    const errorBody = await response.json();
                    throw new Error(errorBody.message);
                }
            } catch (error) {
                alert(error.message);
            }
        });
    }
});