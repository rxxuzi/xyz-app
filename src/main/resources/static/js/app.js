// XYZ App JavaScript
const app = {
    // Initialize
    init() {
        this.bindEvents();
        this.initializeTooltips();
        this.initializeSearch();
    },

    // Bind events
    bindEvents() {
        // Modal close on outside click
        document.getElementById('postModal')?.addEventListener('click', (e) => {
            if (e.target.id === 'postModal') {
                this.hidePostModal();
            }
        });

        // ESC key to close modal
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                this.hidePostModal();
                this.hideSearchSuggestions();
            }
        });

        // Post actions
        document.querySelectorAll('.action-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
            });
        });
    },

    // Initialize search functionality
    initializeSearch() {
        const searchInputs = document.querySelectorAll('input[name="q"]');
        const searchDebounce = this.debounce(this.showSearchSuggestions.bind(this), 300);

        searchInputs.forEach(input => {
            input.addEventListener('input', (e) => {
                if (e.target.value.length >= 2) {
                    searchDebounce(e.target);
                } else {
                    this.hideSearchSuggestions();
                }
            });

            input.addEventListener('focus', (e) => {
                if (e.target.value.length >= 2) {
                    this.showSearchSuggestions(e.target);
                }
            });
        });

        // Hide suggestions on click outside
        document.addEventListener('click', (e) => {
            if (!e.target.closest('.search-box') && !e.target.closest('.search-input-wrapper')) {
                this.hideSearchSuggestions();
            }
        });
    },

    // Show search suggestions
    async showSearchSuggestions(input) {
        const query = input.value.trim();
        if (query.length < 2) return;

        try {
            const response = await fetch(`/search/suggestions?q=${encodeURIComponent(query)}`);
            const suggestions = await response.json();

            if (suggestions.users && suggestions.users.length > 0) {
                this.displaySuggestions(input, suggestions);
            }
        } catch (error) {
            console.error('Error fetching suggestions:', error);
        }
    },

    // Display search suggestions
    displaySuggestions(input, suggestions) {
        // Remove existing suggestions
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

        // Position the dropdown
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

    // Toggle like
    async toggleLike(postId, button) {
        const isLiked = button.classList.contains('liked');
        const url = isLiked ? `/post/${postId}/unlike` : `/post/${postId}/like`;

        try {
            const response = await fetch(url, { method: 'POST' });
            const data = await response.json();

            if (data.success) {
                button.classList.toggle('liked');
                const icon = button.querySelector('i');
                icon.classList.toggle('far');
                icon.classList.toggle('fas');

                // Update count
                const countSpan = button.querySelector('span');
                let count = parseInt(countSpan.textContent) || 0;
                count = isLiked ? count - 1 : count + 1;
                countSpan.textContent = count;

                // Animation
                this.animateButton(button);
            }
        } catch (error) {
            console.error('Error toggling like:', error);
        }
    },

    // Follow/Unfollow user
    async toggleFollow(username, button) {
        const isFollowing = button.textContent.trim() === 'Following';
        const url = isFollowing ? `/u/${username}/unfollow` : `/u/${username}/follow`;

        try {
            const response = await fetch(url, { method: 'POST' });
            const data = await response.json();

            if (data.success) {
                if (isFollowing) {
                    button.textContent = 'Follow';
                    button.classList.remove('btn-secondary');
                    button.classList.add('btn-primary');
                } else {
                    button.textContent = 'Following';
                    button.classList.remove('btn-primary');
                    button.classList.add('btn-secondary');
                }

                this.animateButton(button);
            }
        } catch (error) {
            console.error('Error toggling follow:', error);
        }
    },

    // Delete post
    async deletePost(postId) {
        if (!confirm('Are you sure you want to delete this post?')) {
            return;
        }

        try {
            const response = await fetch(`/post/${postId}/delete`, { method: 'POST' });
            if (response.ok) {
                // Remove post from DOM with animation
                const postElement = document.querySelector(`[data-post-id="${postId}"]`);
                if (postElement) {
                    postElement.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
                    postElement.style.opacity = '0';
                    postElement.style.transform = 'translateX(-20px)';
                    setTimeout(() => postElement.remove(), 300);
                }
            }
        } catch (error) {
            console.error('Error deleting post:', error);
        }
    },

    // Animate button
    animateButton(button) {
        button.style.transform = 'scale(0.9)';
        setTimeout(() => {
            button.style.transform = 'scale(1)';
        }, 100);
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

    // Load more posts
    async loadMore(page, container) {
        const loader = document.createElement('div');
        loader.className = 'loader';
        loader.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
        container.appendChild(loader);

        try {
            const response = await fetch(`${window.location.pathname}?page=${page}`, {
                headers: { 'X-Requested-With': 'XMLHttpRequest' }
            });

            if (response.ok) {
                const html = await response.text();
                loader.remove();
                container.insertAdjacentHTML('beforeend', html);
            }
        } catch (error) {
            console.error('Error loading more posts:', error);
            loader.remove();
        }
    },

    // Debounce function
    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
};

// Initialize app when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    app.init();
});