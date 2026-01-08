async function deactivateLaundryOption(id) {
    if (!confirm("비활성화하시겠습니까?")) {
        return;
    }
    
    try {
        const response = await fetch(`/api/v1/laundry-options/${id}/deactivate`, {
            method: "PATCH"
        });
        
        if (response.ok) {
            window.location.reload();
        } else {
            alert("비활성화에 실패했습니다.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("비활성화 중 오류가 발생했습니다.");
    }
}

async function activateLaundryOption(id) {
    if (!confirm("활성화하시겠습니까?")) {
        return;
    }
    
    try {
        const response = await fetch(`/api/v1/laundry-options/${id}/activate`, {
            method: "PATCH"
        });
        
        if (response.ok) {
            window.location.reload();
        } else {
            alert("활성화에 실패했습니다.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("활성화 중 오류가 발생했습니다.");
    }
}
