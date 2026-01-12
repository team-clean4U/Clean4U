async function deleteNotice(id) {
    if (!confirm("정말 삭제하시겠습니까?")) {
        return;
    }
    
    try {
        const response = await fetch(`/api/v1/notices/${id}`, {
            method: "DELETE"
        });
        
        if (response.ok) {
            window.location.href = "/notices/list";
        } else {
            alert("삭제에 실패했습니다.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("삭제 중 오류가 발생했습니다.");
    }
}
