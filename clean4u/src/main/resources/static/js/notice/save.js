const input = document.getElementById("attachments");
const fileList = document.getElementById("attachment-list");

input.addEventListener("change", () => {
    fileList.innerHTML = "";

    if (input.files.length === 0) {
        addNoFileMessage();
        return;
    }

    const files = input.files;

    Array.from(files).forEach((file, idx) => {
        const span = document.createElement("span");
        span.className = "image-label-file";
        span.innerHTML = `<span>${file.name}</span><i class="fa-solid fa-x remove-file" data-index="${idx}"></i>`;
        fileList.appendChild(span);
    });
});

document.addEventListener("click", (e) => {
    if (!e.target.classList.contains("remove-file")) return;

    const span = e.target.closest(".image-label-file");
    span.remove();

    const remainingFiles = fileList.querySelectorAll(".image-label-file").length;
    if (remainingFiles === 0) {
        addNoFileMessage();
    }
});

function addNoFileMessage() {
    fileList.innerHTML = `<span id="noFile" class="image-label"> 선택된 파일 없음 </span>`;
}