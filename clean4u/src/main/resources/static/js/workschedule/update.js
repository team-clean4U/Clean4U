document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("updateScheduleForm");
    const scheduleId = document.getElementById("scheduleId").value;
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const formData = new FormData(form);
        const data = {
            name: formData.get("name"),
            startTime: formData.get("startTime"),
            endTime: formData.get("endTime")
        }

        try {
            const response = await fetch(`/api/v1/schedules/${scheduleId}/edit`, {
                method: 'PUT',
                headers: {
                    "Content-Type": "application/json"
                },
                credentials: "include",
                body: JSON.stringify(data)
            });

            if (response.ok) {
                alert("스케줄 일정이 수정되었습니다.");
                window.location.href = "/schedules";
            }
        } catch (error) {
            console.error("Error:", error);
            alert("수정 중 오류가 발생했습니다.");
        }
    });
});