function toggleLowstockDropdown() {
    const dropdown = document.getElementById('lowStockDropdown');
    if (dropdown) {
        const isHidden = dropdown.style.display === 'none' || dropdown.style.display === '';
        dropdown.style.display = isHidden ? 'block' : 'none';
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const bell = document.getElementById('lowStockBell');
    const dropdown = document.getElementById('lowStockDropdown');
    
    if (bell && dropdown) {
        bell.addEventListener('click', function(e) {
            e.stopPropagation();
            toggleLowstockDropdown();
        });
        
        document.addEventListener('click', function(event) {
            if (!dropdown.contains(event.target) && !bell.contains(event.target)) {
                dropdown.style.display = 'none';
            }
        });
    }
});
