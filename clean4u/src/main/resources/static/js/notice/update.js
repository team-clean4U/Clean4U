document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("updateForm");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const files = form.uploadImages?.files;
        const hasFiles = files && files.length > 0;
        const noticeId = window.location.pathname.match(/\/(\d+)\/edit/)[1];

        try {
            const textFormData = new FormData();
            textFormData.append("title", form.title.value);
            textFormData.append("content", $('#summernote').summernote('code'));

            const textRes = await fetch(`/api/v1/notices/${noticeId}`, {
                method: "PUT",
                body: textFormData
            });

            if (!textRes.ok) {
                const errorBody = await textRes.json();
                throw new Error(errorBody.message);
            }
            if (textRes.ok) {
                console.log(textRes);
            }

            if (hasFiles) {
                const imageFormData = new FormData();
                Array.from(files).forEach(file => {
                    imageFormData.append("uploadImages", file);
                });

                const imgRes = await fetch(`/notices/${noticeId}/image`, {
                    method: "POST",
                    body: imageFormData,
                });

                if (!imgRes.ok) {
                    const errorBody = await imgRes.json();
                    throw new Error(errorBody.message);
                }
            }

            window.location.href = `/notices/${noticeId}`;
        } catch (error) {
            alert("수정 중 오류가 발생했습니다.");
        }
    });
});