/* ============================================================
   COMPONENTE: SEARCHABLE SELECT (combo com busca)
   Salvar como: src/main/resources/static/js/searchable-select.js

   COMO USAR no HTML:
   <div class="ss-wrapper"
        data-ss-name="cliente.id"        <- name do campo que o form envia
        data-ss-placeholder="Buscar cliente...">
     <input type="text" class="ss-input" autocomplete="off" />
     <input type="hidden" name="cliente.id" />   <- valor real enviado
     <div class="ss-dropdown"></div>
     <!-- as opções vêm de um <script type="application/json"> ao lado -->
   </div>

   Os dados das opções são lidos de um JSON adjacente.
   Ver exemplo no cadastro-veiculo.
   ============================================================ */

(function () {
  'use strict';

  // Inicializa todos os componentes ss-wrapper da página
  document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.ss-wrapper[data-ss-options]').forEach(initSearchableSelect);
  });

  function initSearchableSelect(wrapper) {
    const input    = wrapper.querySelector('.ss-input');
    const hidden   = wrapper.querySelector('input[type="hidden"]');
    const dropdown = wrapper.querySelector('.ss-dropdown');

    // Lê as opções do atributo data-ss-options (JSON: [{id, texto}, ...])
    let opcoes = [];
    try {
      opcoes = JSON.parse(wrapper.getAttribute('data-ss-options') || '[]');
    } catch (e) {
      console.error('searchable-select: JSON inválido em data-ss-options', e);
    }

    const obrigatorio = wrapper.getAttribute('data-ss-required') === 'true';
    let highlightedIndex = -1;
    let opcoesFiltradas = opcoes.slice();

    // Se já houver um valor pré-selecionado (ex: edição), mostra o texto
    const valorInicial = hidden.value;
    if (valorInicial) {
      const sel = opcoes.find(o => String(o.id) === String(valorInicial));
      if (sel) input.value = sel.texto;
    }

    // ── Renderiza a lista filtrada ──────────────────────────────
    function renderLista(termo) {
      const termoLower = (termo || '').toLowerCase().trim();

      opcoesFiltradas = termoLower
        ? opcoes.filter(o => o.texto.toLowerCase().includes(termoLower))
        : opcoes.slice();

      if (opcoesFiltradas.length === 0) {
        dropdown.innerHTML = '<div class="ss-empty">Nenhum resultado encontrado</div>';
        return;
      }

      dropdown.innerHTML = opcoesFiltradas.map((o, i) => {
        const textoDestacado = termoLower
          ? destacar(o.texto, termoLower)
          : o.texto;
        const isSelected = String(o.id) === String(hidden.value);
        return `<div class="ss-option${isSelected ? ' selected' : ''}"
                     data-id="${escapeHtml(String(o.id))}"
                     data-index="${i}">${textoDestacado}</div>`;
      }).join('');

      // Liga clique em cada opção
      dropdown.querySelectorAll('.ss-option').forEach(opt => {
        opt.addEventListener('mousedown', function (e) {
          e.preventDefault(); // impede o blur antes do clique registrar
          selecionar(this.getAttribute('data-id'));
        });
      });
    }

    // ── Seleciona uma opção ─────────────────────────────────────
    function selecionar(id) {
      const opcao = opcoes.find(o => String(o.id) === String(id));
      if (opcao) {
        hidden.value = opcao.id;
        input.value  = opcao.texto;
        input.setCustomValidity(''); // limpa erro de validação
      }
      fecharDropdown();

      // Dispara callback opcional (ex: filtrar veículos do cliente)
      const onselect = wrapper.getAttribute('data-ss-onselect');
      if (onselect && typeof window[onselect] === 'function') {
        window[onselect](hidden.value);
      }
    }

    function abrirDropdown() {
      renderLista(input.value === obterTextoSelecionado() ? '' : input.value);
      dropdown.classList.add('open');
    }

    function fecharDropdown() {
      dropdown.classList.remove('open');
      highlightedIndex = -1;
      // Se o texto digitado não corresponde a nenhuma seleção válida, restaura
      const selecionado = opcoes.find(o => String(o.id) === String(hidden.value));
      if (selecionado) {
        input.value = selecionado.texto;
      } else if (!input.value.trim()) {
        hidden.value = '';
      }
      validar();
    }

    function obterTextoSelecionado() {
      const sel = opcoes.find(o => String(o.id) === String(hidden.value));
      return sel ? sel.texto : '';
    }

    function validar() {
      if (obrigatorio && !hidden.value) {
        input.setCustomValidity('Selecione uma opção da lista.');
      } else {
        input.setCustomValidity('');
      }
    }

    // ── Eventos ─────────────────────────────────────────────────
    input.addEventListener('focus', abrirDropdown);

    input.addEventListener('input', function () {
      hidden.value = ''; // ao digitar, limpa seleção até escolher de novo
      renderLista(input.value);
      dropdown.classList.add('open');
      highlightedIndex = -1;

      // Se limpou o campo, dispara callback com vazio (reseta dependências)
      const onselect = wrapper.getAttribute('data-ss-onselect');
      if (onselect && typeof window[onselect] === 'function') {
        window[onselect]('');
      }
    });

    input.addEventListener('keydown', function (e) {
      const opts = dropdown.querySelectorAll('.ss-option');
      if (e.key === 'ArrowDown') {
        e.preventDefault();
        highlightedIndex = Math.min(highlightedIndex + 1, opts.length - 1);
        atualizarHighlight(opts);
      } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        highlightedIndex = Math.max(highlightedIndex - 1, 0);
        atualizarHighlight(opts);
      } else if (e.key === 'Enter') {
        if (highlightedIndex >= 0 && opts[highlightedIndex]) {
          e.preventDefault();
          selecionar(opts[highlightedIndex].getAttribute('data-id'));
        }
      } else if (e.key === 'Escape') {
        fecharDropdown();
      }
    });

    // Fecha ao clicar fora
    document.addEventListener('click', function (e) {
      if (!wrapper.contains(e.target)) {
        fecharDropdown();
      }
    });

    function atualizarHighlight(opts) {
      opts.forEach((o, i) => o.classList.toggle('highlighted', i === highlightedIndex));
      if (opts[highlightedIndex]) {
        opts[highlightedIndex].scrollIntoView({ block: 'nearest' });
      }
    }

    // Validação inicial
    validar();
  }

  // ── Utilitários ───────────────────────────────────────────────
  function destacar(texto, termo) {
    const idx = texto.toLowerCase().indexOf(termo);
    if (idx === -1) return escapeHtml(texto);
    const antes  = escapeHtml(texto.substring(0, idx));
    const match  = escapeHtml(texto.substring(idx, idx + termo.length));
    const depois = escapeHtml(texto.substring(idx + termo.length));
    return antes + '<mark>' + match + '</mark>' + depois;
  }

  function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
  }

})();