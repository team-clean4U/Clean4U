$(function () {
    const $summernote = $('#summernote');
    if ($summernote.length === 0) return;

    $('#summernote').summernote({
        placeholder: '내용을 입력해주세요.',
        height: 400,
        minHeight: 200,
        maxHeight: 630,
        width: 750,
        lang: 'ko-KR',
        toolbar: [
            ['style', ['bold', 'italic', 'underline', 'clear']],
            ['fontname', ['fontname']],
            ['fontsize', ['fontsize']],
            ['color', ['forecolor', 'backcolor']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['insert', ['link', 'picture']],
            ['view', ['help']]
        ],
        fontNames: ['Arial', 'Arial Black', 'Comic Sans MS', 'Courier New','맑은 고딕','궁서','굴림체','굴림','돋음체','바탕체'],
        fontSizes: ['12','14','16','18','20','22','24','28','30','36','50','72','96'],
        callbacks : {
            onImageUpload : function (files) {
                for (let i = 0; i < files.length; i++) {
                    uploadImage(files[i], this);
                }
            }
        }
    });
});

async function uploadImage(file, editor) {
    const formData = new FormData();
    formData.append("image", file);

    try {
        const res = await fetch("/api/v1/notices/image", {
            method: "POST",
            body: formData,
        });

        if (!res.ok) throw new Error("업로드 실패");

        const data = await res.json();
        $(editor).summernote("insertImage", data.data.imageUrl);
    } catch (e) {
        alert(e.message);
    }
}