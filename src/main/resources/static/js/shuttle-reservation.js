// 快速填入測試資料
function fillTestData(memberId, roomReservationId) {
    document.getElementById('memberId').value = memberId;
    document.getElementById('roomReservationId').value = roomReservationId;
}

// 座位選擇相關功能
let selectedSeats = [];
let requiredSeats = 0;

// 初始化座位選擇功能
function initializeSeatSelection() {
    const seatCheckboxes = document.querySelectorAll('input[name="selectedSeatIds"]');
    const requiredSeatsElement = document.querySelector('[th\\:text="${requiredSeats}"]');
    const confirmButton = document.getElementById('confirmSeatBtn');
    
    if (requiredSeatsElement) {
        requiredSeats = parseInt(requiredSeatsElement.textContent) || 0;
    }
    
    // 根據資料庫seat_status設置座位狀態
    document.querySelectorAll('.seat').forEach(seat => {
        const seatId = seat.getAttribute('data-seat-id');
        const seatStatus = seat.getAttribute('data-seat-status'); // 需要在HTML中添加
        
        if (seatStatus === '0') { // 故障
            seat.classList.add('fault');
            seat.classList.remove('available', 'normal');
        } else if (seatStatus === '1') { // 正常
            seat.classList.add('normal');
            if (!seat.classList.contains('occupied')) {
                seat.classList.add('available');
            }
        }
    });
    
    seatCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            updateSeatSelection();
        });
    });
    
    updateSeatSelection();
}

// 更新座位選擇狀態
function updateSeatSelection() {
    const selectedCheckboxes = document.querySelectorAll('input[name="selectedSeatIds"]:checked');
    const selectedSeatsDiv = document.getElementById('selectedSeats');
    const confirmButton = document.getElementById('confirmSeatBtn');
    
    selectedSeats = Array.from(selectedCheckboxes);
    
    if (selectedSeats.length === 0) {
        selectedSeatsDiv.innerHTML = '<p class="text-muted">尚未選擇座位</p>';
    } else {
        const seatNumbers = selectedSeats.map(checkbox => {
            const label = document.querySelector(`label[for="${checkbox.id}"]`);
            return label ? label.textContent : checkbox.value;
        });
        
        selectedSeatsDiv.innerHTML = seatNumbers.map(number =>
            `<span class="badge bg-primary me-1">${number}</span>`
        ).join('');
    }
    
    // 更新座位視覺效果
    document.querySelectorAll('.seat.available').forEach(seat => {
        const checkbox = seat.querySelector('input[type="checkbox"]');
        if (checkbox) {
            if (checkbox.checked) {
                seat.classList.add('selected');
            } else {
                seat.classList.remove('selected');
            }
        }
    });
    
    // 限制選擇數量
    const allCheckboxes = document.querySelectorAll('input[name="selectedSeatIds"]');
    allCheckboxes.forEach(checkbox => {
        if (!checkbox.checked && selectedSeats.length >= requiredSeats) {
            checkbox.disabled = true;
            checkbox.closest('.seat').style.opacity = '0.5';
        } else if (!checkbox.checked) {
            checkbox.disabled = false;
            checkbox.closest('.seat').style.opacity = '1';
        }
    });
    
    // 啟用/禁用確認按鈕
    if (confirmButton) {
        confirmButton.disabled = selectedSeats.length !== requiredSeats;
    }
}

// 班次選擇功能
function initializeScheduleSelection() {
    const scheduleCards = document.querySelectorAll('.schedule-card');
    const radioButtons = document.querySelectorAll('input[name="selectedScheduleId"]');
    
    scheduleCards.forEach(card => {
        card.addEventListener('click', function() {
            const radio = card.querySelector('input[type="radio"]');
            if (radio) {
                radio.checked = true;
                updateScheduleSelection();
            }
        });
    });
    
    radioButtons.forEach(radio => {
        radio.addEventListener('change', updateScheduleSelection);
    });
}

// 更新班次選擇狀態
function updateScheduleSelection() {
    const selectedRadio = document.querySelector('input[name="selectedScheduleId"]:checked');
    const scheduleCards = document.querySelectorAll('.schedule-card');
    
    scheduleCards.forEach(card => {
        card.classList.remove('selected');
        const radio = card.querySelector('input[type="radio"]');
        if (radio && radio.checked) {
            card.classList.add('selected');
        }
    });
}

// 初始化日期限制
function initializeDateRestrictions() {
    const dateInput = document.getElementById('shuttleDate');
    if (dateInput) {
        const today = new Date().toISOString().split('T')[0];
        dateInput.min = today;
    }
}

