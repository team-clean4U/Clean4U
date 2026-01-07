document.addEventListener("DOMContentLoaded", () => {
    const impKey = 'imp47240471';
    const paymentBtn = document.getElementById("paymentBtn");
    const amount = Number(document.getElementById("amount").textContent.replace(/[^0-9]/g, ""));
    const orderId = Number(document.getElementById("orderId").value);

    if(!paymentBtn) return;

    paymentBtn.addEventListener('click', () => {
        fetch("/api/payment/prepare", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                orderId: orderId,
                amount: amount
            })
        })
            .then(res => {
                if(!res.ok) {
                    throw Error("주문번호 생성 실패");
                }
                return res.json();
            })
            .then(data => {
                const merchantUid = data.merchantUid;
                const payAmount = data.amount;
                const IMP = window.IMP;

                IMP.init(impKey);

                IMP.request_pay(
                    {
                        channelKey: "channel-key-596c9a9f-b4b4-419d-bade-37948cee1ccf",
                        pay_method: "card",
                        merchant_uid: merchantUid,
                        name: "세탁 주문 결제",
                        amount: payAmount,
                        buyer_name: "{{order.customerName}}",
                    },
                    function (response) {
                        if(response.success) {
                            verifyPayment(response.imp_uid, response.merchant_uid, orderId)
                        } else {
                            alert("결제 실패");
                        }
                    },
                );
            })
    });

    function verifyPayment(imp_uid, merchant_uid, order_id) {
        fetch("/api/payment/verify", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                orderId: order_id,
                impUid: imp_uid,
                merchantUid: merchant_uid
            })
        }).then(res => {
            if(!res.ok) {
                return res.json().then(e => {
                    throw new Error(e.message);
                })
            }
            return res.json;
        }).then(() => {
            alert("결제가 완료되었습니다.");
            location.href = `/order/${order_id}`;
        }).catch(() => {
            alert("결제 검증에 실패했습니다.");
        })
    }
});