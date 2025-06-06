// Reply functionality
const reply = {
    // Submit inline reply
    async submitInlineReply(form, event) {
        event.preventDefault();

        const input = form.querySelector('input[name="content"]');
        const submitBtn = form.querySelector('.reply-submit-btn');
        const content = input.value.trim();

        if (!content) return;

        // Disable form during submission
        input.disabled = true;
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';

        try {
            const response = await fetch(form.action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `content=${encodeURIComponent(content)}`
            });

            if (response.ok) {
                // Reload page to show new reply
                window.location.reload();
            } else {
                alert('Failed to post reply');
            }
        } catch (error) {
            console.error('Error posting reply:', error);
            alert('Error posting reply');
        } finally {
            input.disabled = false;
            submitBtn.disabled = false;
            submitBtn.textContent = 'Reply';
        }
    },

    // Initialize reply click handlers
    initReplyClickHandlers() {
        document.querySelectorAll('.reply-post').forEach(post => {
            const postBody = post.querySelector('.post-body');
            const postContent = post.querySelector('.post-content');

            // Make post content clickable
            if (postContent) {
                postContent.style.cursor = 'pointer';
                postContent.addEventListener('click', (e) => {
                    // Don't navigate if clicking on links or buttons
                    if (e.target.tagName === 'A' || e.target.closest('.action-btn')) {
                        return;
                    }
                    const postId = post.getAttribute('data-post-id');
                    if (postId) {
                        window.location.href = `/post/${postId}`;
                    }
                });
            }
        });
    },

    // Initialize inline reply forms
    initInlineReplyForms() {
        const replyForms = document.querySelectorAll('.inline-reply-input');

        replyForms.forEach(form => {
            const input = form.querySelector('input[name="content"]');
            const submitBtn = form.querySelector('.reply-submit-btn');

            // Remove focus event that opens modal
            input.removeEventListener('focus', ui.showReplyModal);

            // Add input event for enabling/disabling submit button
            input.addEventListener('input', (e) => {
                submitBtn.disabled = e.target.value.trim().length === 0;
            });

            // Add submit handler
            form.addEventListener('submit', (e) => {
                this.submitInlineReply(form, e);
            });

            // Enable submit button click
            submitBtn.addEventListener('click', (e) => {
                e.preventDefault();
                this.submitInlineReply(form, e);
            });
        });
    },

    // Toggle reply thread expansion
    toggleReplyThread(button) {
        const thread = button.nextElementSibling;
        const icon = button.querySelector('i');

        if (thread.classList.contains('collapsed')) {
            thread.classList.remove('collapsed');
            icon.classList.remove('fa-chevron-right');
            icon.classList.add('fa-chevron-down');
        } else {
            thread.classList.add('collapsed');
            icon.classList.remove('fa-chevron-down');
            icon.classList.add('fa-chevron-right');
        }
    },

    // Initialize
    init() {
        this.initInlineReplyForms();
        this.initReplyClickHandlers();
    }
};

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    reply.init();
});