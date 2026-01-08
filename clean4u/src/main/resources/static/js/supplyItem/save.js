document.addEventListener("DOMContentLoaded", () => {
    let itemIndex = 0;

    window.addSupplyItem = function () {
        const container = document.querySelector(".supply-item-history-items");
        if (!container) {
            console.error("Container not found");
            return;
        }

        const html = `
            <div class="supply-item-card" data-index="${itemIndex}">
                <div class="supply-item-inner">
                    <div class="supply-item-select-wrapper">
                        <label>비품명</label>
                        <input type="text" name="items[${itemIndex}].name" placeholder="비품 이름을 입력해주세요." required>
                    </div>
                    <div class="supply-item-select-wrapper">
                        <label>단위</label>
                        <input type="text" name="items[${itemIndex}].unit" placeholder="단위를 입력해주세요." required>
                    </div>
                    <div class="supply-item-select-wrapper">
                        <label>재고 수량</label>
                        <input type="number" name="items[${itemIndex}].stockQuantity" min="0" step="1" value="0" required>
                    </div>
                    <div class="supply-item-select-wrapper">
                        <label>안전 재고</label>
                        <input type="number" name="items[${itemIndex}].safetyStock" min="0" step="1" value="0" required>
                    </div>
                    <div class="supply-item-select-wrapper">
                        <label>활성 여부</label>
                        <input type="hidden" value="false" id="hidden-isActive-${itemIndex}">
                        <label class="toggle-switch">
                            <input type="checkbox" name="items[${itemIndex}].isActive" value="true" checked id="checkbox-isActive-${itemIndex}" onchange="updateIsActive(${itemIndex}, this.checked)">
                            <span class="slider"></span>
                        </label>
                    </div>
                    <div class="supply-item-delete-wrapper">
                        <button class="btn btn-delete" type="button" onclick="removeSupplyItem(this)">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        `;

        const bottomButtons = container.querySelector(".bottom-buttons");
        bottomButtons ? bottomButtons.insertAdjacentHTML("beforebegin", html) : container.insertAdjacentHTML("beforeend", html);
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
        document.querySelectorAll(".supply-item-card").forEach((item, newIndex) => {
            item.dataset.index = newIndex;
            item.querySelectorAll("input, select").forEach(el => {
                if (el.name) el.name = el.name.replace(/items\[\d+]/, `items[${newIndex}]`);
            });
            const hiddenInput = item.querySelector(`input[type="hidden"][id^="hidden-isActive-"]`);
            const checkbox = item.querySelector(`input[type="checkbox"][id^="checkbox-isActive-"]`);
            if (hiddenInput) hiddenInput.id = `hidden-isActive-${newIndex}`;
            if (checkbox) {
                checkbox.id = `checkbox-isActive-${newIndex}`;
                checkbox.setAttribute('onchange', `updateIsActive(${newIndex}, this.checked)`);
                updateIsActive(newIndex, checkbox.checked);
            }
        });
    }

    window.updateIsActive = function(index, isChecked) {
        const hiddenInput = document.getElementById(`hidden-isActive-${index}`);
        const checkbox = document.getElementById(`checkbox-isActive-${index}`);
        if (hiddenInput && checkbox) {
            if (isChecked) {
                checkbox.setAttribute('name', `items[${index}].isActive`);
                hiddenInput.removeAttribute('name');
            } else {
                checkbox.removeAttribute('name');
                hiddenInput.setAttribute('name', `items[${index}].isActive`);
            }
        }
    };

    addSupplyItem();
});