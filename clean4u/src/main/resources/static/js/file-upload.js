document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("laundryImage");
    const fileName = document.getElementById("fileName");
    const imageLabel = document.getElementById("imageLabel");

    if(!input || !fileName || !imageLabel) return;

    input.addEventListener("change", () => {
        const oldBtn = document.getElementById("imageDelete");
        if(oldBtn) oldBtn.remove();

        if(input.files && input.files[0]) {
            imageLabel.innerHTML = '<i class="fa-solid fa-edit"></i> 파일 수정';
            fileName.textContent = input.files[0].name;

            const deleteBtn = document.createElement("button");
            deleteBtn.id = "imageDelete";
            deleteBtn.type = "submit";
            deleteBtn.className = "btn btn-delete";
            deleteBtn.setAttribute("form", "deleteImage");
            deleteBtn.innerHTML = '<i class="fa-solid fa-trash"></i>삭제';

            fileName.after(deleteBtn);
        } else {
            fileName.textContent = " 선택된 파일 없음";
        }
    });
});

// notice JS
document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("noticeImages");
    const fileName = document.getElementById("fileName");
    const imageLabel = document.getElementById("image-label");
    const preview = document.getElementById("preview");

    console.log("preview: ", preview);

    input.addEventListener("change", () => {
        preview.innerHTML = "";

        const files = input.files;
        if (!files || files.length === 0) {
            fileName.textContent = "선택된 파일이 없습니다";
            return;
        }
        fileName.textContent = `${files.length}개 파일이 선택되었습니다`;
        imageLabel.innerHTML = '<i class="fa-solid fa-edit"></i> 파일 수정';

        Array.from(files).forEach(file => {
            const render = new FileReader();
            render.onload = (e) => {
                const img = documnet.createElement("img");
                img.src = e.target.result;
                preview.appendChild(img);
            };
            render.readAsDataURL(file);
        });
    });
});