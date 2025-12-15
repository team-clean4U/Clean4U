document.querySelectorAll('.date-input').forEach(input => {
    input.addEventListener('change', () => {
        input.classList.toggle('has-value', !!input.value);
    });

    if(input.value) {
        input.classList.add('has-value');
    }
});
