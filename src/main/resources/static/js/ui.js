// XYZ UI Components and Utilities
const ui = {
    // Show post modal
    showPostModal() {
        const modal = document.getElementById('postModal');
        modal.classList.add('show');
        modal.querySelector('textarea').focus();
    },

    // Hide post modal
    hidePostModal() {
        const modal = document.getElementById('postModal');
        modal.classList.remove('show');
        modal.querySelector('form').reset();
        this.updateCharCount(modal.querySelector('textarea'));
    },

    // Update character count
    updateCharCount(textarea) {
        const count = textarea.value.length;
        const counter = document.getElementById('charCount');
        counter.textContent = count;

        if (count > 280) {
            counter.style.color = '#ff6b6b';
        } else if (count > 260) {
            counter.style.color = '#f59e0b';
        } else {
            counter.style.color = 'var(--text-secondary)';
        }
    },

    // Show search suggestions
    displaySuggestions(input, suggestions) {
        this.hideSearchSuggestions();

        const container = document.createElement('div');
        container.className = 'search-suggestions-dropdown';

        if (suggestions.users && suggestions.users.length > 0) {
            const usersSection = document.createElement('div');
            usersSection.className = 'suggestions-section';
            usersSection.innerHTML = '<div class="suggestions-header">People</div>';

            suggestions.users.forEach(user => {
                const item = document.createElement('a');
                item.href = `/u/${user.username}`;
                item.className = 'suggestion-item';
                item.innerHTML = `
                    <img src="https://ui-avatars.com/api/?name=${user.username.substring(0, 1)}&background=FFC107&color=000&size=32&font-size=0.5&bold=true" 
                         alt="${user.username}" class="suggestion-avatar">
                    <div class="suggestion-content">
                        <div class="suggestion-name">${user.username}</div>
                        <div class="suggestion-meta">${user.followersCount} followers</div>
                    </div>
                `;
                usersSection.appendChild(item);
            });

            container.appendChild(usersSection);
        }

        const rect = input.getBoundingClientRect();
        container.style.position = 'fixed';
        container.style.top = rect.bottom + 'px';
        container.style.left = rect.left + 'px';
        container.style.width = rect.width + 'px';

        document.body.appendChild(container);
    },

    // Hide search suggestions
    hideSearchSuggestions() {
        const dropdown = document.querySelector('.search-suggestions-dropdown');
        if (dropdown) {
            dropdown.remove();
        }
    },

    // Animate button
    animateButton(button) {
        button.style.transform = 'scale(0.9)';
        setTimeout(() => {
            button.style.transform = 'scale(1)';
        }, 100);
    },

    // Show loader
    showLoader(container) {
        const loader = document.createElement('div');
        loader.className = 'loader';
        loader.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
        container.appendChild(loader);
        return loader;
    },

    // Remove post from DOM
    removePostFromDOM(postId) {
        const postElement = document.querySelector(`[data-post-id="${postId}"]`);
        if (postElement) {
            postElement.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
            postElement.style.opacity = '0';
            postElement.style.transform = 'translateX(-20px)';
            setTimeout(() => postElement.remove(), 300);
        }
    },

    // Initialize tooltips
    initializeTooltips() {
        const tooltips = document.querySelectorAll('[data-tooltip]');
        tooltips.forEach(el => {
            el.addEventListener('mouseenter', (e) => {
                const text = e.target.getAttribute('data-tooltip');
                const tooltip = document.createElement('div');
                tooltip.className = 'tooltip';
                tooltip.textContent = text;
                document.body.appendChild(tooltip);

                const rect = e.target.getBoundingClientRect();
                tooltip.style.top = `${rect.top - tooltip.offsetHeight - 8}px`;
                tooltip.style.left = `${rect.left + rect.width / 2 - tooltip.offsetWidth / 2}px`;
            });

            el.addEventListener('mouseleave', () => {
                document.querySelectorAll('.tooltip').forEach(t => t.remove());
            });
        });
    },

    // Format date
    formatDate(dateString) {
        const date = new Date(dateString);
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const year = date.getFullYear();
        return `${month}-${day}-${year}`;
    },

    // Format time ago
    formatTimeAgo(date) {
        const seconds = Math.floor((new Date() - date) / 1000);

        const intervals = [
            { label: 'year', seconds: 31536000 },
            { label: 'month', seconds: 2592000 },
            { label: 'day', seconds: 86400 },
            { label: 'hour', seconds: 3600 },
            { label: 'minute', seconds: 60 },
            { label: 'second', seconds: 1 }
        ];

        for (const interval of intervals) {
            const count = Math.floor(seconds / interval.seconds);
            if (count > 0) {
                return count === 1
                    ? `${count} ${interval.label} ago`
                    : `${count} ${interval.label}s ago`;
            }
        }

        return 'just now';
    }
};