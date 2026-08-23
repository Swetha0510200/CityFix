/**
 * CityFix - Administrator Portal JavaScript
 */

// Admin Dashboard
async function loadAdminDashboard() {
    try {
        const res = await fetch('/api/admin/dashboard-stats');
        const result = await res.json();
        if (result.success && result.data) {
            const d = result.data;
            document.getElementById('adminStatTotal').innerText = d.totalComplaints;
            document.getElementById('adminStatPending').innerText = d.pendingComplaints;
            document.getElementById('adminStatAssigned').innerText = d.assignedComplaints;
            document.getElementById('adminStatInProgress').innerText = d.inProgressComplaints;
            document.getElementById('adminStatResolved').innerText = d.resolvedComplaints;
            document.getElementById('adminStatRejected').innerText = d.rejectedComplaints;
            document.getElementById('adminStatCitizens').innerText = d.totalCitizens;

            // Category breakdown cards / bars
            const catContainer = document.getElementById('categoryBreakdownContainer');
            if (catContainer && d.categoryDistribution) {
                const total = d.totalComplaints || 1;
                const entries = Object.entries(d.categoryDistribution);
                if (entries.length === 0) {
                    catContainer.innerHTML = '<p class="text-muted small">No data yet.</p>';
                } else {
                    catContainer.innerHTML = entries.map(([cat, count]) => {
                        const pct = Math.round((count / total) * 100);
                        return `
                            <div class="mb-3">
                                <div class="d-flex justify-content-between small fw-semibold mb-1">
                                    <span><i class="bi ${getCategoryIcon(cat)} me-1"></i> ${escapeHtml(cat)}</span>
                                    <span>${count} (${pct}%)</span>
                                </div>
                                <div class="progress" style="height: 7px;">
                                    <div class="progress-bar bg-primary" role="progressbar" style="width: ${pct}%;" aria-valuenow="${pct}" aria-valuemin="0" aria-valuemax="100"></div>
                                </div>
                            </div>
                        `;
                    }).join('');
                }
            }

            // Recent table
            const tbody = document.getElementById('adminRecentTable');
            if (tbody && d.recentComplaints) {
                if (d.recentComplaints.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">No complaints recorded yet.</td></tr>';
                    return;
                }
                tbody.innerHTML = d.recentComplaints.map(c => `
                    <tr>
                        <td class="fw-bold text-primary">${escapeHtml(c.complaintId)}</td>
                        <td>${escapeHtml(c.citizenName || 'Citizen')}</td>
                        <td><span class="badge-category"><i class="bi ${getCategoryIcon(c.category)} me-1"></i>${escapeHtml(c.category)}</span></td>
                        <td>${getPriorityBadge(c.priority)}</td>
                        <td>${getStatusBadge(c.status)}</td>
                        <td><small class="text-muted">${escapeHtml(c.createdAt)}</small></td>
                        <td>
                            <a href="/admin/complaints/${c.complaintId}" class="btn btn-sm btn-outline-primary">
                                <i class="bi bi-eye"></i> View
                            </a>
                        </td>
                    </tr>
                `).join('');
            }
        }
    } catch (err) {
        console.error('Error loading admin dashboard:', err);
    }
}

