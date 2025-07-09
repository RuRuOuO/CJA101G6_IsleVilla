// ========== 全域變數 ==========
let memberTable;
let editPhotoFile = null;
let currentMemberPhoto = null;

// ========== 頁面載入完成後初始化 ==========
document.addEventListener('DOMContentLoaded', function () {
    initializeDataTable();
    initializeSearchAndFilter();
    initializeModals();
    initializePhotoUpload();
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
    // 搜尋功能 - 移除即時搜尋
//    $('#searchInput').on('keyup', function () {
//        memberTable.search(this.value).draw();
//    });
	// 搜尋功能 - Enter鍵觸發
    $('#searchInput').on('keypress', function(e) {
        if (e.which === 13) { // Enter鍵
            applyFilters();
        }
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

// ========== 執行篩選功能 ==========
function applyFilters() {
    const searchKeyword = document.getElementById('searchInput').value.trim();
    const selectedGender = document.getElementById('genderFilter').value;
    const selectedStatus = document.getElementById('statusFilter').value;
    
    // 構建查詢參數
    const params = new URLSearchParams();
    if (searchKeyword) params.append('search', searchKeyword);
    if (selectedGender) params.append('gender', selectedGender);
    if (selectedStatus) params.append('status', selectedStatus);
    
    // 重新載入頁面
    window.location.href = '/backend/member/list?' + params.toString();
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
        const gender = button.getAttribute('data-gender');
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
    $('#editMemberModal').on('hidden.bs.modal', function () {
        resetPhotoUpload();
    });
}

// ========== 照片上傳功能初始化 ==========
function initializePhotoUpload() {
    // 編輯會員照片上傳
    const editPhotoInput = document.getElementById('editMemberPhotoInput');
    if (editPhotoInput) {
        editPhotoInput.addEventListener('change', function (e) {
            handlePhotoUpload(e.target.files[0]);
        });
    }
}

// ========== 處理照片上傳 ==========
function handlePhotoUpload(file) {
    if (!file || !file.type.startsWith('image/')) {
        alert('請選擇有效的圖片檔案');
        return;
    }

    const previewContainer = document.getElementById('memberPhotoArea');
    previewContainer.innerHTML = '';

    const reader = new FileReader();
    reader.onload = function (e) {
        const photoId = 'photo_' + Date.now();
        const photoElement = createPhotoPreviewElement(e.target.result, photoId);
        previewContainer.appendChild(photoElement);
        editPhotoFile = file;
    };
    reader.readAsDataURL(file);
}

// ========== 建立照片預覽元素 ==========
function createPhotoPreviewElement(src, photoId, isExisting = false) {
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
                onclick="removePhoto('${photoId}')">
            <i class="bi bi-x"></i>
        </button>
    `;

    return div;
}

// ========== 移除照片 ==========
function removePhoto(photoId) {
    const photoElement = document.querySelector(`[data-photo-id="${photoId}"]`);
    if (photoElement) {
        photoElement.remove();
        
        editPhotoFile = null;
        const input = document.getElementById('editMemberPhotoInput');
        if (input) input.value = '';
        
        // 如果是現有照片，添加隱藏欄位標記刪除
        if (photoElement.getAttribute('data-existing') === 'true') {
            currentMemberPhoto = null;
            const form = document.getElementById('editMemberForm');
            const deleteInput = document.createElement('input');
            deleteInput.type = 'hidden';
            deleteInput.name = 'deletePhoto';
            deleteInput.value = 'true';
            form.appendChild(deleteInput);
        }
    }
}

// ========== 載入會員現有照片 ==========
function loadMemberPhotos(memberId) {
    currentMemberPhoto = null;
    const container = document.getElementById('memberPhotoArea');
    container.innerHTML = '';

    // 移除之前的刪除照片標記
    const existingDeleteInput = document.querySelector('input[name="deletePhoto"]');
    if (existingDeleteInput) {
        existingDeleteInput.remove();
    }

    fetch(`/backend/member/${memberId}/photo`)
        .then(response => {
            if (response.ok) {
                return response.blob();
            }
            return null;
        })
        .then(blob => {
            if (blob) {
                const photoUrl = URL.createObjectURL(blob);
                const photoId = 'existing_photo';
                const photoElement = createPhotoPreviewElement(photoUrl, photoId, true);
                container.appendChild(photoElement);
                currentMemberPhoto = { id: photoId, url: photoUrl };
            }
        })
        .catch(error => {
            console.error('載入照片失敗:', error);
        });
}

// ========== 重置照片上傳 ==========
function resetPhotoUpload() {
    editPhotoFile = null;
    currentMemberPhoto = null;

    const editPreview = document.getElementById('memberPhotoArea');
    if (editPreview) editPreview.innerHTML = '';

    const editInput = document.getElementById('editMemberPhotoInput');
    if (editInput) editInput.value = '';
}