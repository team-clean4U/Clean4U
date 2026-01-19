document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("updateForm");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const noticeId = window.location.pathname.match(/\/(\d+)\/edit/)[1];
        const formData = new FormData();

        formData.append("title", form.title.value);
        formData.append("content", $('#summernote').summernote('code'));

        const deleteInput = document.getElementById("deleteFileIds");
        if (deleteInput && deleteInput.value) {
            deleteInput.value.split(",").forEach(id => {
                formData.append("deleteFileIds", id);
            });
        }

        const files = document.getElementById("attachments").files;
        if (files && files.length > 0) {
            Array.from(files).forEach(file => {
                formData.append("newAttachments", file);
            });
        }

        try {
            const res = await fetch(`/api/v1/notices/${noticeId}`, {
                method: "PUT",
                body: formData
            });

            if (!res.ok) {
                const errorBody = await res.json();
                throw new Error(errorBody.message);
            }

            window.location.href = `/notices/${noticeId}`;
        } catch (error) {
            alert("수정 중 오류가 발생했습니다.");
        }
    });
});