// 文檔加載完成後初始化
document.addEventListener('DOMContentLoaded', function() {
    initializeDateRestrictions();
    initializeScheduleSelection();
    initializeSeatSelection();
    
    // 新增表單驗證事件監聽
    const reservationForm = document.querySelector('form[th\\:action="@{/reservation/validate}"]');
    if (reservationForm) {
        reservationForm.addEventListener('submit', function(e) {
            if (!validateReservationForm()) {
                e.preventDefault();
            }
        });
    }
    
    // 自動關閉Alert（保持原有功能）
    setTimeout(function() {
        const alerts = document.querySelectorAll('.alert:not(.alert-info)');
        alerts.forEach(alert => {
            const closeBtn = alert.querySelector('.btn-close');
            if (closeBtn) {
                closeBtn.click();
            }
        });
    }, 5000);
});

const ROOM_ORDER_STATUS = {
    0: '成立',
    1: '付款中', 
    2: '完成',
    3: '退款',
    4: '取消'
};

const SHUTTLE_DIRECTION = {
    0: '去程',
    1: '回程'
};

const SEAT_STATUS = {
    0: '故障',
    1: '正常'
};

const MEMBER_STATUS = {
    0: '未驗證',
    1: '已驗證',
    2: '停用'
};

// 驗證訂房狀態
function validateRoomReservationStatus(roomOrderStatus) {
    // 只有狀態為2(完成)的訂房才能預約接駁
    return roomOrderStatus === 2;
}

// 格式化日期時間
function formatDateTime(dateTime, format = 'YYYY-MM-DD HH:mm:ss') {
    if (!dateTime) return '';
    const date = new Date(dateTime);
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    
    switch(format) {
        case 'YYYY-MM-DD':
            return `${year}-${month}-${day}`;
        case 'HH:mm':
            return `${hours}:${minutes}`;
        case 'HH:mm:ss':
            return `${hours}:${minutes}:${seconds}`;
        default:
            return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    }
}

// 檢查住宿日期範圍
function validateShuttleDateRange(shuttleDate, checkInDate, checkOutDate) {
    const shuttle = new Date(shuttleDate);
    const checkIn = new Date(checkInDate);
    const checkOut = new Date(checkOutDate);
    
    return shuttle >= checkIn && shuttle <= checkOut;
}

// 顯示訊息
function showMessage(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type === 'error' ? 'danger' : 'success'} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    const container = document.querySelector('.container');
    container.insertBefore(alertDiv, container.firstChild);
    
    // 自動移除訊息
    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}

// 表單驗證
function validateReservationForm() {
    const memberId = document.getElementById('memberId')?.value;
    const roomReservationId = document.getElementById('roomReservationId')?.value;
    const shuttleDate = document.getElementById('shuttleDate')?.value;
    const shuttleNumber = document.getElementById('shuttleNumber')?.value;
    const shuttleDirection = document.querySelector('input[name="shuttleDirection"]:checked')?.value;
    
    // 基本欄位驗證
    if (!memberId || !roomReservationId || !shuttleDate || !shuttleNumber || shuttleDirection === undefined) {
        showMessage('請填寫所有必填欄位', 'error');
        return false;
    }
    
    // 數字欄位驗證
    if (isNaN(memberId) || isNaN(roomReservationId) || isNaN(shuttleNumber)) {
        showMessage('會員編號、訂房編號和人數必須為有效數字', 'error');
        return false;
    }
    
    // 日期驗證
    const today = new Date();
    const selectedDate = new Date(shuttleDate);
    if (selectedDate < today) {
        showMessage('接駁日期不能是過去的日期', 'error');
        return false;
    }
    
    return true;
}

// 更新座位網格
function updateSeatGrid(availabilityData) {
    document.querySelectorAll('.seat').forEach(seat => {
        const seatId = seat.getAttribute('data-seat-id');
        const seatData = availabilityData.seats?.find(s => s.seatId == seatId);
        
        if (seatData) {
            // 移除所有狀態class
            seat.classList.remove('available', 'occupied', 'fault', 'normal');
            
            // 根據數據庫狀態設置class
            if (seatData.seatStatus === 0) { // 故障
                seat.classList.add('fault');
            } else if (seatData.occupied) { // 已被預約
                seat.classList.add('occupied');
            } else { // 可選擇
                seat.classList.add('available', 'normal');
            }
        }
    });
}

// 座位可用性檢查
function checkSeatAvailability(scheduleId, shuttleDate) {
    // 這個函數需要與後端API配合
    // 檢查特定班次和日期的座位可用性
    fetch(`/api/seat-availability?scheduleId=${scheduleId}&date=${shuttleDate}`)
        .then(response => response.json())
        .then(data => {
            updateSeatGrid(data);
        })
        .catch(error => {
            console.error('檢查座位可用性時發生錯誤:', error);
            showMessage('無法載入座位資訊，請重新整理頁面', 'error');
        });
}


