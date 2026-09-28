// Small helpers: confirm before destructive actions, auto-apply filter dropdowns.
document.addEventListener('submit', function (event) {
    var message = event.target.getAttribute('data-confirm');
    if (message && !window.confirm(message)) {
        event.preventDefault();
    }
});

document.addEventListener('change', function (event) {
    if (event.target.hasAttribute('data-autosubmit') && event.target.form) {
        event.target.form.submit();
    }
});
