// 모달
function openDeleteModal() {
    document.getElementById("modal-bg").style.display = 'flex';
}

function closeDeleteModal() {
    document.getElementById("modal-bg").style.display = 'none';
}

document.getElementById("modal-bg").addEventListener("click", function (e){
    if (e.target === this) {
        closeDeleteModal();
    }
});

// 모달 비활성화
async function deactivateCustomer(id) {
    if (!confirm("비활성화하시겠습니까?")) {
        return;
    }

    try {
        const response = await fetch(`/api/v1/customers/${id}/deactivate`, {
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

async function activateCustomer(id) {
    if (!confirm("활성화하시겠습니까?")) {
        return;
    }

    try {
        const response = await fetch(`/api/v1/customers/${id}/activate`, {
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

// 삭제
async function deleteCustomer(id) {
    if (!confirm("정말 삭제하시겠습니까?")) {
        return;
    }

    try {
        const response = await fetch(`/api/v1/customers/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            const errorBody = await response.json();
            throw new Error(errorBody.message);
        } else {
            alert("삭제 되었습니다");
            setTimeout(() => {
                window.location.href = `/customers`;
            }, 100);
        }
    } catch (error) {
        console.error("Error:", error);
        alert("삭제 중 오류가 발생했습니다.");
    }
}
