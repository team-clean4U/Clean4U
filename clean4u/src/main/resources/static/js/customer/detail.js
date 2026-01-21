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
        alert("주문 이력이 있는 고객은 삭제할 수 없습니다.\n" + "비활성화를 사용해주세요. ");
    }
}
