async function deleteOverride(id) {
    if (!confirm("정말 삭제하시겠습니까?")) {
        return;
    }

    try {
        const response = await fetch(`/api/v1/schedule-overrides/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            const errorBody = await response.json();
            throw new Error(errorBody.message);
        } else {
            alert("삭제 되었습니다");
            window.location.href = "/schedule-overrides";
        }
    } catch (error) {
        alert("삭제 중 오류가 발생했습니다.");
    }
}
