document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("form");
    const phoneInput = document.getElementById("phone");

    form.addEventListener("submit", (e) => {
        const phone = phoneInput.value;

        if (!/^010-\d{4}-\d{4}$/.test(phone)) {
            alert("연락처 형식이 올바르지 않습니다.");
            phoneInput.focus();
            e.preventDefault(); // form의 기본동작 취소
        }
    });
});

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