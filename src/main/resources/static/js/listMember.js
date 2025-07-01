// ========== 全域變數 ==========
let memberTable;
let addPhotoFiles = [];
let editPhotoFiles = [];
let currentMemberPhotos = [];

// ========== 頁面載入完成後初始化 ==========
document.addEventListener('DOMContentLoaded', function () {
    initializeDataTable();
    initializeSearchAndFilter();
    initializeModals();
    initializePhotoUpload();
    initializeFormValidation();
});

// ========== DataTable 初始化 ==========
function initializeDataTable() {
    memberTable = $('#memberTable').DataTable({
        responsive: true,
		searching: false,
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/zh-HANT.json'
        },
        columnDefs: [
            {
                targets: [6], // 操作欄位
                orderable: false,
                searchable: false
            }
        ],
        order: [[0, 'desc']], // 預設按編號降序排列
        pageLength: 25,
        dom: '<"row"<"col-sm-12 col-md-6"l><"col-sm-12 col-md-6"f>>' +
            '<"row"<"col-sm-12"tr>>' +
            '<"row"<"col-sm-12 col-md-5"i><"col-sm-12 col-md-7"p>>'
    });
}

// ========== 搜尋與篩選功能 ==========
function initializeSearchAndFilter() {
    // 搜尋功能
    $('#searchInput').on('keyup', function () {
        memberTable.search(this.value).draw();
    });

    // 性別篩選
    $('#genderFilter').on('change', function () {
        const selectedGender = this.value;
        if (selectedGender === '') {
            memberTable.column(4).search('').draw();
        } else {
            const genderText = getGenderText(selectedGender);
            memberTable.column(4).search(genderText).draw();
        }
    });

    // 狀態篩選
    $('#statusFilter').on('change', function () {
        const selectedStatus = this.value;
        if (selectedStatus === '') {
            memberTable.column(5).search('').draw();
        } else {
            const statusText = getStatusText(selectedStatus);
            memberTable.column(5).search(statusText).draw();
        }
    });
}

// ========== 輔助函數：取得性別文字 ==========
function getGenderText(gender) {
    switch (gender) {
        case '0': return '男生';
        case '1': return '女生';
        case '2': return '其它';
        default: return '';
    }
}

// ========== 輔助函數：取得狀態文字 ==========
function getStatusText(status) {
    switch (status) {
        case '0': return '未驗證';
        case '1': return '已驗證';
        case '2': return '停用';
        default: return '';
    }
}

// ========== Modal 初始化 ==========
function initializeModals() {
    // 編輯會員 Modal 打開時的處理
    $('#editMemberModal').on('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const modal = this;

        // 取得資料屬性
        const memberId = button.getAttribute('data-id');
        const email = button.getAttribute('data-email');
        const name = button.getAttribute('data-name');
        const birthdate = button.getAttribute('data-birthdate');
        const gender = button.getAttribute('data-gender');
        const phone = button.getAttribute('data-phone');
        const address = button.getAttribute('data-address');
        const status = button.getAttribute('data-status');

        // 填入表單資料
        modal.querySelector('input[name="memberId"]').value = memberId;
        modal.querySelector('#editMemberName').value = name;
        modal.querySelector('#editMemberEmail').value = email;
        modal.querySelector('#editMemberAddress').value = address;
        modal.querySelector('#editMemberGender').value = gender;
        modal.querySelector('#editMemberStatus').value = status;

        // 清空照片預覽
        editPhotoFiles = [];
        currentMemberPhotos = [];
        document.getElementById('memberPhotoSortArea').innerHTML = '';

        // 載入現有照片
        loadMemberPhotos(memberId);
    });

    // Modal 關閉時清空表單
    $('#addMemberModal, #editMemberModal').on('hidden.bs.modal', function () {
        clearFormValidation(this);
        resetPhotoUpload();
    });
}

// ========== 照片上傳功能初始化 ==========
function initializePhotoUpload() {
    // 新增會員照片上傳
    const addPhotoInput = document.getElementById('addMemberPhotoInput');
    if (addPhotoInput) {
        addPhotoInput.addEventListener('change', function (e) {
            handlePhotoUpload(e.target.files, 'add');
        });
    }

    // 編輯會員照片上傳
    const editPhotoInput = document.getElementById('editMemberPhotoInput');
    if (editPhotoInput) {
        editPhotoInput.addEventListener('change', function (e) {
            handlePhotoUpload(e.target.files, 'edit');
        });
    }

    // 初始化拖曳排序功能
    initializeSortable();
}

