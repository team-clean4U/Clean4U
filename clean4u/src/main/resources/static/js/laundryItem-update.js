document.addEventListener("DOMContentLoaded", () => {
    const categorySelect = document.querySelector(".category-select");
    const categoryDropdown = document.querySelector(".category-dropdown");
    const categoryInput = document.getElementById("category");
    const categorySelected = document.querySelector(".category-selected");
    const categoryItems = document.querySelectorAll(".category-dropdown li");

    if (categorySelect && categoryDropdown && categoryInput) {
        const initialSelected = categoryInput.value;
        if (initialSelected) {
            categoryItems.forEach(li => {
                if (li.dataset.type === initialSelected) {
                    li.classList.add("selected");
                }
            });
        }

        categorySelect.addEventListener("click", (e) => {
            e.stopPropagation();
            categorySelected.classList.toggle("active");
            categoryDropdown.classList.toggle("active");
        });

        categoryItems.forEach(li => {
            li.addEventListener("click", (e) => {
                e.stopPropagation();
                const selectedType = li.dataset.type;
                const selectedText = li.textContent;

                categoryInput.value = selectedType;
                categorySelected.textContent = selectedText;
                categoryDropdown.setAttribute("data-selected", selectedType);

                categoryItems.forEach(i => {
                    i.classList.remove("selected");
                });
                li.classList.add("selected");

                categorySelect.classList.remove("active");
                categoryDropdown.classList.remove("active");
            });
        });
    }
});