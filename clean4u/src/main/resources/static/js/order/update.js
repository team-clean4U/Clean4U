document.addEventListener("DOMContentLoaded", () => {

    window.removeItem = function (button) {
        const itemWrapper = button.closest(".laundry-card");
        if (itemWrapper) {
            itemWrapper.remove();
            reIndexItems();
        }
    }

    function reIndexItems() {
        const items = document.querySelectorAll(".laundry-card");

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

    const itemsSizeEl = document.getElementById("itemsSize");
    let itemIndex = itemsSizeEl ? Number(itemsSizeEl.value) : 0;

    window.addItem = function () {
        const container = document.querySelector(".order-laundry-item");
        if (!container) {
            console.error("Container not found");
            return;
        }

        const optionHtml = allOptions.map(option => `
            <label>
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
            <div class="laundry-card" data-index="${itemIndex}">
                <div class="laundry-info-header">
                    <span>
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
                    </span>
                    <span>
                        <button class="btn btn-delete"
                                type="button"
                                onclick="removeItem(this)"
                        ><i class="fa-solid fa-trash"></i>삭제</button>
                    </span>
                </div>
                <div class="laundry-info-detail">
                    <div class="laundry-item-row">
                        <span class="laundry-item-label">수량:</span>
                        <input class="laundry-item-value"
                               type="number"
                               name="items[${itemIndex}].quantity"
                               min="1"
                               value="1">
                    </div>
                    <div class="laundry-item-row">
                        <span class="laundry-item-label">옵션</span>
                        <div class="laundry-item-option">
                            ${optionHtml}
                        </div>
                    </div>
                </div>
            </div>
        `;

        const bottomButtons = container.querySelector(".bottom-buttons");
        if (bottomButtons) {
            bottomButtons.insertAdjacentHTML("beforebegin", html);
        } else {
            container.insertAdjacentHTML("beforeend", html);
        }

        const newItemWrapper = container.querySelector(`.laundry-card[data-index="${itemIndex}"] .update-value.category`);

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

const updateForm = document.getElementById("updateForm");
if(updateForm) {
    updateForm.addEventListener("submit",  async (e) => {
        e.preventDefault();

        const orderId = updateForm.dataset.orderId;
        const formData = new FormData(updateForm);

        try {
            const res =  await fetch(`/api/v1/orders/${orderId}`, {
                method: "PUT",
                body: formData
            });

            const data = await res.json();

            if(!res.ok) {
                throw new Error(data.message);
            }
            if(data.data.isGradeChanged) {
                alert("고객 등급이 변경되었습니다.");
            } else {
                alert("주문 내역이 변경되었습니다.");
            }
        } catch (error) {
            alert(error.message);
        }

    });
}