// Admin Complaints List & Filters
async function loadAdminComplaints() {
    const searchInput = document.getElementById('adminSearch');
    const catSelect = document.getElementById('adminFilterCategory');
    const statusSelect = document.getElementById('adminFilterStatus');
    const prioritySelect = document.getElementById('adminFilterPriority');
    const tbody = document.getElementById('adminComplaintsTableBody');

    if (!tbody) return;

    async function fetchComplaints() {
        const s = searchInput ? searchInput.value.trim() : '';
        const c = catSelect ? catSelect.value : '';
        const st = statusSelect ? statusSelect.value : '';
        const pr = prioritySelect ? prioritySelect.value : '';

        tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4"><div class="spinner-border text-primary spinner-border-sm me-2"></div> Loading complaints...</td></tr>`;

        try {
            const params = new URLSearchParams();
            if (s) params.append('search', s);
            if (c) params.append('category', c);
            if (st) params.append('status', st);
            if (pr) params.append('priority', pr);

            const res = await fetch(`/api/admin/complaints?${params.toString()}`);
            const result = await res.json();

            if (result.success) {
                const list = result.data;
                if (!list || list.length === 0) {
                    tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4 text-muted">No complaints match the filter criteria.</td></tr>`;
                    return;
                }

                tbody.innerHTML = list.map(c => `
                    <tr>
                        <td>
                            <img src="${escapeHtml(c.imageUrl)}" alt="thumb" class="complaint-thumb" onerror="this.src='/images/placeholder.png'">
                        </td>
                        <td class="fw-bold text-primary">${escapeHtml(c.complaintId)}</td>
                        <td>
                            <div class="fw-semibold">${escapeHtml(c.citizenName || 'Unknown')}</div>
                            <small class="text-muted">${escapeHtml(c.citizenPhone || '')}</small>
                        </td>
                        <td>
                            <div class="fw-medium text-truncate" style="max-width: 180px;">${escapeHtml(c.title)}</div>
                            <span class="badge-category small"><i class="bi ${getCategoryIcon(c.category)} me-1"></i>${escapeHtml(c.category)}</span>
                        </td>
                        <td>${getPriorityBadge(c.priority)}</td>
                        <td>${getStatusBadge(c.status)}</td>
                        <td><small class="text-muted">${escapeHtml(c.createdAt)}</small></td>
                        <td>
                            <div class="btn-group btn-group-sm">
                                <a href="/admin/complaints/${c.complaintId}" class="btn btn-outline-primary" title="View Details">
                                    <i class="bi bi-eye"></i>
                                </a>
                                <button type="button" class="btn btn-outline-secondary" onclick="openStatusModal(${c.id}, '${escapeHtml(c.complaintId)}', '${escapeHtml(c.status)}', '${escapeHtml(c.adminRemarks || '')}')" title="Update Status">
                                    <i class="bi bi-pencil-square"></i>
                                </button>
                                <button type="button" class="btn btn-outline-danger" onclick="deleteComplaint(${c.id}, '${escapeHtml(c.complaintId)}')" title="Delete Complaint">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                `).join('');
            }
        } catch (err) {
            tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4 text-danger">Failed to load complaints.</td></tr>`;
        }
    }

    if (searchInput) searchInput.addEventListener('input', debounce(fetchComplaints, 300));
    if (catSelect) catSelect.addEventListener('change', fetchComplaints);
    if (statusSelect) statusSelect.addEventListener('change', fetchComplaints);
    if (prioritySelect) prioritySelect.addEventListener('change', fetchComplaints);

    fetchComplaints();
}

// Modal helper for status update
function openStatusModal(id, complaintCode, currentStatus, currentRemarks) {
    let modalEl = document.getElementById('statusUpdateModal');
    if (!modalEl) return;

    document.getElementById('modalComplaintId').innerText = complaintCode;
    document.getElementById('modalComplaintNumericId').value = id;
    document.getElementById('modalStatusSelect').value = currentStatus || 'Pending';
    document.getElementById('modalRemarksText').value = currentRemarks || '';

    const bsModal = new bootstrap.Modal(modalEl);
    bsModal.show();
}

