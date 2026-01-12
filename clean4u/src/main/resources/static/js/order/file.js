document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("laundryImage");
    const fileName = document.getElementById("fileName");
    const imageLabel = document.getElementById("imageLabel");
    const formData = document.getElementById("updateForm");
    const orderId = formData.dataset.orderId;

    if(!input || !fileName || !imageLabel) return;

    input.addEventListener("change", () => {
        const oldBtn = document.getElementById("imageDelete");
        if(oldBtn) oldBtn.remove();

        if(input.files && input.files[0]) {
            imageLabel.innerHTML = '<i class="fa-solid fa-edit"></i> 파일 수정';
            fileName.textContent = input.files[0].name;

            const deleteBtn = document.createElement("button");
            deleteBtn.id = "imageDelete";
            deleteBtn.type = "button";
            deleteBtn.className = "btn btn-delete";
            // deleteBtn.setAttribute("form", "deleteImage");
            deleteBtn.innerHTML = '<i class="fa-solid fa-trash"></i>삭제';

            fileName.after(deleteBtn);
        } else {
            fileName.textContent = " 선택된 파일 없음";
        }
    });

    document.addEventListener("click", (e) => {
        if(e.target.closest("#imageDelete")) {
            deleteImage(orderId);
        }
    })
});