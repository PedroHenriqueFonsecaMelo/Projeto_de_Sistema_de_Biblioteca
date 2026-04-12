function openModal(modalId, contentId) {
    const modal = document.getElementById(modalId);
    if (!modal) return;

    modal.classList.remove("hidden");
    if (contentId) {
        const content = document.getElementById(contentId);
        if (content) {
            content.classList.remove("scale-95", "opacity-0");
            content.classList.add("scale-100", "opacity-100");
        }
    }
    document.body.classList.add("modal-active");
}

function closeModal(modalId, contentId) {
    const modal = document.getElementById(modalId);
    if (!modal) return;

    if (contentId) {
        const content = document.getElementById(contentId);
        if (content) {
            content.classList.remove("scale-100", "opacity-100");
            content.classList.add("scale-95", "opacity-0");
        }
        setTimeout(() => modal.classList.add("hidden"), 300);
    } else {
        modal.classList.add("hidden");
    }
    document.body.classList.remove("modal-active");
}

function bindModalCloseOnBackground(modalId, closeCallback) {
    window.addEventListener("click", function (event) {
        const modal = document.getElementById(modalId);
        if (!modal) return;
        if (event.target === modal) {
            closeCallback();
        }
    });
}

function filterCardsByFields(searchInputId, cardSelector, fieldSelectors) {
    const query = document.getElementById(searchInputId)?.value.toLowerCase() || "";
    const cards = document.querySelectorAll(cardSelector);

    cards.forEach((card) => {
        const matches = fieldSelectors.some((selector) => {
            const field = card.querySelector(selector);
            return field && field.innerText.toLowerCase().includes(query);
        });
        card.style.display = matches ? "" : "none";
    });
}

function filterTextList(inputId, itemSelector) {
    const input = document.getElementById(inputId)?.value.toLowerCase() || "";
    document.querySelectorAll(itemSelector).forEach((item) => {
        item.style.display = item.innerText.toLowerCase().includes(input) ? "" : "none";
    });
}

function setDateInputsWithOffset(retiradaId, vencimentoId, offsetDays) {
    const hoje = new Date().toISOString().split("T")[0];
    const vencimento = new Date();
    vencimento.setDate(vencimento.getDate() + (offsetDays || 0));

    const retirada = document.getElementById(retiradaId);
    const vencimentoInput = document.getElementById(vencimentoId);

    if (retirada) {
        retirada.value = hoje;
    }
    if (vencimentoInput) {
        vencimentoInput.value = vencimento.toISOString().split("T")[0];
    }
}
