// 日期範圍更新
function updateDateRange() {
    const roomSelect = document.querySelector('select[name="roomReservationId"]');
    const dateInput = document.querySelector('input[name="shuttleDate"]');

    if (!roomSelect || !dateInput) return;

    const selectedOption = roomSelect.options[roomSelect.selectedIndex];

    if (selectedOption && selectedOption.value) {
        const checkInDate = selectedOption.getAttribute('data-checkin');
        const checkOutDate = selectedOption.getAttribute('data-checkout');

        if (checkInDate && checkOutDate) {
            dateInput.min = checkInDate;
            dateInput.max = checkOutDate;
            dateInput.disabled = false;

            // 更新提示文字
            const helpText = dateInput.parentNode.querySelector('.form-text');
            if (helpText) {
                helpText.textContent = `可選擇日期範圍：${checkInDate} 至 ${checkOutDate}`;
                helpText.style.color = '#28a745';
            }
        }
    } else {
        dateInput.disabled = true;
        dateInput.value = '';
        const helpText = dateInput.parentNode.querySelector('.form-text');
        if (helpText) {
            helpText.textContent = '請先選擇訂房記錄';
            helpText.style.color = '#6c757d';
        }
    }
}

// 訂房選擇功能

document.querySelectorAll('button[data-room-id]').forEach(button => {
    button.addEventListener('click', function () {
        const roomId = this.dataset.roomId;
        const checkIn = this.dataset.checkIn;
        const checkOut = this.dataset.checkOut;
        selectRoomReservation(roomId, checkIn, checkOut);
    });
});


function selectRoomReservation(roomReservationId, checkInDate, checkOutDate) {
    // 設置訂房ID
    const roomSelect = document.querySelector('select[name="roomReservationId"]');
    if (roomSelect) {
        roomSelect.value = roomReservationId;
        updateDateRange();
    }

    // 滾動到表單區域
    document.querySelector('#reservationForm').scrollIntoView({
        behavior: 'smooth'
    });
}

// 座位選擇相關功能
let selectedSeats = [];
let requiredSeats = 0;

// 初始化座位選擇功能
function initializeSeatSelection() {
    const seatContainer = document.getElementById('seatGrid');
    if (!seatContainer) return;

    const requiredSeatsElement = document.querySelector('[th\\:text="${requiredSeats}"]');
    const confirmButton = document.getElementById('confirmSeatBtn');

    if (requiredSeatsElement) {
        requiredSeats = parseInt(requiredSeatsElement.textContent) || 0;
    }

    // 為每個座位添加點擊事件
    seatContainer.addEventListener('click', function (e) {
        const seat = e.target.closest('.seat');
        if (!seat || !seat.classList.contains('available')) return;

        const checkbox = seat.querySelector('input[type="checkbox"]');
        if (!checkbox) return;

        // 切換選擇狀態
        if (checkbox.checked) {
            checkbox.checked = false;
        } else {
            // 檢查是否已達到最大選擇數量
            const selectedCount = document.querySelectorAll('input[name="selectedSeatIds"]:checked').length;
            if (selectedCount >= requiredSeats) {
                showMessage(`最多只能選擇 ${requiredSeats} 個座位`, 'warning');
                return;
            }
            checkbox.checked = true;
        }

        updateSeatSelection();
    });

    // 初始化狀態
    updateSeatSelection();
}

// 更新座位選擇狀態
function updateSeatSelection() {
    const selectedCheckboxes = document.querySelectorAll('input[name="selectedSeatIds"]:checked');
    const selectedSeatsDiv = document.getElementById('selectedSeats');
    const confirmButton = document.getElementById('confirmSeatBtn');

    selectedSeats = Array.from(selectedCheckboxes);

    // 更新選擇座位顯示
    if (selectedSeats.length === 0) {
        selectedSeatsDiv.innerHTML = '<p class="text-muted mb-0">尚未選擇座位</p>';
    } else {
        const seatNumbers = selectedSeats.map(checkbox => {
            const seat = checkbox.closest('.seat');
            return seat ? seat.textContent.trim() : checkbox.value;
        });

        selectedSeatsDiv.innerHTML = `
            <div class="mb-2">
                ${seatNumbers.map(number =>
            `<span class="badge bg-primary me-1 mb-1">${number}</span>`
        ).join('')}
            </div>
            <small class="text-success">已選擇 ${selectedSeats.length}/${requiredSeats} 個座位</small>
        `;
    }

    // 更新座位視覺效果
    document.querySelectorAll('.seat').forEach(seat => {
        const checkbox = seat.querySelector('input[type="checkbox"]');
        if (checkbox) {
            seat.classList.toggle('selected', checkbox.checked);
        }
    });

    // 啟用/禁用確認按鈕
    if (confirmButton) {
        confirmButton.disabled = selectedSeats.length !== requiredSeats;

        if (selectedSeats.length === requiredSeats) {
            confirmButton.innerHTML = '<i class="fas fa-check me-2"></i>確認選擇';
            confirmButton.classList.remove('btn-outline-primary');
            confirmButton.classList.add('btn-primary');
        } else {
            confirmButton.innerHTML = `<i class="fas fa-chair me-2"></i>請選擇座位 (${selectedSeats.length}/${requiredSeats})`;
            confirmButton.classList.remove('btn-primary');
            confirmButton.classList.add('btn-outline-primary');
        }
    }
}

