function openImageModal(src) {
    const modal = document.getElementById("imageModal");
    const img = document.getElementById("modalImage");

    img.src = src;
    modal.style.display = 'flex';
}
function closeImageModal() {
    document.getElementById("imageModal").style.display = 'none';
}