// ========== 處理照片上傳 ==========
function handlePhotoUpload(files, mode) {
    const previewContainer = mode === 'add' ?
        document.getElementById('addMemberPhotoPreview') :
        document.getElementById('memberPhotoSortArea');

    Array.from(files).forEach(file => {
        if (file.type.startsWith('image/')) {
            const reader = new FileReader();
            reader.onload = function (e) {
                const photoId = 'photo_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
                const photoElement = createPhotoPreviewElement(e.target.result, photoId, mode);
                previewContainer.appendChild(photoElement);

                // 儲存檔案參考
                if (mode === 'add') {
                    addPhotoFiles.push({ id: photoId, file: file });
                } else {
                    editPhotoFiles.push({ id: photoId, file: file });
                }
            };
            reader.readAsDataURL(file);
        }
    });
}

// ========== 建立照片預覽元素 ==========
function createPhotoPreviewElement(src, photoId, mode, isExisting = false) {
    const div = document.createElement('div');
    div.className = 'position-relative d-inline-block';
    div.setAttribute('data-photo-id', photoId);
    if (isExisting) {
        div.setAttribute('data-existing', 'true');
    }

    div.innerHTML = `
        <img src="${src}" class="img-thumbnail" style="width: 100px; height: 100px; object-fit: cover;">
        <button type="button" class="btn btn-danger btn-sm position-absolute top-0 end-0 rounded-circle" 
                style="width: 25px; height: 25px; padding: 0; font-size: 12px;"
                onclick="removePhoto('${photoId}', '${mode}')">
            <i class="bi bi-x"></i>
        </button>
    `;

    return div;
}

// ========== 移除照片 ==========
function removePhoto(photoId, mode) {
    const photoElement = document.querySelector(`[data-photo-id="${photoId}"]`);
    if (photoElement) {
        photoElement.remove();

        // 從檔案陣列中移除
        if (mode === 'add') {
            addPhotoFiles = addPhotoFiles.filter(item => item.id !== photoId);
        } else {
            editPhotoFiles = editPhotoFiles.filter(item => item.id !== photoId);
            // 如果是現有照片，加入到刪除清單
            if (photoElement.getAttribute('data-existing') === 'true') {
                // 這裡可以加入刪除現有照片的邏輯
                console.log('移除現有照片:', photoId);
            }
        }
    }
}

// ========== 初始化拖曳排序 ==========
function initializeSortable() {
    const sortArea = document.getElementById('memberPhotoSortArea');
    if (sortArea && typeof Sortable !== 'undefined') {
        new Sortable(sortArea, {
            animation: 150,
            ghostClass: 'sortable-ghost',
            chosenClass: 'sortable-chosen',
            dragClass: 'sortable-drag'
        });
    }
}

// ========== 載入會員現有照片 ==========
function loadMemberPhotos(memberId) {
    // 這裡應該發送 AJAX 請求到後端獲取會員照片
    // 為了示範，我們使用假資料
    fetch(`/api/members/${memberId}/photos`)
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('無法載入照片');
        })
        .then(photos => {
            const container = document.getElementById('memberPhotoSortArea');
            photos.forEach((photo, index) => {
                const photoId = 'existing_' + index;
                const photoElement = createPhotoPreviewElement(photo.url, photoId, 'edit', true);
                container.appendChild(photoElement);
                currentMemberPhotos.push({ id: photoId, url: photo.url });
            });
        })
        .catch(error => {
            console.error('載入照片失敗:', error);
        });
}

// ========== 表單驗證初始化 ==========
function initializeFormValidation() {
    // 新增會員表單驗證
    const addForm = document.getElementById('addMemberForm');
    if (addForm) {
        addForm.addEventListener('submit', function (e) {
            if (!validateForm(this)) {
                e.preventDefault();
                e.stopPropagation();
            } else {
                handleFormSubmit(this, 'add');
            }
        });
    }

    // 編輯會員表單驗證
    const editForm = document.getElementById('editMemberForm');
    if (editForm) {
        editForm.addEventListener('submit', function (e) {
            if (!validateForm(this)) {
                e.preventDefault();
                e.stopPropagation();
            } else {
                handleFormSubmit(this, 'edit');
            }
        });
    }
}

// ========== 表單驗證 ==========
function validateForm(form) {
    const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
    let isValid = true;

    inputs.forEach(input => {
        if (!input.value.trim()) {
            input.classList.add('is-invalid');
            isValid = false;
        } else {
            input.classList.remove('is-invalid');
            input.classList.add('is-valid');
        }
    });

    // 信箱格式驗證
    const emailInputs = form.querySelectorAll('input[type="email"], input[id*="Email"]');
    emailInputs.forEach(input => {
        if (input.value && !isValidEmail(input.value)) {
            input.classList.add('is-invalid');
            isValid = false;
        }
    });

    return isValid;
}

