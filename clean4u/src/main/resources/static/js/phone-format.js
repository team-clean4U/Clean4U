function formatPhoneNumber(phone) {
    if (!phone) return '';
    const cleaned = phone.replace(/[^0-9]/g, '');
    if (cleaned.length <= 3) return cleaned;
    if (cleaned.length <= 7) return cleaned.slice(0, 3) + '-' + cleaned.slice(3);
    if (cleaned.length === 11) return cleaned.slice(0, 3) + '-' + cleaned.slice(3, 7) + '-' + cleaned.slice(7);
    if (cleaned.length === 10) return cleaned.slice(0, 3) + '-' + cleaned.slice(3, 6) + '-' + cleaned.slice(6);
    return cleaned.slice(0, 3) + '-' + cleaned.slice(3, 7) + '-' + cleaned.slice(7, 11);
}

function initPhoneFormat(el) {
    if (!el) return;
    el.addEventListener('input', e => {
        e.target.value = formatPhoneNumber(e.target.value);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const phone = document.getElementById('phone');
    if (phone) initPhoneFormat(phone);
    document.querySelectorAll('.phone-input').forEach(initPhoneFormat);
    const formatted = document.getElementById('formatted-phone');
    if (formatted) formatted.textContent = formatPhoneNumber(formatted.textContent);
});
