document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("updateForm");
    if (form) {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            const formData = new FormData(form);
            const customerId = window.location.pathname.match(/\/(\d+)\/edit/)[1];

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

                if (!response.ok) {
                    const errorBody = await response.json();
                    throw new Error(errorBody.message);
                } else {
                    window.location.href = "/customers";
                }
            } catch (error) {
                alert(error.message);
            }
        });
    }
});