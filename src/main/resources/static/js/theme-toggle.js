// Theme Toggle Functionality

(function() {
    'use strict';

    // Check for saved theme preference or default to light
    const currentTheme = localStorage.getItem('theme') || 'light';

    // Apply theme immediately (before DOM loads to prevent flash)
    if (currentTheme === 'dark') {
        document.documentElement.classList.add('dark-theme');
        if (document.body) {
            document.body.classList.add('dark-theme');
        }
    }

    // Wait for DOM to be ready
    document.addEventListener('DOMContentLoaded', function() {
        const toggleButton = document.querySelector('.theme-toggle');

        if (!toggleButton) {
            console.warn('Theme toggle button not found');
            return;
        }

        toggleButton.addEventListener('click', function(e) {
            e.preventDefault();
            document.body.classList.toggle('dark-theme');

            // Save preference to localStorage
            const theme = document.body.classList.contains('dark-theme') ? 'dark' : 'light';
            localStorage.setItem('theme', theme);
        });
    });
})();
