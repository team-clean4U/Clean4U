document.addEventListener("DOMContentLoaded", () => {

    window.removeItem =function (button) {
        const itemWrapper = button.closest(".update-item-list");
        itemWrapper.remove();

        reIndexItems();
    }

    function reIndexItems() {
        const items = document.querySelectorAll(".update-item-list");

        items.forEach((item, newIndex) => {
            item.dataset.index = newIndex;

            item.querySelectorAll("input, select").forEach(el => {
                if (el.name) {
                    el.name = el.name.replace(/items\[\d+]/, `items[${newIndex}]`);
                }
            });
        });
    }

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
            <div class="update-item-list"
                  style="width:100%;
                  padding: 1.5rem 2rem;
                  border-bottom:1px dashed var(--color-light-gray); 
                  display: flex; 
                  justify-content: space-between;
                  flex-direction: column" 
            >
                <div style="display: flex; justify-content: space-between; gap: 1rem;">
                    <div class="update-item order-item"
                        style="padding-top: 0; border-bottom: none; display: flex; align-items: center; width: 90%;">
                        <div class="update-value category" style="flex: 1;">
                        <input type="hidden" name="items[${itemIndex}].laundryItemId">
                            <div class="category-select"">
                                <span class="category-selected"">세탁물 선택</span>
                                <i class="fa-solid fa-chevron-down"></i>
                            </div>
                            <ul class="category-dropdown">
                                ${laundryItemDropdown}
                            </ul>
                        </div>
    
                        <input 
                               style="width: 90px; margin-left: 1rem;"
                               class="update-value"
                               type="number"
                               name="items[${itemIndex}].quantity"
                               min="1"
                               value="1">
                    </div>
                    <div>
                        <button class="btn btn-delete" 
                                style="
                                    margin-top: 6px;
                                    height: 36px;
                                    padding: 0 12px;
                                    font-size: 14px;
                                    display: flex;
                                    align-items: center;
                                    gap: 6px;
                                    flex-shrink: 0;"
                                onclick="removeItem(this)"
                        ><i class="fa-solid fa-trash"></i>삭제</button>
                    </div>
                </div>
                <div style="
                        display: flex;
                        flex-direction: column;
                        gap: 0.75rem;
                        padding: 1rem 0;"
                    >
                    <span class="option-label" style="width: fit-content">옵션</span>
                    <div class="option-list" style="display: flex;flex-wrap: wrap; gap: 0.6rem 1rem;">
                    ${optionHtml}
                    </div>
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
