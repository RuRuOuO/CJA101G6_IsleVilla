// 座位選擇相關功能
let selectedSeats = [];
let requiredSeats = 0;

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
			const label = seat ? seat.querySelector('label') : null;
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

// 初始化座位選擇功能
function initializeSeatSelection() {
    const seatContainer = document.getElementById('seatGrid');
    if (!seatContainer) return;

	// 獲取所需座位數
	const requiredSeatsText = seatContainer.closest('.card-body').querySelector('small.text-muted');
    if (requiredSeatsText) {
        const match = requiredSeatsText.textContent.match(/需選擇\s+(\d+)\s+個座位/);
        if (match) {
            requiredSeats = parseInt(match[1]) || 0;
        }
    }

    console.log('Required seats:', requiredSeats);
	
//    const requiredSeatsElement = document.querySelector('[th\\:text="${requiredSeats}"]');
//    const confirmButton = document.getElementById('confirmSeatBtn');
//
//    if (requiredSeatsElement) {
//        requiredSeats = parseInt(requiredSeatsElement.textContent) || 0;
//    }

    // 為每個座位添加點擊事件
    seatContainer.addEventListener('click', function (e) {
		e.preventDefault();
        e.stopPropagation();
		
        const seat = e.target.closest('.seat');
		console.log('Clicked seat:', seat);
		
		if (!seat) return;
		
		// 檢查座位狀態
		if (seat.classList.contains('occupied') || seat.classList.contains('disabled')) {
            showMessage('此座位無法選擇', 'warning');
            return;
        }
        
        if (!seat.classList.contains('available') && !seat.classList.contains('selected')) {
            showMessage('此座位不可選擇', 'warning');
            return;
        }

        const checkbox = seat.querySelector('input[type="checkbox"]');
        if (!checkbox) {
            console.log('No checkbox found in seat');
            return;
        }
		
//        if (!seat || !seat.classList.contains('available')) return;
//
//        const checkbox = seat.querySelector('input[type="checkbox"]');
//        if (!checkbox) return;

        // 切換選擇狀態
        if (checkbox.checked) {
            checkbox.checked = false;
			console.log('Unchecked seat');
        } else {
            // 檢查是否已達到最大選擇數量
            const selectedCount = document.querySelectorAll('input[name="selectedSeatIds"]:checked').length;
            if (selectedCount >= requiredSeats) {
                showMessage(`最多只能選擇 ${requiredSeats} 個座位`, 'warning');
                return;
            }
            checkbox.checked = true;
			console.log('Checked seat');
        }

        updateSeatSelection();
    });

	// 為座位的label添加點擊事件處理
    const seatLabels = seatContainer.querySelectorAll('.seat label');
    seatLabels.forEach(label => {
        label.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            const seat = label.closest('.seat');
            const checkbox = seat.querySelector('input[type="checkbox"]');
            
            if (!checkbox) return;
            
            // 檢查座位狀態
            if (seat.classList.contains('occupied') || seat.classList.contains('disabled')) {
                return;
            }
            
            // 切換選擇狀態
            if (checkbox.checked) {
                checkbox.checked = false;
            } else {
                const selectedCount = document.querySelectorAll('input[name="selectedSeatIds"]:checked').length;
                if (selectedCount >= requiredSeats) {
                    showMessage(`最多只能選擇 ${requiredSeats} 個座位`, 'warning');
                    return;
                }
                checkbox.checked = true;
            }
            
            updateSeatSelection();
        });
    });
	
    // 初始化狀態
    updateSeatSelection();
}
//

//
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

//
//// 表單驗證
//function validateReservationForm() {
//    const form = document.querySelector('form[action*="/validate"]');
//    if (!form) return true;
//
//    let isValid = true;
//    const errors = [];
//
//    // 檢查訂房選擇
//    const roomSelect = form.querySelector('select[name="roomReservationId"]');
//    if (!roomSelect || !roomSelect.value) {
//        errors.push('請選擇訂房記錄');
//        isValid = false;
//    }
//
//    // 檢查接駁日期
//    const dateInput = form.querySelector('input[name="shuttleDate"]');
//    if (!dateInput || !dateInput.value) {
//        errors.push('請選擇接駁日期');
//        isValid = false;
//    } else {
//        const selectedDate = new Date(dateInput.value);
//        const minDate = new Date(dateInput.min);
//        const maxDate = new Date(dateInput.max);
//
//        if (selectedDate < minDate || selectedDate > maxDate) {
//            errors.push('接駁日期必須在住宿期間內');
//            isValid = false;
//        }
//    }
//
//    // 檢查人數
//    const numberSelect = form.querySelector('select[name="shuttleNumber"]');
//    if (!numberSelect || !numberSelect.value) {
//        errors.push('請選擇接駁人數');
//        isValid = false;
//    }
//
//    // 檢查方向
//    const directionRadio = form.querySelector('input[name="shuttleDirection"]:checked');
//    if (!directionRadio) {
//        errors.push('請選擇接駁方向');
//        isValid = false;
//    }
//
//    if (!isValid) {
//        showMessage(errors.join('、'), 'error');
//    }
//
//    return isValid;
//}
//

