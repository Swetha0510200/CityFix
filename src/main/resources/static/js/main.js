/**
 * CityFix - Common JavaScript Utilities
 */

// Toast notification helper
function showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.style.position = 'fixed';
        container.style.top = '20px';
        container.style.right = '20px';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    const bgClass = type === 'success' ? 'bg-success' : (type === 'danger' || type === 'error' ? 'bg-danger' : (type === 'warning' ? 'bg-warning text-dark' : 'bg-primary'));
    
    toast.className = `toast align-items-center text-white ${bgClass} border-0 show mb-2 shadow`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body fw-medium">
                ${escapeHtml(message)}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" onclick="this.parentElement.parentElement.remove()" aria-label="Close"></button>
        </div>
    `;

    container.appendChild(toast);

    setTimeout(() => {
        if (toast && toast.parentElement) {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }
    }, 4500);
}

function escapeHtml(str) {
    if (!str) return '';
    return str
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

function getStatusBadge(status) {
    if (!status) return '<span class="badge-status badge-pending">Pending</span>';
    const s = status.trim().toLowerCase();
    switch (s) {
        case 'pending':
            return `<span class="badge-status badge-pending"><i class="bi bi-clock-history"></i> Pending</span>`;
        case 'assigned':
            return `<span class="badge-status badge-assigned"><i class="bi bi-person-check"></i> Assigned</span>`;
        case 'in progress':
            return `<span class="badge-status badge-inprogress"><i class="bi bi-gear-wide-connected"></i> In Progress</span>`;
        case 'resolved':
            return `<span class="badge-status badge-resolved"><i class="bi bi-check-circle-fill"></i> Resolved</span>`;
        case 'rejected':
            return `<span class="badge-status badge-rejected"><i class="bi bi-x-circle-fill"></i> Rejected</span>`;
        default:
            return `<span class="badge-status badge-pending">${escapeHtml(status)}</span>`;
    }
}

function getPriorityBadge(priority) {
    if (!priority) return '<span class="badge-priority priority-medium">Medium</span>';
    const p = priority.trim().toLowerCase();
    switch (p) {
        case 'high':
            return `<span class="badge-priority priority-high"><i class="bi bi-exclamation-triangle-fill"></i> High</span>`;
        case 'medium':
            return `<span class="badge-priority priority-medium">Medium</span>`;
        case 'low':
            return `<span class="badge-priority priority-low">Low</span>`;
        default:
            return `<span class="badge-priority priority-medium">${escapeHtml(priority)}</span>`;
    }
}

function getCategoryIcon(category) {
    if (!category) return 'bi-tag';
    const c = category.toLowerCase();
    if (c.includes('pothole') || c.includes('road')) return 'bi-cone-striped';
    if (c.includes('garbage') || c.includes('dumping')) return 'bi-trash3';
    if (c.includes('street') || c.includes('light')) return 'bi-lightbulb';
    if (c.includes('water') || c.includes('leakage')) return 'bi-droplet';
    if (c.includes('drain')) return 'bi-water';
    if (c.includes('traffic')) return 'bi-stoplights';
    if (c.includes('toilet')) return 'bi-door-closed';
    return 'bi-geo-alt';
}

async function handleLogout() {
    try {
        const response = await fetch('/api/auth/logout', { method: 'POST' });
        const result = await response.json();
        window.location.href = '/login';
    } catch (err) {
        window.location.href = '/login';
    }
}

async function handleAdminLogout() {
    try {
        const response = await fetch('/api/auth/logout', { method: 'POST' });
        const result = await response.json();
        window.location.href = '/admin/login';
    } catch (err) {
        window.location.href = '/admin/login';
    }
}

// Public Track Complaint handler
async function handlePublicTrack(event) {
    if (event) event.preventDefault();
    const input = document.getElementById('trackComplaintInput');
    const submitBtn = document.getElementById('trackSubmitBtn');
    const resBox = document.getElementById('trackResultBox');
    const errBox = document.getElementById('trackErrorBox');
    const errMsg = document.getElementById('trackErrorMsg');

    if (!input) return;
    const code = input.value.trim();
    if (!code) return;

    resBox.classList.add('d-none');
    errBox.classList.add('d-none');
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Tracking...';

    try {
        const res = await fetch(`/api/complaints/track/${encodeURIComponent(code)}`);
        const result = await res.json();

        if (result.success && result.data) {
            const c = result.data;
            document.getElementById('trackResId').innerText = c.complaintId;
            document.getElementById('trackResTitle').innerText = c.title;
            document.getElementById('trackResCategory').innerText = c.category;
            document.getElementById('trackResDate').innerText = c.createdAt;
            document.getElementById('trackResLocation').innerText = c.location;

            document.getElementById('trackResBadges').innerHTML = `
                ${getStatusBadge(c.status)}
                ${getPriorityBadge(c.priority)}
            `;

            // Update modal timeline
            updateModalTimeline(c.status);

            // Remarks
            const remarksBox = document.getElementById('trackResRemarksBox');
            const remarksText = document.getElementById('trackResRemarksText');
            if (c.adminRemarks && c.adminRemarks.trim() !== '') {
                remarksText.innerText = c.adminRemarks;
                remarksBox.classList.remove('d-none');
            } else {
                remarksBox.classList.add('d-none');
            }

            resBox.classList.remove('d-none');
        } else {
            errMsg.innerText = result.message || 'Complaint not found with code: ' + code;
            errBox.classList.remove('d-none');
        }
    } catch (err) {
        errMsg.innerText = 'Network error while tracking complaint. Please try again.';
        errBox.classList.remove('d-none');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="bi bi-search me-1"></i> Track Now';
    }
}

function updateModalTimeline(status) {
    const s = status ? status.toLowerCase() : 'pending';
    const step1 = document.getElementById('modalStepSubmitted');
    const step2 = document.getElementById('modalStepAssigned');
    const step3 = document.getElementById('modalStepInProgress');
    const step4 = document.getElementById('modalStepResolved');

    if (!step1) return;

    [step1, step2, step3, step4].forEach(step => {
        step.className = 'timeline-step';
    });

    if (s === 'rejected') {
        step1.classList.add('completed');
        step2.classList.add('rejected');
        step2.querySelector('.timeline-label').innerText = 'Rejected';
        return;
    }

    if (s === 'pending') {
        step1.classList.add('active');
    } else if (s === 'assigned') {
        step1.classList.add('completed');
        step2.classList.add('active');
    } else if (s === 'in progress') {
        step1.classList.add('completed');
        step2.classList.add('completed');
        step3.classList.add('active');
    } else if (s === 'resolved') {
        step1.classList.add('completed');
        step2.classList.add('completed');
        step3.classList.add('completed');
        step4.classList.add('completed', 'active');
    }
}

