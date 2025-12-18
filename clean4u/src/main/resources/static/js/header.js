document.addEventListener("DOMContentLoaded", function () {
    const currentPath = window.location.pathname;

    const navLinks = document.querySelectorAll('.nav-link');

    navLinks.forEach(link => {
        const href = link.getAttribute('href');

        if (!href || href === '#') {
            return;
        }

        if (!href.startsWith('/')) {
            return;
        }

        const linkPath = href;
        let isActive = false;

        if (currentPath === linkPath || currentPath === linkPath + '/') {
            isActive = true;
        } else if (currentPath.startsWith(linkPath + '/') && linkPath !== '/') {
            isActive = true;
        } else if (linkPath === '/' && (currentPath === '/' || currentPath === '')) {
            isActive = true;
        }

        if (isActive) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    })
})