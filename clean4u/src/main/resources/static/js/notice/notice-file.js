const fileInput = document.getElementById("attachments");
const fileNameSpan = document.getElementById("fileName");

fileInput.addEventListener("change", function () {
    const files = fileInput.files;

    if (!files || files.length === 0) {
        fileNameSpan.textContent = "선택된 파일 없음";
        return;
    }

    fileNameSpan.innerHTML = "";

    Array.from(files).forEach(file => {
        const div = document.createElement("div");
        div.textContent = file.name;
        fileNameSpan.appendChild(div);
    })
})