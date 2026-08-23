/**
 * CityFix - Citizen Portal JavaScript
 */

// Citizen Dashboard
async function loadCitizenDashboard() {
    try {
        const res = await fetch('/api/complaints/citizen/dashboard-stats');
        const result = await res.json();
        if (result.success && result.data) {
            const d = result.data;
            document.getElementById('statTotal').innerText = d.totalComplaints;
            document.getElementById('statPending').innerText = d.pendingComplaints;
            document.getElementById('statInProgress').innerText = d.inProgressComplaints + d.assignedComplaints;
            document.getElementById('statResolved').innerText = d.resolvedComplaints;
            document.getElementById('statRejected').innerText = d.rejectedComplaints;

            const tbody = document.getElementById('recentComplaintsTable');
            if (tbody) {
                if (!d.recentComplaints || d.recentComplaints.length === 0) {
                    tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">No complaints reported yet. <a href="/report-issue" class="btn btn-sm btn-primary ms-2">Report Now</a></td></tr>`;
                    return;
                }
                tbody.innerHTML = d.recentComplaints.map(c => `
                    <tr>
                        <td class="fw-bold text-primary">${escapeHtml(c.complaintId)}</td>
                        <td>${escapeHtml(c.title)}</td>
                        <td><span class="badge-category"><i class="bi ${getCategoryIcon(c.category)} me-1"></i>${escapeHtml(c.category)}</span></td>
                        <td>${escapeHtml(c.createdAt)}</td>
                        <td>${getStatusBadge(c.status)}</td>
                        <td>
                            <a href="/complaints/${c.complaintId}" class="btn btn-sm btn-outline-primary">
                                <i class="bi bi-eye"></i> View
                            </a>
                        </td>
                    </tr>
                `).join('');
            }
        }
    } catch (err) {
        console.error('Error loading dashboard stats:', err);
    }
}

// Report Issue Form
function initReportIssueForm() {
    const form = document.getElementById('reportIssueForm');
    const imgInput = document.getElementById('imageFile');
    const previewContainer = document.getElementById('imagePreviewBox');
    const previewImg = document.getElementById('previewImg');
    const alertBox = document.getElementById('reportAlert');
    const geoBtn = document.getElementById('getGeoBtn');

    // Geo-location fetch
    if (geoBtn) {
        geoBtn.addEventListener('click', () => {
            if (navigator.geolocation) {
                geoBtn.disabled = true;
                geoBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Locating...';
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        document.getElementById('latitude').value = position.coords.latitude.toFixed(6);
                        document.getElementById('longitude').value = position.coords.longitude.toFixed(6);
                        showToast('GPS coordinates captured!', 'success');
                        geoBtn.disabled = false;
                        geoBtn.innerHTML = '<i class="bi bi-geo-alt-fill text-success"></i> Located';
                    },
                    (err) => {
                        showToast('Could not get GPS location. Please type the address manually.', 'warning');
                        geoBtn.disabled = false;
                        geoBtn.innerHTML = '<i class="bi bi-crosshair"></i> Auto-Detect';
                    }
                );
            } else {
                showToast('Geolocation is not supported by your browser.', 'warning');
            }
        });
    }

    // Image change preview
    if (imgInput) {
        imgInput.addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file) {
                // Check size (max 10MB)
                if (file.size > 10 * 1024 * 1024) {
                    showToast('File is too large! Maximum allowed image size is 10MB.', 'danger');
                    imgInput.value = '';
                    previewContainer.classList.add('d-none');
                    return;
                }
                const reader = new FileReader();
                reader.onload = (re) => {
                    previewImg.src = re.target.result;
                    previewContainer.classList.remove('d-none');
                };
                reader.readAsDataURL(file);
            } else {
                previewContainer.classList.add('d-none');
            }
        });
    }

    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            alertBox.classList.add('d-none');
            const submitBtn = form.querySelector('button[type="submit"]');

            const title = document.getElementById('title').value.trim();
            const category = document.getElementById('category').value;
            const location = document.getElementById('location').value.trim();
            const description = document.getElementById('description').value.trim();
            const priority = document.getElementById('priority').value;
            const latitude = document.getElementById('latitude').value;
            const longitude = document.getElementById('longitude').value;
            const file = imgInput.files[0];

            if (!title || !category || !location || !description) {
                alertBox.innerHTML = 'Please fill in all required fields marked with *';
                alertBox.classList.remove('d-none');
                return;
            }

            const formData = new FormData();
            formData.append('title', title);
            formData.append('category', category);
            formData.append('location', location);
            formData.append('description', description);
            formData.append('priority', priority);
            if (latitude) formData.append('latitude', latitude);
            if (longitude) formData.append('longitude', longitude);
            if (file) formData.append('image', file);

            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Submitting Complaint...';

            try {
                const res = await fetch('/api/complaints', {
                    method: 'POST',
                    body: formData
                });
                const data = await res.json();

                if (data.success) {
                    showToast('Complaint reported successfully!', 'success');
                    setTimeout(() => {
                        window.location.href = `/complaints/${data.data.complaintId}`;
                    }, 1000);
                } else {
                    alertBox.innerHTML = data.message || 'Failed to submit complaint.';
                    alertBox.classList.remove('d-none');
                    submitBtn.disabled = false;
                    submitBtn.innerHTML = '<i class="bi bi-send-fill"></i> Submit Complaint';
                }
            } catch (err) {
                alertBox.innerHTML = 'Network error while submitting. Please check connection.';
                alertBox.classList.remove('d-none');
                submitBtn.disabled = false;
                submitBtn.innerHTML = '<i class="bi bi-send-fill"></i> Submit Complaint';
            }
        });
    }
}

