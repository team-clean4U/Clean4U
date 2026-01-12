document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("updateForm");
    if (form) {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            const formData = new FormData(form);
            const noticeId = window.location.pathname.match(/\/(\d+)\/update/)[1];

            try {
                const response = await fetch(`/api/v1/notices/${noticeId}`, {
                    method: "PUT",
                    body: formData
                });

                if (response.ok) {
                    window.location.href = `/notices/${noticeId}`;
                }
            } catch (error) {
                console.error("Error:", error);
                alert("수정 중 오류가 발생했습니다.");
            }
        });
    }
});