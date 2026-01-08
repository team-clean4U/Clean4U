document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("updateForm");
    if (form) {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            const formData = new FormData(form);
            const id = window.location.pathname.match(/\/(\d+)\/update/)[1];
            const data = {
                name: formData.get("name"),
                extraPrice: parseInt(formData.get("extraPrice")) || 0,
                description: formData.get("description") || "",
                isActive: formData.get("isActive") === "true"
            };
            
            try {
                const response = await fetch(`/api/v1/laundry-options/${id}`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(data)
                });
                
                if (response.ok) {
                    window.location.href = `/laundry-options/${id}`;
                } else {
                    alert("수정에 실패했습니다.");
                }
            } catch (error) {
                console.error("Error:", error);
                alert("수정 중 오류가 발생했습니다.");
            }
        });
    }
});
