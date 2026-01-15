document.addEventListener("DOMContentLoaded", () => {
    const reason = document.getElementById("reason");
    const paymentId = refundData.dataset.paymentId;

    refundData.addEventListener("submit", (e) => {
        e.preventDefault();

        if (!reason.value.trim()) {
            alert("환불 사유를 입력하세요");
            return;
        }

        try {
            fetch(`/api/v1/refunds/${paymentId}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    reason: reason.value
                })
            })
                .then(res => {
                    if (!res.ok) {
                        alert("환불에 실패하였습니다.");
                        throw new Error("환불 중 오류가 발생했습니다.");
                    }
                    return res.json();
                })
                .then((data) => {
                    if(data.success) {
                        alert("환불 완료되었습니다.");
                        location.href = `/payments`;
                    } else {
                        alert("환불에 실패하였습니다.");
                    }
                })
        } catch (error) {
            alert(error.message);
        }
    });
});