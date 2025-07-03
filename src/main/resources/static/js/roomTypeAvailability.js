/**
 * 房型庫存可用性管理 JavaScript
 * 功能包括：DataTable初始化、庫存編輯、同步、刪除、右鍵菜單等
 */

// 初始化DataTable - 針對交叉表格優化
$(function(){
    $('#availabilityTable').DataTable({
        dom: "t<'d-flex justify-content-center my-3'p>",
        pagingType: 'simple_numbers',
        lengthChange: false,
        info: false,
        searching: false,
        ordering: false, // 停用排序功能，因為交叉表格不適合排序
        pageLength: 10,
        language: {
            paginate: {
                previous: '上一頁',
                next: '下一頁'
            },
            emptyTable: "暫無房型庫存資料"
        },
        columnDefs: [
            {
                targets: 0,
                className: 'sticky-col'
            }
        ],
        drawCallback: function(){
            var $pg = $('#availabilityTable_wrapper .pagination');
            $pg.find('li.disabled a, li.active a')
                .each(function(){
                    var txt = $(this).text();
                    $(this).replaceWith(
                        $('<span/>').addClass('page-link').text(txt)
                    );
                });
        }
    });
});

/**
 * 開啟更新 Modal（用於表單編輯）
 * @param {HTMLElement} element - 包含數據屬性的元素
 */
function openUpdateModal(element) {
    const roomTypeId = element.getAttribute('data-room-type-id');
    const roomTypeName = element.getAttribute('data-room-type-name');
    const availabilityDate = element.getAttribute('data-availability-date');
    const availabilityCount = element.getAttribute('data-availability-count');
    
    document.getElementById('updateRoomTypeId').value = roomTypeId;
    document.getElementById('updateRoomTypeName').value = roomTypeName;
    document.getElementById('updateAvailabilityDate').value = availabilityDate;
    document.getElementById('updateAvailabilityCount').value = availabilityCount;
    
    new bootstrap.Modal(document.getElementById('updateModal')).show();
}

/**
 * 同步單筆記錄的庫存數據
 * @param {HTMLElement} button - 觸發同步的按鈕元素
 */
function syncSingleRecord(button) {
    const roomTypeId = button.getAttribute('data-room-type-id');
    const availabilityDate = button.getAttribute('data-availability-date');
    
    if (confirm('確定要同步此記錄的庫存嗎？')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/backend/roomTypeAvailability/sync';
        
        const roomTypeInput = document.createElement('input');
        roomTypeInput.type = 'hidden';
        roomTypeInput.name = 'syncRoomTypeId';
        roomTypeInput.value = roomTypeId;
        
        const dateInput = document.createElement('input');
        dateInput.type = 'hidden';
        dateInput.name = 'syncTargetDate';
        dateInput.value = availabilityDate;
        
        form.appendChild(roomTypeInput);
        form.appendChild(dateInput);
        document.body.appendChild(form);
        form.submit();
    }
}

/**
 * 刪除庫存記錄
 * @param {HTMLElement} button - 觸發刪除的按鈕元素
 */
function deleteRecord(button) {
    const roomTypeId = button.getAttribute('data-room-type-id');
    const availabilityDate = button.getAttribute('data-availability-date');
    
    if (confirm('確定要刪除此庫存記錄嗎？')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/backend/roomTypeAvailability/delete';
        
        const roomTypeInput = document.createElement('input');
        roomTypeInput.type = 'hidden';
        roomTypeInput.name = 'roomTypeId';
        roomTypeInput.value = roomTypeId;
        
        const dateInput = document.createElement('input');
        dateInput.type = 'hidden';
        dateInput.name = 'availabilityDate';
        dateInput.value = availabilityDate;
        
        form.appendChild(roomTypeInput);
        form.appendChild(dateInput);
        document.body.appendChild(form);
        form.submit();
    }
}

/**
 * 單元格直接編輯功能 - 實時庫存版本
 * @param {HTMLElement} element - 可用性單元格元素
 */
function editCell(element) {
    const roomTypeId = element.getAttribute('data-room-type-id');
    const roomTypeName = element.getAttribute('data-room-type-name');
    const date = element.getAttribute('data-availability-date');
    const realTimeCount = element.getAttribute('data-availability-count');
    const staticCount = element.getAttribute('data-static-count');
    
    // 構建提示信息
    let promptText = `${roomTypeName} 在 ${date} 的庫存狀態：\n\n`;
    promptText += `🔄 實時可用數量：${realTimeCount} 間\n`;
    if (staticCount && staticCount !== 'null') {
        promptText += `⚙️ 靜態設定數量：${staticCount} 間\n\n`;
        promptText += `您要設定新的靜態庫存數量：`;
    } else {
        promptText += `⚙️ 靜態設定：未設定\n\n`;
        promptText += `請輸入要設定的靜態庫存數量：`;
    }
    
    const newCount = prompt(promptText, staticCount || realTimeCount || '0');
    
    if (newCount !== null && !isNaN(newCount) && parseInt(newCount) >= 0) {
        // 檢查是否為新建記錄
        const isNewRecord = !staticCount || staticCount === 'null';
        const action = isNewRecord ? '/backend/roomTypeAvailability/add' : '/backend/roomTypeAvailability/update';
        
        // 確認操作
        const confirmText = isNewRecord 
            ? `確定要為 ${roomTypeName} 在 ${date} 新增庫存設定 ${newCount} 間嗎？`
            : `確定要將 ${roomTypeName} 在 ${date} 的庫存設定從 ${staticCount} 間更新為 ${newCount} 間嗎？`;
        
        if (confirm(confirmText)) {
            // 發送更新請求
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = action;
            
            const roomTypeInput = document.createElement('input');
            roomTypeInput.type = 'hidden';
            roomTypeInput.name = 'roomTypeId';
            roomTypeInput.value = roomTypeId;
            
            const dateInput = document.createElement('input');
            dateInput.type = 'hidden';
            dateInput.name = 'availabilityDate';
            dateInput.value = date;
            
            const countInput = document.createElement('input');
            countInput.type = 'hidden';
            countInput.name = 'availabilityCount';
            countInput.value = parseInt(newCount);
            
            form.appendChild(roomTypeInput);
            form.appendChild(dateInput);
            form.appendChild(countInput);
            document.body.appendChild(form);
            form.submit();
        }
    }
}

