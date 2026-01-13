document.addEventListener("DOMContentLoaded", () => {
   const rows = document.querySelectorAll(".payment-row");
   if(!rows) return;

   rows.forEach(row => {
       row.addEventListener("click", () => {
           const paymentId = row.dataset.id;
           const orderId = row.dataset.orderId;
           if(!paymentId || !orderId) return;

           location.href = `/orders/${orderId}`;
       })
   });
});