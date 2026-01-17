document.addEventListener("DOMContentLoaded", () => {
    if (window.location.pathname === "/chats") {
        const chatWidgetToggle = document.getElementById("chatWidgetToggle");
        const chatWidget = document.getElementById("chatWidget");
        if (chatWidgetToggle) {
            chatWidgetToggle.style.display = "none";
            chatWidgetToggle.style.visibility = "hidden";
        }
        if (chatWidget) {
            chatWidget.style.display = "none";
            chatWidget.style.visibility = "hidden";
        }
        return;
    }

    const chatWidget = document.getElementById("chatWidget");
    const chatWidgetToggle = document.getElementById("chatWidgetToggle");
    const chatWidgetClose = document.getElementById("chatWidgetClose");
    const chatWidgetForm = document.getElementById("chatWidgetForm");
    const chatWidgetMessages = document.getElementById("chatWidgetMessages");
    const chatWidgetMessage = document.getElementById("chatWidgetMessage");
    const chatWidgetSenderId = document.getElementById("chatWidgetSenderId");

    if (!chatWidget || !chatWidgetToggle || !chatWidgetForm) {
        return;
    }

    let historyLoaded = false;

    function loadHistoryOnce() {
        if (historyLoaded) return;
        historyLoaded = true;

        fetch("/api/v1/chats/history?limit=50")
            .then(res => {
                if (!res.ok) {
                    return res.json().then(e => {
                        const error = new Error(e.message || "채팅 기록을 불러오는데 실패했습니다.");
                        alert(error.message);
                        throw error;
                    });
                }
                return res.json();
            })
            .then(result => {
                const list = result?.data;
                if (!Array.isArray(list)) return;
                chatWidgetMessages.innerHTML = "";
                list.forEach(item => addMessageToWidget(item.sender, item.message));
                scrollToBottom();
            })
            .catch((error) => {
                console.error("채팅 기록 로드 오류:", error);
            });
    }

    function scrollToBottom() {
        const body = document.getElementById("chatWidgetBody");
        if (body) {
            setTimeout(() => {
                body.scrollTop = body.scrollHeight;
            }, 0);
        }
    }

    chatWidgetToggle.addEventListener("click", () => {
        const isHidden = chatWidget.style.display === "none" || chatWidget.style.display === "";
        chatWidget.style.display = isHidden ? "flex" : "none";
        if (isHidden && chatWidgetMessage) {
            loadHistoryOnce();
            chatWidgetMessage.focus();
            setTimeout(() => {
                scrollToBottom();
            }, 100);
        }
    });

    if (chatWidgetClose) {
        chatWidgetClose.addEventListener("click", () => {
            chatWidget.style.display = "none";
        });
    }

    function createMessageElement(sender, message, isMine) {
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

        return msgWrap;
    }

    function addMessageToWidget(sender, message) {
        const currentUserName = chatWidget.getAttribute("data-current-user");
        const isMine = sender === currentUserName;
        const wrap = createMessageElement(sender, message, isMine);
        const body = document.getElementById("chatWidgetBody");

        chatWidgetMessages.appendChild(wrap);
        if (body) {
            body.scrollTop = body.scrollHeight;
        }
    }

    let eventSource = null;

    function connectEventSource() {
        if (eventSource) {
            eventSource.close();
        }

        eventSource = new EventSource("/api/v1/chats");

        eventSource.addEventListener("connect", (event) => {
        });

        eventSource.addEventListener("newMessage", (event) => {
            const data = JSON.parse(event.data);
            addMessageToWidget(data.sender, data.message);
        });

        eventSource.onerror = (error) => {
            eventSource.close();
            setTimeout(connectEventSource, 3000);
        };
    }

    connectEventSource();

    window.addEventListener("beforeunload", () => {
        if (eventSource) {
            eventSource.close();
        }
    });

    chatWidgetForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const senderId = chatWidgetSenderId ? chatWidgetSenderId.value : null;
        const message = chatWidgetMessage.value.trim();

        if (!message) {
            alert("메시지는 반드시 입력해주세요");
            return;
        }

        if (!senderId) {
            console.error("발신자 ID를 찾을 수 없습니다.");
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
                    chatWidgetMessage.value = "";
                    chatWidgetMessage.focus();
                } else {
                    return res.json().then(e => {
                        const error = new Error(e.message || "메시지 전송에 실패했습니다.");
                        alert(error.message);
                        throw error;
                    });
                }
            })
            .catch(err => {
                if (err.message && err.message !== "메시지 전송에 실패했습니다.") {
                    // 이미 alert가 표시되었으므로 추가 처리 불필요
                } else {
                    alert("메시지 전송 중 오류가 발생했습니다.");
                }
            });
    });
});