// My Complaints List & Filters
async function loadMyComplaints() {
    const searchInput = document.getElementById('searchComplaint');
    const categorySelect = document.getElementById('filterCategory');
    const statusSelect = document.getElementById('filterStatus');
    const container = document.getElementById('complaintsList');

    if (!container) return;

    async function fetchComplaints() {
        const s = searchInput ? searchInput.value.trim() : '';
        const c = categorySelect ? categorySelect.value : '';
        const st = statusSelect ? statusSelect.value : '';

        container.innerHTML = `
            <div class="text-center py-5">
                <div class="spinner-border text-primary" role="status"></div>
                <p class="text-muted mt-2">Loading complaints...</p>
            </div>
        `;

        try {
            const params = new URLSearchParams();
            if (s) params.append('search', s);
            if (c) params.append('category', c);
            if (st) params.append('status', st);

            const res = await fetch(`/api/complaints/my?${params.toString()}`);
            const result = await res.json();

            if (result.success) {
                const list = result.data;
                if (!list || list.length === 0) {
                    container.innerHTML = `
                        <div class="text-center py-5 bg-white rounded border">
                            <i class="bi bi-inbox text-muted fs-1 mb-2"></i>
                            <h5>No complaints found</h5>
                            <p class="text-muted mb-3">Try adjusting your filters or report a new civic issue.</p>
                            <a href="/report-issue" class="btn btn-primary"><i class="bi bi-plus-circle"></i> Report an Issue</a>
                        </div>
                    `;
                    return;
                }

                container.innerHTML = list.map(c => `
                    <div class="cf-card cf-card-hover mb-3">
                        <div class="cf-card-body">
                            <div class="row align-items-center">
                                <div class="col-md-2 text-center text-md-start mb-3 mb-md-0">
                                    <img src="${escapeHtml(c.imageUrl)}" alt="${escapeHtml(c.title)}" class="rounded img-fluid" style="max-height: 90px; width: 100%; object-fit: cover; border: 1px solid #e2e8f0;">
                                </div>
                                <div class="col-md-7 mb-3 mb-md-0">
                                    <div class="d-flex align-items-center gap-2 flex-wrap mb-1">
                                        <span class="fw-bold text-primary">${escapeHtml(c.complaintId)}</span>
                                        <span class="badge-category"><i class="bi ${getCategoryIcon(c.category)} me-1"></i>${escapeHtml(c.category)}</span>
                                        ${getPriorityBadge(c.priority)}
                                    </div>
                                    <h5 class="mb-1 fw-bold">${escapeHtml(c.title)}</h5>
                                    <p class="text-muted small mb-1 text-truncate"><i class="bi bi-geo-alt me-1"></i>${escapeHtml(c.location)}</p>
                                    <small class="text-muted"><i class="bi bi-calendar3 me-1"></i>${escapeHtml(c.createdAt)}</small>
                                </div>
                                <div class="col-md-3 text-md-end">
                                    <div class="mb-2">${getStatusBadge(c.status)}</div>
                                    <a href="/complaints/${c.complaintId}" class="btn btn-sm btn-outline-primary w-100">
                                        View Details <i class="bi bi-arrow-right"></i>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                `).join('');
            }
        } catch (err) {
            container.innerHTML = `<div class="alert alert-danger">Error loading complaints. Please try again.</div>`;
        }
    }

    if (searchInput) searchInput.addEventListener('input', debounce(fetchComplaints, 300));
    if (categorySelect) categorySelect.addEventListener('change', fetchComplaints);
    if (statusSelect) statusSelect.addEventListener('change', fetchComplaints);

    fetchComplaints();
}

