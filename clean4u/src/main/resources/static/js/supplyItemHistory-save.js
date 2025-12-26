document.addEventListener("DOMContentLoaded", () => {
    const dataEl = document.getElementById("supply-items-data");
    if (!dataEl) return;

    const supplyItems = JSON.parse(dataEl.textContent);
    let itemIndex = 0;

    window.addSupplyItem = function () {
        const container = document.querySelector(".supply-item-history-items");
        if (!container) {
            console.error("Container not found");
            return;
        }

        const supplyItemDropdown = supplyItems.filter(i => i.isActive)
            .map(i => `
            <li data-id="${i.id}">
            ${i.name} (재고: ${i.stockQuantity}${i.unit ? i.unit : ''})
            </li>
`).join("");

        const html = `
            <div class="supply-item-card" data-index="${itemIndex}">
                <div class="supply-item-row">
                    <div class="supply-item-select-wrapper">
                    <label>자재명</label>
                        <div class="update-value category">
                            <input type="hidden" name="items[${itemIndex}].supplyItemId">
                            <div class="category-select">
                                <span class="category-selected">자재 선택</span>
                                <i class="fa-solid fa-chevron-down"></i>
                            </div>
                            <ul class="category-dropdown">
                                ${supplyItemDropdown}
                            </ul>
                        </div>
                    </div>
                    <div class="supply-item-select-wrapper">
                        <label>수량</label>
                        <input type="number" name="items[${itemIndex}].quantity" min="1" step="1" value="1" required>
                    </div>
                    <button class="btn btn-delete" type="button" onclick="removeSupplyItem(this)">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </div>
            </div>
        `;

        const bottomButtons = container.querySelector(".bottom-buttons");
        if (bottomButtons) {
            bottomButtons.insertAdjacentHTML("beforebegin", html);
        } else {
            container.insertAdjacentHTML("beforeend", html);
        }

        const newItemWrapper = container.querySelector(`.supply-item-card[data-index="${itemIndex}"] .update-value.category`);
        if (newItemWrapper && typeof bindDropdown === 'function') {
            bindDropdown(newItemWrapper);
        }

        itemIndex++;
    };

    window.removeSupplyItem = function (button) {
        const itemCard = button.closest(".supply-item-card");
        if (itemCard) {
            itemCard.remove();
            reIndexItems();
        }
    };

    function reIndexItems() {
        const items = document.querySelectorAll(".supply-item-card");
        items.forEach((item, newIndex) => {
            item.dataset.index = newIndex;
            item.querySelectorAll("input, select").forEach(el => {
                if (el.name) {
                    el.name = el.name.replace(/items\[\d+]/, `items[${newIndex}]`);
                }
            });
        });
    }

    addSupplyItem();
})