document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("laundryImage");
    const fileName = document.getElementById("fileName");
    const imageLabel = document.getElementById("imageLabel");

    if(!input || !fileName || !imageLabel) return;

    input.addEventListener("change", () => {
        if(input.files && input.files[0]) {
            fileName.textContent = input.files[0].name;
            imageLabel.innerHTML = '<i class="fa-solid fa-edit"></i> 파일 수정';
        } else {
            fileName.textContent = " 선택된 파일 없음";
        }
    });
});