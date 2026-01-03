document.addEventListener("DOMContentLoaded", () => {
    const impKey = 'imp47240471';
    const paymentBtn = document.getElementById("paymentBtn");
    const amount = Number(document.getElementById("amount").textContent.replace(/[^0-9]/g, ""));
    const orderId = Number(document.getElementById("orderId").value);

    paymentBtn.addEventListener('click', () => {
        console.log(orderId);
        console.log(amount);
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
               console.log("data object 확인: " + data.amount);
               const merchantUid = data.merchantUid;
               const payAmount = data.amount;
               const IMP = window.IMP;

               IMP.init("imp47240471");

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
                           alert("포트원 결제 성공, 우리 서버 검증 필요");
                       } else {
                           alert("결제 실패");
                       }
                   },
               );
           })
    });
});