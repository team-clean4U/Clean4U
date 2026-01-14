document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("updateForm");
    if (form) {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            const formData = new FormData(form);
            const id = window.location.pathname.match(/\/(\d+)\/update/)[1];
            const data = {
                name: formData.get("name"),
                category: formData.get("category"),
                basePrice: parseInt(formData.get("basePrice")) || 0,
                description: formData.get("description") || ""
            };
            
            try {
                const response = await fetch(`/api/v1/laundry-items/${id}`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(data)
                });
                
                if (!response.ok) {
                    const errorBody = await response.json();
                    const error = new Error(errorBody.message || "수정에 실패했습니다.");
                    alert(error.message);
                    throw error;
                }
                window.location.href = `/laundry-items/${id}`;
            } catch (error) {
                console.error("Error:", error);
                if (!error.message || error.message === "수정에 실패했습니다.") {
                    alert(error.message || "수정 중 오류가 발생했습니다.");
                }
            }
        });
    }
});
