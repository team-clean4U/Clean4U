async function cancelOrder(orderId) {
    if(!confirm("주문 접수를 취소하시겠습니까?")) {
        return;
    }

    try {
        const response = await fetch(`/api/v1/orders/${orderId}`, {
            method: "DELETE"
        });

        if(!response.ok) {
            const errorBody = await response.json();
            throw new Error(errorBody.message);
        }
        alert("주문이 취소되었습니다.");
        location.href = "/orders";

    } catch (error) {
        console.log("Error: ", error);
        alert("접수 취소(삭제) 중 오류가 발생했습니다.");
    }
}

async function deleteImage(orderId) {
    if(!confirm("선택된 이미지 파일을 삭제하시겠습니까?")) {
        return;
    }
    try {
        const response = await fetch(`/api/v1/orders/${orderId}/laundry-image`, {
            method: "DELETE"
        });
        if(!response.ok) {
            const errorBody = await response.json();
            throw new Error(errorBody.message);
        }
        location.reload();

    }  catch (error) {
        console.error("Error:", error);
        alert(error.message);
    }
}

async function deleteOrder(orderId) {
    if(!confirm("주문내역이 완전히 삭제됩니다. 삭제하시겠습니까?")) {
        return;
    }
    try {
        const response = await fetch(`/api/v1/orders/${orderId}?hardDelete=true`, {
            method: "DELETE"
        });
        if(!response.ok) {
            const errorBody = await response.json();
            throw new Error(errorBody.message);
        }
        alert("주문이 삭제되었습니다.");
        location.href = "/orders";
    } catch (error) {
        console.error("Error:", error);
        alert(error.message);
    }
}