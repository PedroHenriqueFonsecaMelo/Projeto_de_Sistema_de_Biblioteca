// --- 1. GENÉRICOS (UI & UTIL) ---

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
        if (event.target === modal) closeCallback();
    });
}

function filterCardsByFields(searchInputId, cardSelector, fieldSelectors) {
    const query = document.getElementById(searchInputId)?.value.toLowerCase() ||
        "";
    document.querySelectorAll(cardSelector).forEach((card) => {
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
        item.style.display = item.innerText.toLowerCase().includes(input)
            ? ""
            : "none";
    });
}

function setDateInputsWithOffset(retiradaId, vencimentoId, offsetDays) {
    const hoje = new Date().toISOString().split("T")[0];
    const vencimento = new Date();
    vencimento.setDate(vencimento.getDate() + (offsetDays || 0));
    if (document.getElementById(retiradaId)) {
        document.getElementById(retiradaId).value = hoje;
    }
    if (document.getElementById(vencimentoId)) {
        document.getElementById(vencimentoId).value =
            vencimento.toISOString().split("T")[0];
    }
}

// --- 2. FUNÇÕES ESPECÍFICAS (API & PÁGINAS) ---

// --- ACERVO (BOOKS) ---
function acervo_filterBooks() {
    filterCardsByFields("bookSearch", ".book-card", [
        ".title-text",
        ".author-text",
        ".isbn-text",
    ]);
}

function acervo_cadastrarLivro(formElement) {
    const formData = new FormData(formElement);
    const data = Object.fromEntries(formData.entries());

    fetch("/api/livros", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    })
        .then((response) => {
            if (response.ok) {
                alert("Livro salvo com sucesso!");
                location.reload();
            } else {
                alert("Erro ao salvar livro. Verifique os dados.");
            }
        });
}

function acervo_removerLivro(id) {
    if (!confirm("Tem certeza que deseja remover este livro do acervo?")) {
        return;
    }

    fetch(`/api/livros/${id}`, {
        method: "DELETE",
    })
        .then((response) => {
            if (response.ok) {
                alert("Livro removido com sucesso!");
                location.reload();
            } else {
                alert("Erro ao remover: " + response.status);
            }
        })
        .catch((error) => console.error("Erro:", error));
}

// --- RESERVAS (RESERVATIONS) ---
function reservas_filterReservas() {
    filterTextList("searchInput", ".reserva-item");
}

function reservas_criarReserva(leitorId, bookId) {
    fetch("/api/reservas", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ leitorId, bookId }),
    })
        .then(async (response) => {
            if (response.ok) {
                location.reload();
            } else {
                // Tenta ler a mensagem de erro enviada pelo Spring
                const errorData = await response.json().catch(() => ({}));
                const mensagem = errorData.message ||
                    "Este livro ainda tem exemplares disponíveis na estante!";
                alert("Atenção: " + mensagem);
            }
        });
}

function reservas_liberarReserva(id) {
    fetch(`/api/reservas/liberar/${id}`, {
        method: "PATCH",
    })
        .then((response) =>
            response.ok ? location.reload() : alert("Erro ao liberar reserva.")
        );
}

function reservas_cancelarReserva(id) {
    if (!confirm("Deseja realmente cancelar esta reserva?")) return;

    fetch(`/api/reservas/${id}`, {
        method: "DELETE",
    })
        .then((response) => {
            if (response.ok) {
                alert("Reserva cancelada!");
                location.reload();
            } else {
                alert("Erro ao cancelar.");
            }
        });
}

// --- TICKETS ---
function tickets_enviarResposta() {
    const id = document.getElementById("modalTicketId").value;
    const resposta = document.getElementById("textoResposta").value;
    if (!resposta) return alert("Preencha a resposta.");

    fetch(
        `/api/tickets/responder/${id}?resposta=${encodeURIComponent(resposta)}`,
        {
            method: "PATCH",
        },
    )
        .then((response) =>
            response.ok
                ? (alert("Ticket respondido!"), location.reload())
                : alert("Erro ao responder.")
        );
}

function tickets_criarTicket(formElement) {
    const formData = new FormData(formElement);
    const data = Object.fromEntries(formData.entries());

    fetch("/api/tickets/registrar", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    })
        .then((response) => {
            if (response.ok) {
                alert("Ticket registrado!");
                location.reload();
            } else {
                alert("Erro ao registrar ticket. Verifique os dados.");
            }
        });
}

