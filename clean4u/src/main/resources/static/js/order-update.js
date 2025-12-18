document.addEventListener("DOMContentLoaded", () => {

    function initDropdowns() {
        if (typeof bindDropdown !== 'function') {
            setTimeout(initDropdowns, 10);
            return;
        }

        const statusWrapper = document.querySelector(".update-value.category");
        if (statusWrapper) {
            bindDropdown(statusWrapper);
        }
    }

    initDropdowns();

    const laundryItems = JSON.parse(document.getElementById("laundry-items-data").textContent);

    const allOptions = JSON.parse(document.getElementById("laundry-options-data").textContent);

    let itemIndex = Number(document.getElementById("itemsSize").value);

    window.addItem = function () {
        const container = document.getElementById("items-container");

        const optionHtml = allOptions.map(option => `
            <label style="display:flex; align-items:center; gap:0.3rem;">
                <input type="checkbox"
                       name="items[${itemIndex}].optionIds"
                       value="${option.id}">
                ${option.name} (+${option.extraPrice})
            </label>
        `).join("");

        const laundryItemDropdown = laundryItems.map(item => `
            <li data-id="${item.id}">
                ${item.name} (${item.category})
            </li>
        `).join("");

        const html = `
            <div class="dynamic-item"
                 style="width:100%; padding:1.5rem 2rem; border-bottom:1px dashed var(--color-light-gray);">

                <div class="update-item order-item" style="border-bottom:none;">
                    <div class="update-value category">
                    <input type="hidden" name="items[${itemIndex}].laundryItemId">
                        <div class="category-select">
                            <span class="category-selected">세탁물 선택</span>
                            <i class="fa-solid fa-chevron-down"></i>
                        </div>
                        <ul class="category-dropdown">
                            ${laundryItemDropdown}
                        </ul>
                    </div>

                    <input class="update-value"
                           type="number"
                           name="items[${itemIndex}].quantity"
                           min="1"
                           value="1">
                </div>

                <div style="display:flex; flex-wrap:wrap; gap:0.6rem 1rem;">
                    ${optionHtml}
                </div>
            </div>
        `;

        container.insertAdjacentHTML("beforeend", html);

        const newItemWrapper = container.lastElementChild.querySelector(".update-value.category");

        if (newItemWrapper) {
            if (typeof bindDropdown === 'function') {
                bindDropdown(newItemWrapper);
            } else {
                setTimeout(() => {
                    if (typeof bindDropdown === 'function') {
                        bindDropdown(newItemWrapper);
                    }
                }, 50);
            }
        }

        itemIndex++;
    };
});
