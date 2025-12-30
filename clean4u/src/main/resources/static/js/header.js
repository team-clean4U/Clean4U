document.addEventListener("DOMContentLoaded", function () {
    const currentPath = window.location.pathname;

    function isPathMatch(current, target) {
        if (!target || !target.startsWith('/')) return false;
        if (current === target || current === target + '/') return true;
        if (target !== '/' && current.startsWith(target + '/')) return true;
        return false;
    }

    function getParentPath(path) {
        const parts = path.split('/').filter(p => p);
        if (parts.length < 2) return null;

        const last = parts[parts.length - 1];
        const secondLast = parts.length >= 3 ? parts[parts.length - 2] : null;

        const isCreate = last === 'save' || last === 'create';
        const isUpdate = last === 'update' || last === 'edit';
        const isDetail = !isNaN(last) && parts.length === 2;
        const isUpdateWithId = isUpdate && !isNaN(secondLast);

        if (isCreate || (isUpdate && !isUpdateWithId) || isDetail) {
            return '/' + parts.slice(0, -1).join('/');
        }

        if (isUpdateWithId) {
            return '/' + parts.slice(0, -2).join('/');
        }

        return null;
    }

    function activateDropdown(item) {
        const dropdown = item.closest('.nav-item.dropdown');
        if (dropdown) {
            const toggle = dropdown.querySelector('.dropdown-toggle');
            if (toggle) {
                toggle.classList.add('active');
            }

            const checkbox = dropdown.querySelector('.dropdown-checkbox');
            if (checkbox) {
                checkbox.checked = true;
            }
        }
    }

    function setActive(link, href) {
        if (isPathMatch(currentPath, href)) {
            link.classList.add('active');
            if (link.classList.contains('dropdown-item')) {
                activateDropdown(link);
            }

            return true;
        }

        return false;
    }

    document.querySelectorAll('.dropdown-item').forEach(i => {
        const href = i.getAttribute('href');

        if (!setActive(i, href)) {
            const parentPath = getParentPath(currentPath);
            if (parentPath && href === parentPath + '/list') {
                i.classList.add('active');
                activateDropdown(i);
            }
        }
    });

    document.querySelectorAll('.nav-link').forEach(link => {
        const href = link.getAttribute('href');

        if (!href || href === '#' || !href.startsWith('/')) return;
        if (link.classList.contains('dropdown-toggle')) return;

        if (!setActive(link, href)) {
            const parentPath = getParentPath(currentPath);
            if (parentPath && href === parentPath + '/list') {
                link.classList.add('active');
            } else {
                link.classList.remove('active');
            }
        }
    });

    document.querySelectorAll('.dropdown-toggle').forEach(toggle => {
        toggle.addEventListener('click', function (e) {
            if (window.innerWidth <= 1024) {
                const dropdown = this.closest('.nav-item.dropdown');
                if (dropdown) {
                    const checkbox = dropdown.querySelector('.dropdown-checkbox');
                    if (checkbox) {
                        checkbox.checked = !checkbox.checked;
                        e.preventDefault();
                    }
                }
            }
        });
    });

    function navDropdownWidth() {
        if (window.innerWidth <= 1024) return;

        document.querySelectorAll('.nav-item.dropdown').forEach(dropdown => {
            const menu = dropdown.querySelector('.dropdown-menu');
            const toggle = dropdown.querySelector('.nav-link.dropdown-toggle');

            if (menu && toggle) {
                menu.style.visibility = 'hidden';
                menu.style.display = 'block';
                menu.style.position = 'absolute';
                menu.style.opacity = '0';

                const menuWidth = menu.offsetWidth;

                menu.style.visibility = '';
                menu.style.display = '';
                menu.style.position = '';
                menu.style.opacity = '';

                if (menuWidth > 0) {
                    toggle.style.minWidth = menuWidth + 'px';
                }
            }
        });
    }

    navDropdownWidth();
    window.addEventListener('resize', navDropdownWidth);

    document.querySelectorAll('.nav-item.dropdown').forEach(dropdown => {
        dropdown.addEventListener('mouseenter', function () {
            if (window.innerWidth > 1024) {
                navDropdownWidth();
            }
        });
    });
})