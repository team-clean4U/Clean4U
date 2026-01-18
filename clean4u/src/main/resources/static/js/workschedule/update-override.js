document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("updateOverrideForm");
    const overrideId = document.getElementById("id").value;
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const formData = new FormData(form);
        const data = {
            date: formData.get("date"),
            originalId: formData.get("originalId"),
            overrideId: formData.get("overrideId"),
            startTime: formData.get("startTime"),
            endTime: formData.get("endTime")
        }

        try {
            const response = await fetch(`/api/v1/schedule-overrides/${overrideId}/edit`, {
                method: 'PUT',
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                alert("스케줄 일정이 수정되었습니다.");
                window.location.href = "/schedule-overrides";
            }
        } catch (error) {
            console.error("Error:", error);
            alert("수정 중 오류가 발생했습니다.");
        }
    });
});