document.addEventListener('DOMContentLoaded', function () {
    const scheduleType = document.getElementById('scheduleType');
    const normalForm = document.getElementById('normalForm');
    const overrideForm = document.getElementById('overrideForm');
    const form = document.getElementById('scheduleForm');

    function switchForm() {
        if(scheduleType.value === 'NORMAL') {
            normalForm.style.display = 'flex';   // 기존 CSS 때문에 flex가 안전
            overrideForm.style.display = 'none';
            form.action = '/schedule/normal';
        } else {
            normalForm.style.display = 'none';
            overrideForm.style.display = 'flex';
            form.action = '/schedule/override';
        }
    }

    // 초기 폼 상태
    switchForm();

    // select 변경 시 폼 전환
    scheduleType.addEventListener('change', switchForm);
});