// 班次選擇功能
function initializeScheduleSelection() {
    const scheduleCards = document.querySelectorAll('.schedule-card');

    scheduleCards.forEach(card => {
        card.addEventListener('click', function () {
            // 移除其他卡片的選擇狀態
            scheduleCards.forEach(c => c.classList.remove('selected'));

            // 選擇當前卡片
            card.classList.add('selected');

            // 設置對應的radio按鈕
            const radio = card.querySelector('input[type="radio"]');
            if (radio) {
                radio.checked = true;
            }
        });
    });
}

// 表單驗證
function validateReservationForm() {
    const form = document.querySelector('form[action*="/validate"]');
    if (!form) return true;

    let isValid = true;
    const errors = [];

    // 檢查訂房選擇
    const roomSelect = form.querySelector('select[name="roomReservationId"]');
    if (!roomSelect || !roomSelect.value) {
        errors.push('請選擇訂房記錄');
        isValid = false;
    }

    // 檢查接駁日期
    const dateInput = form.querySelector('input[name="shuttleDate"]');
    if (!dateInput || !dateInput.value) {
        errors.push('請選擇接駁日期');
        isValid = false;
    } else {
        const selectedDate = new Date(dateInput.value);
        const minDate = new Date(dateInput.min);
        const maxDate = new Date(dateInput.max);

        if (selectedDate < minDate || selectedDate > maxDate) {
            errors.push('接駁日期必須在住宿期間內');
            isValid = false;
        }
    }

    // 檢查人數
    const numberSelect = form.querySelector('select[name="shuttleNumber"]');
    if (!numberSelect || !numberSelect.value) {
        errors.push('請選擇接駁人數');
        isValid = false;
    }

    // 檢查方向
    const directionRadio = form.querySelector('input[name="shuttleDirection"]:checked');
    if (!directionRadio) {
        errors.push('請選擇接駁方向');
        isValid = false;
    }

    if (!isValid) {
        showMessage(errors.join('、'), 'error');
    }

    return isValid;
}

// 顯示訊息
function showMessage(message, type = 'info') {
    // 移除現有的訊息
    const existingAlerts = document.querySelectorAll('.alert.dynamic-alert');
    existingAlerts.forEach(alert => alert.remove());

    const alertClass = type === 'error' ? 'alert-danger' :
        type === 'warning' ? 'alert-warning' :
            type === 'success' ? 'alert-success' : 'alert-info';

    const alertDiv = document.createElement('div');
    alertDiv.className = `alert ${alertClass} alert-dismissible fade show dynamic-alert`;
    alertDiv.style.position = 'fixed';
    alertDiv.style.top = '20px';
    alertDiv.style.right = '20px';
    alertDiv.style.zIndex = '9999';
    alertDiv.style.minWidth = '300px';
    alertDiv.innerHTML = `
        <div class="d-flex align-items-center">
            <i class="fas fa-${type === 'error' ? 'exclamation-circle' :
            type === 'warning' ? 'exclamation-triangle' :
                type === 'success' ? 'check-circle' : 'info-circle'} me-2"></i>
            <div>${message}</div>
        </div>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    document.body.appendChild(alertDiv);

    // 自動移除訊息
    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}

// 文檔加載完成後初始化
document.addEventListener('DOMContentLoaded', function () {
    initializeScheduleSelection();
    initializeSeatSelection();

    // 訂房選擇變更事件
    const roomSelect = document.querySelector('select[name="roomReservationId"]');
    if (roomSelect) {
        roomSelect.addEventListener('change', updateDateRange);
    }

    // 表單驗證事件監聽
    const reservationForm = document.querySelector('form[th\\:action="@{/reservation/validate}"]');
    if (reservationForm) {
        reservationForm.addEventListener('submit', function (e) {
            if (!validateReservationForm()) {
                e.preventDefault();
            }
        });
    }

    // 座位表單提交驗證
    const seatForm = document.getElementById('seatForm');
    if (seatForm) {
        seatForm.addEventListener('submit', function (e) {
            const selectedCount = document.querySelectorAll('input[name="selectedSeatIds"]:checked').length;
            if (selectedCount !== requiredSeats) {
                e.preventDefault();
                showMessage(`請選擇 ${requiredSeats} 個座位`, 'warning');
            }
        });
    }

    // 自動關閉Alert
    setTimeout(function () {
        const alerts = document.querySelectorAll('.alert:not(.alert-info):not(.dynamic-alert)');
        alerts.forEach(alert => {
            const closeBtn = alert.querySelector('.btn-close');
            if (closeBtn) {
                closeBtn.click();
            }
        });
    }, 5000);
});