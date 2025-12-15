document.addEventListener('DOMContentLoaded', () => {
    const ul = document.querySelector('.update-value.category');
    if (!ul) return;

    const items = ul.querySelectorAll('li');
    if (items.length === 0) return;

    const initial = ul.dataset.selected;
    const matched = initial
        ? Array.from(items).find((li) => li.getAttribute('datatype') === initial)
        : null;

    let selected = matched || items[0];
    selected.classList.add('selected');

    items.forEach((li, idx) => li.style.setProperty('--li-offset', idx));

    ul.addEventListener('click', (e) => {
        if (e.target.tagName !== 'LI') return;

        if (ul.classList.contains('open')) {
            items.forEach((li) => li.classList.remove('selected'));
            e.target.classList.add('selected');
        }

        ul.classList.toggle('open');
    });

    document.addEventListener('click', (e) => {
        if (!ul.contains(e.target)) {
            ul.classList.remove('open');
        }
    });
});