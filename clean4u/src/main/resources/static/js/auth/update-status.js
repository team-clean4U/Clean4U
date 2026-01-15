document.querySelectorAll('.btn btn-edit, .btn btn-delete').forEach(button => {
    button.addEventListener('click', async (e) => {
        e.preventDefault();

        const employeeId = button.dataset.employeeId;
        const status = button.classList.contains('btn btn-edit') ? 'APPROVED' : "REJECTED";

        try {
            const response = fetch(`/admin/employees/${employeeId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ status })
            });

            if (!response.ok) {
                const errorData = await response.json();
                alert('오류: ' + errorData.message);
                return;
            }
            alert('직원 상태 변경 완료');
        } catch (error) {
            console.error(err);
            alert('네트워크 오류');
        }
    })
})