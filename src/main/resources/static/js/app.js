// XYZ App Main Logic
const app = {
    // Initialize
    init() {
        this.bindEvents();
        ui.initializeTooltips();
        ui.initializeInlineReplyForms();
        this.initializeSearch();
    },

    // Bind events
    bindEvents() {
        // Modal close on outside click
        document.getElementById('postModal')?.addEventListener('click', (e) => {
            if (e.target.id === 'postModal') {
                ui.hidePostModal();
            }
        });

        // ESC key to close modal
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                ui.hidePostModal();
                ui.hideSearchSuggestions();
            }
        });

        // Post actions
        document.querySelectorAll('.action-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
            });
        });

        // Prevent navigation when clicking on post actions
        document.addEventListener('click', (e) => {
            if (e.target.closest('.action-btn') || e.target.closest('.btn')) {
                e.stopPropagation();
            }
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
                    ui.hideSearchSuggestions();
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
                ui.hideSearchSuggestions();
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
                ui.displaySuggestions(input, suggestions);
            }
        } catch (error) {
            console.error('Error fetching suggestions:', error);
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

                ui.animateButton(button);
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

                ui.animateButton(button);
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
            const data = await response.json();

            if (data.success) {
                if (data.redirect) {
                    window.location.href = data.redirect;
                } else {
                    ui.removePostFromDOM(postId);
                }
            }
        } catch (error) {
            console.error('Error deleting post:', error);
        }
    },

    // Load more posts
    async loadMore(page, container) {
        const loader = ui.showLoader(container);

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