// Complaint Details Page
async function loadComplaintDetails(complaintId) {
    const detailsContainer = document.getElementById('complaintDetailsContent');
    if (!detailsContainer) return;

    try {
        const res = await fetch(`/api/complaints/${complaintId}`);
        const result = await res.json();

        if (result.success && result.data) {
            const c = result.data;

            document.getElementById('detComplaintId').innerText = c.complaintId;
            document.getElementById('detTitle').innerText = c.title;
            document.getElementById('detCategory').innerHTML = `<i class="bi ${getCategoryIcon(c.category)} me-1"></i> ${escapeHtml(c.category)}`;
            document.getElementById('detLocation').innerText = c.location;
            document.getElementById('detDescription').innerText = c.description;
            document.getElementById('detCreatedAt').innerText = c.createdAt;
            document.getElementById('detStatusBadge').innerHTML = getStatusBadge(c.status);
            document.getElementById('detPriorityBadge').innerHTML = getPriorityBadge(c.priority);

            if (c.imageFilename && c.imageFilename.trim() !== '') {
                document.getElementById('detImage').src = `/api/complaints/images/${c.imageFilename}`;
                document.getElementById('detImageLink').href = `/api/complaints/images/${c.imageFilename}`;
                document.getElementById('detImageWrapper').classList.remove('d-none');
            } else {
                document.getElementById('detImage').src = '/images/placeholder.png';
                document.getElementById('detImageLink').href = '/images/placeholder.png';
                document.getElementById('detImageWrapper').classList.remove('d-none');
            }

            if (c.latitude && c.longitude) {
                document.getElementById('detCoords').innerHTML = `<i class="bi bi-geo"></i> Lat: ${c.latitude}, Lng: ${c.longitude}`;
                document.getElementById('detCoords').classList.remove('d-none');
            }

            // Admin Remarks
            const remarksBox = document.getElementById('detAdminRemarks');
            if (c.adminRemarks && c.adminRemarks.trim() !== '') {
                document.getElementById('detRemarksText').innerText = c.adminRemarks;
                remarksBox.classList.remove('d-none');
            }

            // Update Visual Timeline
            updateTimeline(c.status);
        } else {
            detailsContainer.innerHTML = `<div class="alert alert-danger">Complaint not found.</div>`;
        }
    } catch (err) {
        detailsContainer.innerHTML = `<div class="alert alert-danger">Failed to load complaint details.</div>`;
    }
}

function updateTimeline(status) {
    const s = status ? status.toLowerCase() : 'pending';
    const step1 = document.getElementById('stepSubmitted');
    const step2 = document.getElementById('stepAssigned');
    const step3 = document.getElementById('stepInProgress');
    const step4 = document.getElementById('stepResolved');

    if (!step1) return;

    // Reset classes
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

// Profile Page
async function loadProfile() {
    try {
        const res = await fetch('/api/profile');
        const result = await res.json();
        if (result.success && result.data) {
            const u = result.data;
            document.getElementById('profileName').value = u.name || '';
            document.getElementById('profileEmail').value = u.email || '';
            document.getElementById('profilePhone').value = u.phone || '';
            document.getElementById('profileJoined').innerText = u.createdAt || '-';
            document.getElementById('profileComplaintCount').innerText = u.complaintCount || '0';
        }
    } catch (err) {
        console.error('Error loading profile:', err);
    }
}

function initProfileForm() {
    const form = document.getElementById('profileForm');
    const alertBox = document.getElementById('profileAlert');

    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            alertBox.classList.add('d-none');
            const submitBtn = form.querySelector('button[type="submit"]');

            const name = document.getElementById('profileName').value.trim();
            const phone = document.getElementById('profilePhone').value.trim();
            const currentPassword = document.getElementById('currentPassword').value;
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmNewPassword').value;

            if (!name || !phone) {
                alertBox.innerHTML = 'Name and phone cannot be blank.';
                alertBox.classList.remove('d-none');
                return;
            }

            if (newPassword && newPassword !== confirmPassword) {
                alertBox.innerHTML = 'New password and confirmation do not match.';
                alertBox.classList.remove('d-none');
                return;
            }

            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Saving...';

            try {
                const res = await fetch('/api/profile', {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name, phone, currentPassword, newPassword, confirmPassword })
                });
                const data = await res.json();

                if (data.success) {
                    showToast('Profile updated successfully!', 'success');
                    document.getElementById('currentPassword').value = '';
                    document.getElementById('newPassword').value = '';
                    document.getElementById('confirmNewPassword').value = '';
                } else {
                    alertBox.innerHTML = data.message || 'Failed to update profile.';
                    alertBox.classList.remove('d-none');
                }
            } catch (err) {
                alertBox.innerHTML = 'Network error while updating profile.';
                alertBox.classList.remove('d-none');
            } finally {
                submitBtn.disabled = false;
                submitBtn.innerHTML = '<i class="bi bi-check2-circle"></i> Save Changes';
            }
        });
    }
}

// Debounce helper for instant search
function debounce(func, wait) {
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
