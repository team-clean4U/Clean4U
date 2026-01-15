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
            ['color', ['forecolor', 'backcolor']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['insert', ['link', 'picture']],
            ['view', ['help']]
        ],
        disableDragAndDrop: true,
        disableGrammar: false,
    });
});