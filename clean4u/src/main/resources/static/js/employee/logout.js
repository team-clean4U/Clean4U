document.addEventListener("DOMContentLoaded", () => {
    const logoutBtn = document.querySelector('#logoutBtn');
    if (!logoutBtn) return;

    logoutBtn.addEventListener("click", async (e) => {
        e.preventDefault();

        const confirmed = confirm("로그아웃 하시겠습니까?");
        if (!confirmed) return;

        try {
            const response = await fetch("/api/v1/sessions", {
                method: 'DELETE',
                credentials: "include",
            });

            const result = await response.json();

            if (response.ok && result.success) {
                alert(result.message);
                window.location.href = "/login";
            } else {
                alert("로그아웃 실패");
            }
        } catch (error) {
            console.error("Error:", error);
            alert("로그아웃 중 오류가 발생했습니다.");
        }
    });
});