//// 文檔加載完成後初始化
//document.addEventListener('DOMContentLoaded', function () {
//    initializeScheduleSelection();
//    initializeSeatSelection();
//
//    // 訂房選擇變更事件
//    const roomSelect = document.querySelector('select[name="roomReservationId"]');
//    if (roomSelect) {
//        roomSelect.addEventListener('change', updateDateRange);
//    }
//
//    // 表單驗證事件監聽
//    const reservationForm = document.querySelector('form[th\\:action="@{/reservation/validate}"]');
//    if (reservationForm) {
//        reservationForm.addEventListener('submit', function (e) {
//            if (!validateReservationForm()) {
//                e.preventDefault();
//            }
//        });
//    }
//
//    // 座位表單提交驗證
//    const seatForm = document.getElementById('seatForm');
//    if (seatForm) {
//        seatForm.addEventListener('submit', function (e) {
//            const selectedCount = document.querySelectorAll('input[name="selectedSeatIds"]:checked').length;
//            if (selectedCount !== requiredSeats) {
//                e.preventDefault();
//                showMessage(`請選擇 ${requiredSeats} 個座位`, 'warning');
//            }
//        });
//    }
//
//    // 自動關閉Alert
//    setTimeout(function () {
//        const alerts = document.querySelectorAll('.alert:not(.alert-info):not(.dynamic-alert)');
//        alerts.forEach(alert => {
//            const closeBtn = alert.querySelector('.btn-close');
//            if (closeBtn) {
//                closeBtn.click();
//            }
//        });
//    }, 5000);
//});