// ========== 信箱格式驗證 ==========
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// ========== 處理表單提交 ==========
function handleFormSubmit(form, mode) {
    const formData = new FormData(form);

    // 添加照片檔案
    const photoFiles = mode === 'add' ? addPhotoFiles : editPhotoFiles;
    photoFiles.forEach((photoItem, index) => {
        formData.append('photos', photoItem.file);
    });

    // 添加照片排序資訊
    const photoOrder = getPhotoOrder(mode);
    formData.append('photoOrder', JSON.stringify(photoOrder));

    // 發送請求
    const url = mode === 'add' ? '/backend/member/add' : '/backend/member/update';

    fetch(url, {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (response.ok) {
                return response.text();
            }
            throw new Error('提交失敗');
        })
        .then(result => {
            showNotification('操作成功！', 'success');
            const modal = form.closest('.modal');
            const modalInstance = bootstrap.Modal.getInstance(modal);
            modalInstance.hide();

            // 重新載入頁面或更新表格
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        })
        .catch(error => {
            console.error('提交錯誤:', error);
            showNotification('操作失敗，請重試！', 'error');
        });
}

// ========== 取得照片排序 ==========
function getPhotoOrder(mode) {
    const container = mode === 'add' ?
        document.getElementById('addMemberPhotoPreview') :
        document.getElementById('memberPhotoSortArea');

    const photos = container.querySelectorAll('[data-photo-id]');
    return Array.from(photos).map(photo => photo.getAttribute('data-photo-id'));
}

// ========== 清空表單驗證樣式 ==========
function clearFormValidation(modal) {
    const inputs = modal.querySelectorAll('.is-valid, .is-invalid');
    inputs.forEach(input => {
        input.classList.remove('is-valid', 'is-invalid');
    });
}

// ========== 重置照片上傳 ==========
function resetPhotoUpload() {
    addPhotoFiles = [];
    editPhotoFiles = [];
    currentMemberPhotos = [];

    const addPreview = document.getElementById('addMemberPhotoPreview');
    const editPreview = document.getElementById('memberPhotoSortArea');

    if (addPreview) addPreview.innerHTML = '';
    if (editPreview) editPreview.innerHTML = '';

    const addInput = document.getElementById('addMemberPhotoInput');
    const editInput = document.getElementById('editMemberPhotoInput');

    if (addInput) addInput.value = '';
    if (editInput) editInput.value = '';
}

// ========== 顯示通知 ==========
function showNotification(message, type = 'info') {
    // 建立通知元素
    const notification = document.createElement('div');
    notification.className = `alert alert-${type === 'error' ? 'danger' : type} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    notification.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    document.body.appendChild(notification);

    // 3秒後自動移除
    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 3000);
}

// ========== 生日日期格式化 ==========
function formatBirthdate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.getFullYear() + '年' +
        String(date.getMonth() + 1).padStart(2, '0') + '月' +
        String(date.getDate()).padStart(2, '0') + '日';
}

// ========== 電話號碼格式化 ==========
function formatPhoneNumber(phone) {
    if (!phone) return '';
    // 簡單的台灣手機號碼格式化
    if (phone.length === 10 && phone.startsWith('09')) {
        return phone.substring(0, 4) + '-' + phone.substring(4, 7) + '-' + phone.substring(7);
    }
    return phone;
}

// ========== 會員詳細資料顯示 ==========
function showMemberDetails(memberId) {
    // 這個函數可以用來顯示會員的詳細資料
    fetch(`/api/members/${memberId}`)
        .then(response => response.json())
        .then(member => {
            // 建立詳細資料modal或頁面
            console.log('會員詳細資料:', member);
        })
        .catch(error => {
            console.error('載入會員資料失敗:', error);
            showNotification('載入資料失敗', 'error');
        });
}

// ========== 匯出功能 ==========
function exportMembers() {
    const url = '/backend/member/export';
    window.open(url, '_blank');
}

// ========== 批量操作 ==========
function initializeBatchOperations() {
    // 全選功能
    const selectAll = document.getElementById('selectAll');
    const checkboxes = document.querySelectorAll('.member-checkbox');

    if (selectAll) {
        selectAll.addEventListener('change', function () {
            checkboxes.forEach(checkbox => {
                checkbox.checked = this.checked;
            });
            updateBatchButtons();
        });
    }

    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', updateBatchButtons);
    });
}

function updateBatchButtons() {
    const checkedBoxes = document.querySelectorAll('.member-checkbox:checked');
    const batchButtons = document.querySelectorAll('.batch-action');

    batchButtons.forEach(button => {
        button.disabled = checkedBoxes.length === 0;
    });
}