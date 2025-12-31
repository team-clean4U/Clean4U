function copyReviewLink(button, link) {
    const fullUrl = window.location.origin + link;

    if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(fullUrl).then(() => {
            showCopyFeedback(button);
        }).catch(() => {
            fallbackCopy(fullUrl, button);
        });
    } else {
        fallbackCopy(fullUrl, button);
    }
}

function fallbackCopy(text, button) {
    const textarea = document.createElement('textarea');
    textarea.value = text;
    textarea.style.position = 'fixed';
    textarea.style.opacity = '0';
    document.body.appendChild(textarea);
    textarea.select();
    document.execCommand('copy');
    document.body.removeChild(textarea);
    showCopyFeedback(button);
}

function showCopyFeedback(button) {
    if (button) {
        const originalText = button.innerHTML;
        const successColor = getComputedStyle(document.documentElement).getPropertyValue('--icon-success').trim();
        button.innerHTML = '<i class="fa-solid fa-check"></i> 복사됨';
        button.style.backgroundColor = successColor;

        setTimeout(() => {
            button.innerHTML = originalText;
            button.style.backgroundColor = '';
        }, 2000);
    }
}

function openImageModal(src) {
    const modal = document.getElementById("imageModal");
    const img = document.getElementById("modalImage");

    img.src = src;
    modal.style.display = 'flex';
}
function closeImageModal() {
    document.getElementById("imageModal").style.display = 'none';
}