function tickets_openTicketModal() {
    const modal = document.getElementById("ticketModal");
    const content = document.getElementById("ticketModalContent");
    modal.classList.remove("hidden");
    modal.classList.add("flex");
    setTimeout(() => {
        content.classList.remove("scale-95", "opacity-0");
        content.classList.add("scale-100", "opacity-100");
    }, 10);
}

function tickets_closeTicketModal() {
    const modal = document.getElementById("ticketModal");
    const content = document.getElementById("ticketModalContent");
    content.classList.remove("scale-100", "opacity-100");
    content.classList.add("scale-95", "opacity-0");
    setTimeout(() => {
        modal.classList.add("hidden");
        modal.classList.remove("flex");
    }, 200);
}
function tickets_prepararResposta(btn) {
    const id = btn.getAttribute("data-id");
    const assunto = btn.getAttribute("data-assunto");

    document.getElementById("modalTicketId").value = id;
    document.getElementById("modalAssunto").innerText = "Assunto: " +
        assunto;

    document.getElementById("modalResposta").classList.remove("hidden");
    document.getElementById("modalResposta").classList.add("flex");
}

// --- EMPRÉSTIMOS (LOANS) ---

function emprestimos_salvar(formElement) {
    const formData = new FormData(formElement);
    const data = Object.fromEntries(formData.entries());

    fetch("/api/emprestimos", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    })
        .then((response) => {
            if (response.ok) {
                alert("Empréstimo registrado!");
                location.reload();
            } else {
                alert(
                    "Erro ao registrar empréstimo. Verifique se o livro está disponível.",
                );
            }
        });
}

function emprestimos_devolver(id) {
    if (!confirm("Confirmar devolução do exemplar?")) return;

    fetch(`/api/emprestimos/devolver/${id}`, {
        method: "POST",
    })
        .then((response) => {
            if (response.ok) {
                location.reload();
            } else {
                alert("Erro ao processar devolução.");
            }
        })
        .catch((error) => console.error("Erro:", error));
}

function emprestimos_renovar(id) {
    fetch(`/api/emprestimos/renovar/${id}`, {
        method: "POST",
    })
        .then((response) => {
            if (response.ok) {
                alert("Prazo renovado com sucesso!");
                location.reload();
            } else {
                alert(
                    "Não foi possível renovar (verifique se há reservas para este livro).",
                );
            }
        });
}

// --- 3. INICIALIZAÇÃO (DOMContentLoaded) ---

document.addEventListener("DOMContentLoaded", function () {
    // Inicialização Acervo
    if (document.getElementById("bookSearch")) {
        document.getElementById("bookSearch").addEventListener(
            "keyup",
            acervo_filterBooks,
        );
        bindModalCloseOnBackground(
            "bookModal",
            () => closeModal("bookModal", "modalContent"),
        );
    }

    // Inicialização Empréstimos
    if (document.getElementById("topSearch")) {
        document.getElementById("topSearch").addEventListener(
            "keyup",
            () => filterTextList("topSearch", ".emprestimo-card"),
        );
        bindModalCloseOnBackground(
            "loanModal",
            () => closeModal("loanModal", "loanModalContent"),
        );
    }

    // Inicialização Reservas
    if (document.getElementById("searchInput")) {
        document.getElementById("searchInput").addEventListener(
            "keyup",
            reservas_filterReservas,
        );
        bindModalCloseOnBackground(
            "reservaModal",
            () => closeModal("reservaModal", "modalContent"),
        );
    }

    // Inicialização Usuários
    if (document.getElementById("userSearch")) {
        document.getElementById("userSearch").addEventListener(
            "keyup",
            () =>
                filterCardsByFields("userSearch", ".user-card", [
                    ".search-name",
                    ".search-email",
                ]),
        );
        bindModalCloseOnBackground("userModal", () => closeModal("userModal"));
    }

    // Inicialização Tickets
    if (document.getElementById("ticketSearch")) {
        document.getElementById("ticketSearch").addEventListener(
            "keyup",
            () =>
                filterCardsByFields("ticketSearch", ".ticket-card", [
                    ".search-assunto",
                    ".search-leitor",
                ]),
        );
        bindModalCloseOnBackground(
            "ticketModal",
            () => closeModal("ticketModal", "ticketModalContent"),
        );
    }
});