/**
 * 顯示右鍵菜單
 * @param {Event} event - 右鍵點擊事件
 * @param {HTMLElement} element - 被右鍵點擊的元素
 */
function showContextMenu(event, element) {
    // 移除舊的菜單
    const oldMenu = document.getElementById('contextMenu');
    if (oldMenu) {
        oldMenu.remove();
    }
    
    // 創建新菜單
    const menu = document.createElement('div');
    menu.id = 'contextMenu';
    menu.className = 'dropdown-menu show';
    menu.style.position = 'fixed';
    menu.style.left = event.clientX + 'px';
    menu.style.top = event.clientY + 'px';
    menu.style.zIndex = '9999';
    
    const roomTypeId = element.getAttribute('data-room-type-id');
    const roomTypeName = element.getAttribute('data-room-type-name');
    const date = element.getAttribute('data-availability-date');
    const count = element.getAttribute('data-availability-count');
    const staticCount = element.getAttribute('data-static-count');
    
    menu.innerHTML = `
        <h6 class="dropdown-header">${roomTypeName} - ${date}</h6>
        <div class="dropdown-item-text small">
            <div><strong>🔄 實時可用：</strong>${count} 間</div>
            <div><strong>⚙️ 靜態設定：</strong>${staticCount && staticCount !== 'null' ? staticCount + ' 間' : '未設定'}</div>
        </div>
        <div class="dropdown-divider"></div>
        <button class="dropdown-item" onclick="editCell(this)" 
                data-room-type-id="${roomTypeId}"
                data-room-type-name="${roomTypeName}"
                data-availability-date="${date}"
                data-availability-count="${count}"
                data-static-count="${staticCount}">
            <i class="bi bi-pencil me-2"></i>編輯靜態庫存設定
        </button>
        <button class="dropdown-item" onclick="openUpdateModal(this)"
                data-room-type-id="${roomTypeId}"
                data-room-type-name="${roomTypeName}"
                data-availability-date="${date}"
                data-availability-count="${staticCount || count}"
                data-static-count="${staticCount}">
            <i class="bi bi-modal me-2"></i>使用表單編輯
        </button>
        <div class="dropdown-divider"></div>
        <button class="dropdown-item" onclick="syncSingleRecord(this)"
                data-room-type-id="${roomTypeId}"
                data-availability-date="${date}">
            <i class="bi bi-arrow-clockwise me-2"></i>重新計算庫存
        </button>
        ${staticCount && staticCount !== 'null' ? `
        <button class="dropdown-item text-danger" onclick="deleteRecord(this)"
                data-room-type-id="${roomTypeId}"
                data-availability-date="${date}">
            <i class="bi bi-trash me-2"></i>刪除靜態設定
        </button>
        ` : ''}
    `;
    
    document.body.appendChild(menu);
}

/**
 * 月份切換功能
 */
function changeMonth() {
    const monthInput = document.querySelector('input[name="selectedMonth"]');
    if (monthInput && monthInput.value) {
        // 自動提交表單切換月份
        monthInput.closest('form').submit();
    }
}

/**
 * 初始化月份選擇器預設值
 */
function initializeMonthSelector() {
    const monthInput = document.querySelector('input[name="selectedMonth"]');
    if (monthInput && !monthInput.value) {
        // 如果沒有預設值，設置為當前月份
        const now = new Date();
        const currentMonth = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0');
        monthInput.value = currentMonth;
    }
}

/**
 * 初始化事件監聽器
 */
document.addEventListener('DOMContentLoaded', function() {
    // 初始化月份選擇器
    initializeMonthSelector();
    
    // 為月份選擇器添加改變事件
    const monthInput = document.querySelector('input[name="selectedMonth"]');
    if (monthInput) {
        monthInput.addEventListener('change', changeMonth);
    }
    
    // 為所有 availability-cell 添加右鍵事件
    document.addEventListener('contextmenu', function(e) {
        if (e.target.classList.contains('availability-cell')) {
            e.preventDefault();
            showContextMenu(e, e.target);
        }
    });
    
    // 點擊其他地方隱藏右鍵菜單
    document.addEventListener('click', function() {
        const contextMenu = document.getElementById('contextMenu');
        if (contextMenu) {
            contextMenu.remove();
        }
    });
    
    // 自動隱藏提示訊息
    setTimeout(function() {
        const alerts = document.querySelectorAll('.alert-dismissible');
        alerts.forEach(function(alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);
}); 