const fileInput = document.getElementById("attachments");
const noFile = document.getElementById("noFile"); // 첨부파일 없음
const newFile = document.getElementById("newFile"); // 새파일

fileInput.addEventListener("change", function () {
    const files = fileInput.files;
    newFile.innerHTML = "";

    if (!files || files.length === 0) {
        checkEmpty();
        return;
    }

    if (noFile) noFile.style.display = "none";

    Array.from(files).forEach(file => {
        const div = document.createElement("div");
        div.classList.add("image-label-file", "new-file-label")
        div.style.marginBottom = "8px";
        div.style.color = "blue";
        div.innerHTML = `${file.name}<i class="fa-solid fa-x remove-file" style=""></i>`;
        newFile.appendChild(div);
    });
});

document.addEventListener("click", (e) => {
    if (!e.target.classList.contains("remove-file")) return;

    const target = e.target.closest(".image-label-file");
    const fileId = target.dataset.fileId;

    if (fileId) {
        const hiddenInput = document.getElementById("deleteFileIds");
        const deleteIds = hiddenInput.value ? hiddenInput.value.split(",") : [];
        deleteIds.push(fileId);
        hiddenInput.value = deleteIds.join(",");
    }

    target.remove();
    checkEmpty();
});

function checkEmpty() {
    const totalFiles = document.querySelectorAll(".image-label-file").length;
    if (totalFiles === 0) {
        if (noFile) noFile.style.display = "block";
    } else {
        if (noFile) noFile.style.display = "none";
    }
}