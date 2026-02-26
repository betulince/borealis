/**
 * Main JavaScript
 * Additional interactive features
 */

(function() {
    'use strict';

    /**
     * Initialize on DOM content loaded
     */
    document.addEventListener('DOMContentLoaded', function() {
        initSmoothScroll();
        initNavigationHighlight();
        initFadeInAnimations();
    });

    /**
     * Smooth scroll for anchor links
     */
    function initSmoothScroll() {
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function(e) {
                const href = this.getAttribute('href');

                // Skip empty anchors
                if (href === '#') return;

                e.preventDefault();

                const target = document.querySelector(href);
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            });
        });
    }

    /**
     * Highlight current page in navigation
     */
    function initNavigationHighlight() {
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('.nav-links a');

        navLinks.forEach(link => {
            const linkPath = new URL(link.href).pathname;

            // Check if current path starts with link path
            if (currentPath.startsWith(linkPath) && linkPath !== '/') {
                link.classList.add('active');
            }
            // Special case for home page
            else if (currentPath === '/' && linkPath === '/') {
                link.classList.add('active');
            }
        });
    }

    /**
     * Fade-in animations using Intersection Observer
     */
    function initFadeInAnimations() {
        // Only add if user prefers reduced motion is not enabled
        const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

        if (prefersReducedMotion) {
            return;
        }

        const observerOptions = {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        };

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                }
            });
        }, observerOptions);

        // Observe cards and notes
        const animatedElements = document.querySelectorAll('.card, .note-card');
        animatedElements.forEach((el, index) => {
            // Initial state
            el.style.opacity = '0';
            el.style.transform = 'translateY(20px)';
            el.style.transition = `opacity 0.5s ease ${index * 0.1}s, transform 0.5s ease ${index * 0.1}s`;

            observer.observe(el);
        });
    }

    /**
     * Add external link indicators
     */
    function initExternalLinks() {
        const links = document.querySelectorAll('a[href^="http"]');

        links.forEach(link => {
            // Check if it's not an internal link
            if (!link.href.includes(window.location.hostname)) {
                link.setAttribute('target', '_blank');
                link.setAttribute('rel', 'noopener noreferrer');
            }
        });
    }

    // Initialize external links
    initExternalLinks();

})();
