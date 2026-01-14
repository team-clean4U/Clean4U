async function deleteLaundryItem(id) {
    if (!confirm("정말 삭제하시겠습니까?")) {
        return;
    }
    
    try {
        const response = await fetch(`/api/v1/laundry-items/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            const errorBody = await response.json();
            const error = new Error(errorBody.message || "삭제에 실패했습니다.");
            alert(error.message);
            throw error;
        }
        window.location.href = "/laundry-items";
    } catch (error) {
        console.error("Error:", error);
        if (!error.message || error.message === "삭제에 실패했습니다.") {
            alert(error.message || "삭제 중 오류가 발생했습니다.");
        }
    }
}
