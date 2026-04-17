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

/* Tailwind config moved to fragments/tailwind-config.html */

/* Page-specific JS functions - prefixed */

/* From Acervo.html */
function acervo_filterBooks() {
    filterCardsByFields("bookSearch", ".book-card", [".title-text", ".author-text", ".isbn-text"]);
}
function acervo_openBookModal() {
    openModal("bookModal", "modalContent");
}
function acervo_closeBookModal() {
    closeModal("bookModal", "modalContent");
}
function acervo_bindModal() {
    bindModalCloseOnBackground("bookModal", acervo_closeBookModal);
}

/* From Emprestimos.html */
function emprestimos_filterEmprestimos() {
    filterTextList("topSearch", ".emprestimo-card");
}
function emprestimos_openLoanModal() {
    openModal("loanModal");
    setDateInputsWithOffset("dataRetirada", "dataVencimento", 14);
}
function emprestimos_closeLoanModal() {
    closeModal("loanModal");
}
function emprestimos_bindModal() {
    bindModalCloseOnBackground("loanModal", emprestimos_closeLoanModal);
}

/* From Controle.html */
function controle_filterUsers() {
    filterCardsByFields("userSearch", ".user-card", [".search-name", ".search-email"]);
}
function controle_openUserModal() {
    openModal("userModal");
}
function controle_closeUserModal() {
    closeModal("userModal");
}
function controle_bindModal() {
    bindModalCloseOnBackground("userModal", controle_closeUserModal);
}
function controle_editUser(element) {
    // Existing logic - no change needed as it uses DOM attributes
}
function controle_resetForm() {
    document.getElementById("detailsForm").reset();
    // Clear selections - existing
}

/* From Reservas.html */
function reservas_filterReservas() {
    filterTextList("searchInput", ".reserva-item");
}
function reservas_openReservationModal() {
    openModal("reservaModal");
}
function reservas_closeReservationModal() {
    closeModal("reservaModal");
}
function reservas_bindModal() {
    bindModalCloseOnBackground("reservaModal", reservas_closeReservationModal);
}
function reservas_confirmDelete(id) {
    if (confirm("Deseja realmente cancelar esta reserva?")) {
        window.location.href = "/library/reservas/deletar/" + id;
    }
}

/* Dashboard, Relatorio, error have no JS to move */

