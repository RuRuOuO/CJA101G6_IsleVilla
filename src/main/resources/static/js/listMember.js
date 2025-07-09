// ========== 全域變數 ==========
let memberTable;
let addPhotoFile = null;
let editPhotoFile = null;
let currentMemberPhoto = null;

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

	        // 清空照片預覽並載入現有照片
	        editPhotoFile = null;
	        currentMemberPhoto = null;
	        document.getElementById('memberPhotoArea').innerHTML = '';
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
            handlePhotoUpload(e.target.files[0], 'add');
        });
    }

    // 編輯會員照片上傳
    const editPhotoInput = document.getElementById('editMemberPhotoInput');
    if (editPhotoInput) {
        editPhotoInput.addEventListener('change', function (e) {
            handlePhotoUpload(e.target.files[0], 'edit');
        });
    }
}

// ========== 處理照片上傳 ==========
function handlePhotoUpload(file, mode) {
    if (!file || !file.type.startsWith('image/')) {
        showNotification('請選擇有效的圖片檔案', 'error');
        return;
    }

    const previewContainer = mode === 'add' ?
        document.getElementById('addMemberPhotoPreview') :
        document.getElementById('memberPhotoArea');

    // 清空現有預覽
    previewContainer.innerHTML = '';

    const reader = new FileReader();
    reader.onload = function (e) {
        const photoId = 'photo_' + Date.now();
        const photoElement = createPhotoPreviewElement(e.target.result, photoId, mode);
        previewContainer.appendChild(photoElement);

        // 儲存檔案參考
        if (mode === 'add') {
            addPhotoFile = file;
        } else {
            editPhotoFile = file;
        }
    };
    reader.readAsDataURL(file);
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
        <img src="${src}" class="img-thumbnail" style="width: 150px; height: 150px; object-fit: cover;">
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

        // 清空檔案參考
        if (mode === 'add') {
            addPhotoFile = null;
            const input = document.getElementById('addMemberPhotoInput');
            if (input) input.value = '';
        } else {
            editPhotoFile = null;
            const input = document.getElementById('editMemberPhotoInput');
            if (input) input.value = '';
            
            // 如果是現有照片，標記為已刪除
            if (photoElement.getAttribute('data-existing') === 'true') {
                currentMemberPhoto = null;
                console.log('移除現有照片:', photoId);
            }
        }
    }
}

// ========== 初始化拖曳排序 ==========
//function initializeSortable() {
//    const sortArea = document.getElementById('memberPhotoSortArea');
//    if (sortArea && typeof Sortable !== 'undefined') {
//        new Sortable(sortArea, {
//            animation: 150,
//            ghostClass: 'sortable-ghost',
//            chosenClass: 'sortable-chosen',
//            dragClass: 'sortable-drag'
//        });
//    }
//}

// ========== 載入會員現有照片 ==========
function loadMemberPhotos(memberId) {
    // 清空現有照片
    currentMemberPhoto = null;
    const container = document.getElementById('memberPhotoArea');
    container.innerHTML = '';

    // 發送請求獲取會員照片
    fetch(`/backend/member/${memberId}/photo`)
        .then(response => {
            if (response.ok) {
                return response.blob();
            }
            // 如果沒有照片，直接返回
            return null;
        })
        .then(blob => {
            if (blob) {
                const photoUrl = URL.createObjectURL(blob);
                const photoId = 'existing_photo';
                const photoElement = createPhotoPreviewElement(photoUrl, photoId, 'edit', true);
                container.appendChild(photoElement);
                currentMemberPhoto = { id: photoId, url: photoUrl };
            }
        })
        .catch(error => {
            console.error('載入照片失敗:', error);
            // 不顯示錯誤訊息，因為會員可能沒有照片
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

    // 添加照片檔案（如果有的話）
    const photoFile = mode === 'add' ? addPhotoFile : editPhotoFile;
    if (photoFile) {
        formData.append('memberPhoto', photoFile);
    }

    // 如果是編輯模式且刪除了現有照片，需要通知後端
    if (mode === 'edit' && !photoFile && !currentMemberPhoto) {
        formData.append('deletePhoto', 'true');
    }

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
//function getPhotoOrder(mode) {
//    const container = mode === 'add' ?
//        document.getElementById('addMemberPhotoPreview') :
//        document.getElementById('memberPhotoSortArea');
//
//    const photos = container.querySelectorAll('[data-photo-id]');
//    return Array.from(photos).map(photo => photo.getAttribute('data-photo-id'));
//}

// ========== 清空表單驗證樣式 ==========
function clearFormValidation(modal) {
    const inputs = modal.querySelectorAll('.is-valid, .is-invalid');
    inputs.forEach(input => {
        input.classList.remove('is-valid', 'is-invalid');
    });
}

// ========== 重置照片上傳 ==========
function resetPhotoUpload() {
    addPhotoFile = null;
    editPhotoFile = null;
    currentMemberPhoto = null;

    const addPreview = document.getElementById('addMemberPhotoPreview');
    const editPreview = document.getElementById('memberPhotoArea');

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