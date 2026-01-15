const chatContainer = document.getElementById("chatContainer");
const chatForm = document.getElementById("chatForm");
const chatBox = document.getElementById("chatBox");

window.addEventListener("load", () => {
    chatContainer.scrollTop = chatContainer.scrollHeight;
});

const eventSource = new EventSource("/api/v1/chats");
window.addEventListener("beforeunload", () => {
    eventSource.close();
});

eventSource.addEventListener("newMessage", (event) => {
    const data = JSON.parse(event.data);
    const sender = data.sender;
    const message = data.message;
    const currentUserName = chatContainer.getAttribute("data-current-user");
    const isMine = sender === currentUserName;

    const msgWrap = document.createElement("div");
    msgWrap.className = isMine ? "msg-wrap msg-mine" : "msg-wrap msg-other";

    const msgBox = document.createElement("div");
    msgBox.className = "msg-box";

    const nameSpan = document.createElement("span");
    nameSpan.className = "name";
    const icon = document.createElement("i");
    icon.className = "fa-solid fa-user";
    nameSpan.appendChild(icon);
    nameSpan.appendChild(document.createTextNode(sender));

    const msgSpan = document.createElement("span");
    msgSpan.className = "msg";
    msgSpan.textContent = message;

    msgBox.appendChild(nameSpan);
    msgBox.appendChild(msgSpan);
    msgWrap.appendChild(msgBox);
    chatBox.appendChild(msgWrap);
    chatContainer.scrollTop = chatContainer.scrollHeight;
});

chatForm.addEventListener("submit", function (e) {
    e.preventDefault();

    const senderIdHidden = document.getElementById("senderId");
    const messageInput = document.getElementById("message");

    const senderId = senderIdHidden.value;
    const message = messageInput.value;

    if (!message.trim()) {
        alert("메세지는 반드시 입력해주세요");
        return;
    }

    fetch("/api/v1/chats", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },

        body: new URLSearchParams({
            employeeId: senderId,
            message: message
        })
    })
        .then(res => {
            if (res.ok) {
                messageInput.value = "";
                messageInput.focus();
            } else {
                alert("메시지 전송 중 오류가 발생했습니다.");
            }
        })
        .catch(error => {
            alert(error.message);
        });
});