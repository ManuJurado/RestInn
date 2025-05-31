export class CustomDatepicker {
  constructor(inputId, pickerId, options = {}) {
    this.input = document.getElementById(inputId);
    this.picker = document.getElementById(pickerId);
    this.current = new Date();
    this.options = options;

    if (options.backgroundUrl) {
      this.picker.style.backgroundImage = `url('${options.backgroundUrl}')`;
    }

    this.build();
    this.attachEvents();
  }

  build() {
    // crea la cabecera y rejilla pero no la añade aún
    this.picker.innerHTML = '';
    const header = document.createElement('div');
    header.className = 'header';
    this.btnPrev = document.createElement('button');
    this.btnPrev.textContent = '<';
    this.titleEl = document.createElement('div');
    this.btnNext = document.createElement('button');
    this.btnNext.textContent = '>';
    header.append(this.btnPrev, this.titleEl, this.btnNext);
    this.picker.append(header);

    this.daysContainer = document.createElement('div');
    this.daysContainer.className = 'days';
    this.picker.append(this.daysContainer);
  }

  render() {
    // header
    this.titleEl.textContent = `${this.current.toLocaleString('default',{month:'long'})} ${this.current.getFullYear()}`;

    // reset días
    this.daysContainer.innerHTML = '';
    ['L','M','X','J','V','S','D'].forEach(d => {
      const cell = document.createElement('div');
      cell.className = 'day';
      cell.textContent = d;
      this.daysContainer.append(cell);
    });

    // offset primer día
    const firstDay = new Date(this.current.getFullYear(), this.current.getMonth(), 1).getDay();
    const offset = (firstDay + 6) % 7;
    for (let i = 0; i < offset; i++) {
      this.daysContainer.appendChild(document.createElement('div'));
    }

    // días del mes
    const daysInMonth = new Date(this.current.getFullYear(), this.current.getMonth()+1, 0).getDate();
    for (let d = 1; d <= daysInMonth; d++) {
      const cell = document.createElement('div');
      cell.className = 'day';
      cell.textContent = d;
      cell.addEventListener('click', () => this.select(d));
      this.daysContainer.append(cell);
    }

    // eventos de navegación
    this.btnPrev.onclick = () => { this.current.setMonth(this.current.getMonth()-1); this.render(); };
    this.btnNext.onclick = () => { this.current.setMonth(this.current.getMonth()+1); this.render(); };
  }

  attachEvents() {
    this.input.addEventListener('click', e => {
      const rect = this.input.getBoundingClientRect();
      this.picker.style.top = `${rect.bottom + window.scrollY}px`;
      this.picker.style.left = `${rect.left + window.scrollX}px`;
      this.render();
      this.picker.style.display = 'block';
    });
    document.addEventListener('click', e => {
      if (!this.picker.contains(e.target) && e.target !== this.input) {
        this.picker.style.display = 'none';
      }
    });
    document.addEventListener('keydown', e => {
      if (e.key === 'Escape') this.picker.style.display = 'none';
    });
  }

  select(day) {
    const sel = new Date(this.current.getFullYear(), this.current.getMonth(), day);
    this.input.value = sel.toISOString().split('T')[0];
    this.picker.style.display = 'none';
    if (this.options.onSelect) this.options.onSelect(sel);
  }
}