async function saveStatusFromModal() {
    const id = document.getElementById('modalComplaintNumericId').value;
    const status = document.getElementById('modalStatusSelect').value;
    const adminRemarks = document.getElementById('modalRemarksText').value.trim();

    try {
        const res = await fetch(`/api/admin/complaints/${id}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status, adminRemarks })
        });
        const result = await res.json();

        if (result.success) {
            showToast('Status updated successfully!', 'success');
            const modalEl = document.getElementById('statusUpdateModal');
            const modal = bootstrap.Modal.getInstance(modalEl);
            if (modal) modal.hide();
            
            // Reload list
            loadAdminComplaints();
        } else {
            showToast(result.message || 'Failed to update status.', 'danger');
        }
    } catch (err) {
        showToast('Network error while updating status.', 'danger');
    }
}

async function deleteComplaint(id, code) {
    if (!confirm(`Are you sure you want to permanently delete complaint ${code}? This action cannot be undone.`)) {
        return;
    }

    try {
        const res = await fetch(`/api/admin/complaints/${id}`, { method: 'DELETE' });
        const result = await res.json();

        if (result.success) {
            showToast(`Complaint ${code} deleted.`, 'success');
            loadAdminComplaints();
        } else {
            showToast(result.message || 'Failed to delete complaint.', 'danger');
        }
    } catch (err) {
        showToast('Error deleting complaint.', 'danger');
    }
}

// Admin Complaint Details Page
async function loadAdminComplaintDetails(complaintId) {
    const container = document.getElementById('adminComplaintDetailsContent');
    if (!container) return;

    try {
        const res = await fetch(`/api/admin/complaints/${complaintId}`);
        const result = await res.json();

        if (result.success && result.data) {
            const c = result.data;
            document.getElementById('admDetNumericId').value = c.id;
            document.getElementById('admDetComplaintId').innerText = c.complaintId;
            document.getElementById('admDetTitle').innerText = c.title;
            document.getElementById('admDetCategory').innerHTML = `<i class="bi ${getCategoryIcon(c.category)} me-1"></i> ${escapeHtml(c.category)}`;
            document.getElementById('admDetLocation').innerText = c.location;
            document.getElementById('admDetDescription').innerText = c.description;
            document.getElementById('admDetCreatedAt').innerText = c.createdAt;
            document.getElementById('admDetUpdatedAt').innerText = c.updatedAt || c.createdAt;
            document.getElementById('admDetStatusBadge').innerHTML = getStatusBadge(c.status);
            document.getElementById('admDetPriorityBadge').innerHTML = getPriorityBadge(c.priority);

            // Citizen Info
            document.getElementById('admDetCitizenName').innerText = c.citizenName || 'N/A';
            document.getElementById('admDetCitizenEmail').innerText = c.citizenEmail || 'N/A';
            document.getElementById('admDetCitizenPhone').innerText = c.citizenPhone || 'N/A';

            // Image
            if (c.imageFilename && c.imageFilename.trim() !== '') {
                document.getElementById('admDetImage').src = `/api/complaints/images/${c.imageFilename}`;
                document.getElementById('admDetImageLink').href = `/api/complaints/images/${c.imageFilename}`;
            } else {
                document.getElementById('admDetImage').src = '/images/placeholder.png';
                document.getElementById('admDetImageLink').href = '/images/placeholder.png';
            }

            // Edit Form Values
            document.getElementById('admEditStatus').value = c.status || 'Pending';
            document.getElementById('admEditRemarks').value = c.adminRemarks || '';

            // Delete action binding
            const delBtn = document.getElementById('admDeleteBtn');
            if (delBtn) {
                delBtn.onclick = () => {
                    if (confirm(`Delete complaint ${c.complaintId}?`)) {
                        fetch(`/api/admin/complaints/${c.id}`, { method: 'DELETE' })
                            .then(r => r.json())
                            .then(data => {
                                if (data.success) {
                                    showToast('Complaint deleted.', 'success');
                                    setTimeout(() => window.location.href = '/admin/complaints', 600);
                                }
                            });
                    }
                };
            }
        }
    } catch (err) {
        console.error('Error loading complaint details:', err);
    }
}

function initAdminComplaintEditForm() {
    const form = document.getElementById('adminUpdateStatusForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const id = document.getElementById('admDetNumericId').value;
            const status = document.getElementById('admEditStatus').value;
            const adminRemarks = document.getElementById('admEditRemarks').value.trim();

            const submitBtn = form.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Updating...';

            try {
                const res = await fetch(`/api/admin/complaints/${id}/status`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ status, adminRemarks })
                });
                const result = await res.json();

                if (result.success) {
                    showToast('Complaint status updated successfully!', 'success');
                    document.getElementById('admDetStatusBadge').innerHTML = getStatusBadge(result.data.status);
                    document.getElementById('admDetUpdatedAt').innerText = result.data.updatedAt;
                } else {
                    showToast(result.message || 'Failed to update.', 'danger');
                }
            } catch (err) {
                showToast('Network error while updating.', 'danger');
            } finally {
                submitBtn.disabled = false;
                submitBtn.innerHTML = '<i class="bi bi-check2-circle"></i> Save Changes';
            }
        });
    }
}

// Admin Registered Citizens List
async function loadAdminUsers() {
    const tbody = document.getElementById('adminUsersTableBody');
    if (!tbody) return;

    try {
        const res = await fetch('/api/admin/users');
        const result = await res.json();

        if (result.success && result.data) {
            const list = result.data;
            if (list.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-muted">No citizens registered yet.</td></tr>';
                return;
            }

            tbody.innerHTML = list.map(u => `
                <tr>
                    <td class="fw-bold text-muted">#${u.id}</td>
                    <td class="fw-semibold">${escapeHtml(u.name)}</td>
                    <td>${escapeHtml(u.email)}</td>
                    <td>${escapeHtml(u.phone)}</td>
                    <td><span class="badge bg-light text-dark border">${u.complaintCount} Reported</span></td>
                    <td><small class="text-muted">${escapeHtml(u.createdAt)}</small></td>
                </tr>
            `).join('');
        }
    } catch (err) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-danger">Failed to load citizens directory.</td></tr>';
    }
}
