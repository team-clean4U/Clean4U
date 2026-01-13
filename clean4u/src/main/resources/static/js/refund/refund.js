document.addEventListener("DOMContentLoaded", () => {
    const refundData = document.getElementById("refundData");
    const reason = document.getElementById("reason");
    const paymentId = refundData.dataset.paymentId;

    refundData.addEventListener("submit", (e) => {
        e.preventDefault();

        if (!reason.value.trim()) {
            alert("환불 사유를 입력하세요");
            return;
        }
        const formData = new FormData(refundData);

        try {
            fetch(`/api/v1/refunds/${paymentId}`, {
                method: "POST",
                body: formData
            })
                .then(res => {
                    if (!res.ok) {
                        alert("환불에 실패하였습니다.");
                        throw new Error("환불 실패");
                    }
                    return res.json();
                })
                .then((data) => {
                    if(data.success) {
                        alert("환불 완료되었습니다.");
                        location.href = `/orders`;
                    } else {
                        alert("환불 실패");
                    }
                })
        } catch (error) {
            console.error("Error: ", error);
            alert("환불 중 오류가 발생했습니다.");
        }
    });
});