document.addEventListener('DOMContentLoaded', function() {
	
	// 班次選擇功能
	initializeScheduleSelection()
	// 初始化座位選擇功能
	initializeSeatSelection()
	
    const selectRoomBtns = document.querySelectorAll('.select-room-btn');
    const submitBtn = document.getElementById('submitBtn');
    const selectedRoomInfo = document.getElementById('selectedRoomInfo');
    
    // 接駁方向選項
    const directionOutward = document.getElementById('direction-outward');
    const directionReturn = document.getElementById('direction-return');
	if (selectRoomBtns.length > 0) {
	    selectRoomBtns.forEach(btn => {
	        btn.addEventListener('click', function() {
	            // 移除其他按鈕的 active 狀態
	            selectRoomBtns.forEach(b => b.classList.remove('btn-success'));
	            selectRoomBtns.forEach(b => b.classList.add('btn-primary'));
	            selectRoomBtns.forEach(b => b.textContent = '選擇此訂房');
	            
	            // 設定當前按鈕為 active
	            this.classList.remove('btn-primary');
	            this.classList.add('btn-success');
	            this.textContent = '✓ 已選擇';
	
	            // 獲取訂房資料
	            const roomReservationId = this.getAttribute('data-room-reservation-id');
	            const checkInDate = this.getAttribute('data-check-in-date');
	            const checkOutDate = this.getAttribute('data-check-out-date');
	
	            // 設定隱藏欄位值
//	            document.getElementById('selectedRoomReservationId').value = roomReservationId;
				const selectedRoomField = document.getElementById('selectedRoomReservationId');
                if (selectedRoomField) {
                    selectedRoomField.value = roomReservationId;
                }
				
	            // 顯示選擇的訂房資訊
//	            document.getElementById('displayRoomId').textContent = roomReservationId;
//	            document.getElementById('displayCheckIn').textContent = checkInDate;
//	            document.getElementById('displayCheckOut').textContent = checkOutDate;
//	            selectedRoomInfo.style.display = 'block';
				
				const displayElements = {
                    roomId: document.getElementById('displayRoomId'),
                    checkIn: document.getElementById('displayCheckIn'),
                    checkOut: document.getElementById('displayCheckOut')
                };

                if (displayElements.roomId) displayElements.roomId.textContent = roomReservationId;
                if (displayElements.checkIn) displayElements.checkIn.textContent = checkInDate;
                if (displayElements.checkOut) displayElements.checkOut.textContent = checkOutDate;
                if (selectedRoomInfo) selectedRoomInfo.style.display = 'block';
	
	            // 重置接駁方向選擇
//	            directionOutward.checked = false;
//	            directionReturn.checked = false;
//	            document.getElementById('displayShuttleDate').textContent = '';
//	            document.getElementById('selectedShuttleDate').value = '';
				
				if (directionOutward) directionOutward.checked = false;
                if (directionReturn) directionReturn.checked = false;
                
                const displayShuttleDate = document.getElementById('displayShuttleDate');
                const selectedShuttleDate = document.getElementById('selectedShuttleDate');
                if (displayShuttleDate) displayShuttleDate.textContent = '';
                if (selectedShuttleDate) selectedShuttleDate.value = '';
	
	            // 檢查是否可以啟用提交按鈕
	            checkSubmitButton();
	        });
	    });
	}
    // 監聽接駁方向變更
//    directionOutward.addEventListener('change', function() {
//        if (this.checked) {
//            updateShuttleDate();
//        }
//    });
	if (directionOutward) {
        directionOutward.addEventListener('change', function() {
            if (this.checked) {
                updateShuttleDate();
            }
        });
    }

//    directionReturn.addEventListener('change', function() {
//        if (this.checked) {
//            updateShuttleDate();
//        }
//    });
	if (directionReturn) {
        directionReturn.addEventListener('change', function() {
            if (this.checked) {
                updateShuttleDate();
            }
        });
    }

    function updateShuttleDate() {
        const selectedRoom = document.querySelector('.select-room-btn.btn-success');
        if (!selectedRoom) return;

        const checkInDate = selectedRoom.getAttribute('data-check-in-date');
        const checkOutDate = selectedRoom.getAttribute('data-check-out-date');
        
        let shuttleDate;
//        if (directionOutward.checked) {
//            shuttleDate = checkInDate;
//        } else if (directionReturn.checked) {
//            shuttleDate = checkOutDate;
//        }
		if (directionOutward && directionOutward.checked) {
            shuttleDate = checkInDate;
        } else if (directionReturn && directionReturn.checked) {
            shuttleDate = checkOutDate;
        }

        if (shuttleDate) {
//            document.getElementById('selectedShuttleDate').value = shuttleDate;
//            document.getElementById('displayShuttleDate').textContent = shuttleDate;
			const selectedShuttleDate = document.getElementById('selectedShuttleDate');
            const displayShuttleDate = document.getElementById('displayShuttleDate');
            
            if (selectedShuttleDate) selectedShuttleDate.value = shuttleDate;
            if (displayShuttleDate) displayShuttleDate.textContent = shuttleDate;
			
            checkSubmitButton();
        }
    }

    function checkSubmitButton() {
		if (!submitBtn) return;
		
        const roomSelected = document.querySelector('.select-room-btn.btn-success');
//        const directionSelected = directionOutward.checked || directionReturn.checked;
		const directionSelected = (directionOutward && directionOutward.checked) || 
                                 (directionReturn && directionReturn.checked);
 		const shuttleNumberSelect = document.querySelector('select[name="shuttleNumber"]');
//        const shuttleNumberSelected = document.querySelector('select[name="shuttleNumber"]').value;
		const shuttleNumberSelected = shuttleNumberSelect ? shuttleNumberSelect.value : false;

        if (roomSelected && directionSelected && shuttleNumberSelected) {
            submitBtn.disabled = false;
        } else {
            submitBtn.disabled = true;
        }
    }

    // 監聽接駁人數變更
//    document.querySelector('select[name="shuttleNumber"]').addEventListener('change', checkSubmitButton);
	const shuttleNumberSelect = document.querySelector('select[name="shuttleNumber"]');
    if (shuttleNumberSelect) {
        shuttleNumberSelect.addEventListener('change', checkSubmitButton);
    }
	
	// 座位表單提交驗證
    const seatForm = document.getElementById('seatForm');
    if (seatForm) {
        seatForm.addEventListener('submit', function (e) {
            const selectedCount = document.querySelectorAll('input[name="selectedSeatIds"]:checked').length;
            if (selectedCount !== requiredSeats) {
                e.preventDefault();
                showMessage(`請選擇 ${requiredSeats} 個座位`, 'warning');
				return false;
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