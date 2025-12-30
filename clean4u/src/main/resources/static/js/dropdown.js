function bindDropdown(wrapper) {
    const categorySelect = wrapper.querySelector(".category-select");
    const categoryDropdown = wrapper.querySelector(".category-dropdown");
    const categorySelected = wrapper.querySelector(".category-selected");

    const categoryInput = wrapper.querySelector("input[type='hidden']") ||
        wrapper.querySelector("input[name*='supplyItemId']") ||
        wrapper.querySelector("input[name='status']") ||
        wrapper.querySelector("input[name='category']") ||
        wrapper.querySelector("input[name='isActive']") ||
        wrapper.querySelector("input[name='rating']") ||
        document.getElementById("category");

    if (!categorySelect || !categoryDropdown || !categorySelected) return;

    if (categorySelect.hasAttribute('data-bound')) {
        return;
    }
    categorySelect.setAttribute('data-bound', 'true');

    const categoryItems = categoryDropdown.querySelectorAll("li");

    if (categoryInput && categoryInput.value) {
        const initialSelected = categoryInput.value;
        categoryItems.forEach(li => {
            if (li.dataset.type === initialSelected || li.dataset.id === initialSelected) {
                li.classList.add("selected");
                categorySelected.textContent = li.textContent;
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
            const selectedId = li.dataset.id;
            const selectedText = li.textContent;

            if (categoryInput) {
                if (selectedType !== undefined) {
                    if (selectedType === "") {
                        categoryInput.removeAttribute("value");
                    } else {
                        categoryInput.value = selectedType;
                    }
                } else if (selectedId) {
                    categoryInput.value = selectedId;
                }
            }

            categorySelected.textContent = selectedText;

            if (selectedType !== undefined) {
                if (selectedType === "") {
                    categoryDropdown.removeAttribute("data-selected");
                } else {
                    categoryDropdown.setAttribute("data-selected", selectedType);
                }
            }

            categoryItems.forEach(i => {
                i.classList.remove("selected");
            });
            li.classList.add("selected");

            categorySelect.classList.remove("active");
            categoryDropdown.classList.remove("active");
        });
    });
}

document.addEventListener("click", (e) => {
    const clickedSelect = e.target.closest(".category-select");
    const clickedDropdown = e.target.closest(".category-dropdown");

    if (clickedDropdown || clickedSelect) {
        return;
    }

    document.querySelectorAll(".category-dropdown.active")
        .forEach(dropdown => {
            dropdown.classList.remove("active");
            const wrapper = dropdown.closest(".update-value.category");
            if (wrapper) {
                const select = wrapper.querySelector(".category-select");
                if (select) {
                    select.classList.remove("active");
                }
            }
        });
});

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".update-value.category").forEach(wrapper => {
        bindDropdown(wrapper);
    });
});