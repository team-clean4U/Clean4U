document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("laundryImage");
    const fileName = document.getElementById("fileName");
    const imageLabel = document.getElementById("image-label");

    input.addEventListener("change", () => {
        fileName.textContent = input.files ? input.files[0].name : ' 선택된 파일 없음';
        imageLabel.textContent = '사진 수정';
    });
});