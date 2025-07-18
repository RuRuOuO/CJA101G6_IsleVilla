// DOM載入完成後執行
document.addEventListener('DOMContentLoaded', function() {
	const form = document.getElementById('roomForm');
	const roomIdInput = document.getElementById('roomId');
	const roomTypeIdSelect = document.getElementById('roomTypeId');
	const roomStatusSelect = document.getElementById('roomStatus');
	const successMessage = document.getElementById('successMessage');

	// 表單提交處理
	if(form){
		form.addEventListener('submit', function(e) {
			e.preventDefault(); // 阻止瀏覽器預設送出行為
			e.stopPropagation();
	
			// 執行Bootstrap驗證
			if (form.checkValidity()) {
				// 獲取表單資料
				const formData = new FormData(form);
				const roomData = {
					roomId: parseInt(formData.get('roomId')),
					roomTypeId: parseInt(formData.get('roomTypeId')),
					roomStatus: parseInt(formData.get('roomStatus'))
				};
	
				// 自定義驗證
				if (validateRoomData(roomData)) {
					form.submit();
				}
			}
	
			// 顯示Bootstrap驗證樣式 會讓已經存在的 .invalid-feedback 區塊顯示出來。加上 .is-invalid 類別(CSS)
			form.classList.add('was-validated');
		});
		
		
		// 即時驗證 - 房間ID
		if(roomIdInput) {
			roomIdInput.addEventListener('input', function() {
				validateNumberInput(this);
			});
		}

		// 即時驗證 - 房型ID
		if(roomTypeIdSelect) {
			roomTypeIdSelect.addEventListener('change', function() {
				validateSelectInput(this);
			});
		}

		// 即時驗證 - 房間狀態
		if(roomStatusSelect) {
			roomStatusSelect.addEventListener('change', function() {
				validateSelectInput(this);
			});
		}
	}	
		// 數字輸入驗證
		function validateNumberInput(input) {
			const value = parseInt(input.value);
			const isValid = !isNaN(value) && value > 0;
	
			if (input.value === '') {
				// 空值時移除所有驗證類別
				input.classList.remove('is-valid', 'is-invalid');
			} else if (isValid) {
				input.classList.remove('is-invalid');
				input.classList.add('is-valid');
			} else {
				input.classList.remove('is-valid');
				input.classList.add('is-invalid');
			}
		}
	
		// 下拉選單驗證
		function validateSelectInput(select) {
			if (select.value !== '') {
				select.classList.remove('is-invalid');
				select.classList.add('is-valid');
			} else {
				select.classList.remove('is-valid');
				select.classList.add('is-invalid');
			}
		}
	
		// 自定義資料驗證 加判斷驗證模式條件
		function validateRoomData(roomData) {
			const validateMode = form.getAttribute('data-validate-mode'); // 'loose' 或 'strict'
			const { roomId, roomTypeId, roomStatus } = roomData;

			// 驗證條件不同的情況下：
			if (validateMode === 'strict') {
			    if (!roomId || isNaN(roomId) || roomId <= 0) {
			        showErrorAlert('房間ID必須是大於0的整數！');
			        return false;
			    }
			    if (isNaN(roomTypeId) || roomTypeId < 0) {
			        showErrorAlert('請選擇房型！');
			        return false;
			    }
			    if (isNaN(roomStatus) || roomStatus < 0) {
			        showErrorAlert('請選擇房間狀態！');
			        return false;
			    }
			    return true;
			} else {
			    // loose 模式：只需填一個條件
			    const hasRoomId = roomId && !isNaN(roomId) && roomId > 0;
			    const hasRoomTypeId = !isNaN(roomTypeId) && roomTypeId > 0;
			    const hasRoomStatus = !isNaN(roomStatus) && roomStatus >= 0;
			    
			    if (!hasRoomId && !hasRoomTypeId && !hasRoomStatus) {
			        showErrorAlert('請至少輸入一個查詢條件');
			        return false;
			    }
			    return true;
			}
		}

	// 顯示錯誤警告
	function showErrorAlert(message) {
		// 移除現有的錯誤警告
		const existingAlert = document.querySelector('.alert-danger');
		if (existingAlert) {
			existingAlert.remove();
		}

		// 建立新的錯誤警告
		const alertDiv = document.createElement('div');
		alertDiv.className = 'alert alert-danger d-flex align-items-center';
		alertDiv.innerHTML = `
            <i class="bi bi-exclamation-triangle me-2"></i>
            <div>${message}</div>
        `;

		// 插入警告訊息
		const cardBody = document.querySelector('.card-body');
		cardBody.insertBefore(alertDiv, form);

		// 5秒後移除警告
		setTimeout(() => {
			alertDiv.remove();
		}, 5000);
	}
});

// 重置表單函數（全域函數，供HTML onclick使用）
function resetForm() {
	const form = document.getElementById('roomForm');
	console.log("重置表單");

	// 直接清空欄位值
	document.getElementById('roomId').value = '';
	document.getElementById('roomTypeId').value = '';
	document.getElementById('roomStatus').value = '';

	// 移除Bootstrap驗證類別
	form.classList.remove('was-validated');

	// 移除自定義驗證類別
	const inputs = form.querySelectorAll('.form-control, .form-select');
	inputs.forEach(input => {
		input.classList.remove('is-valid', 'is-invalid');
	});

	// 隱藏成功訊息
	const successMessage = document.getElementById('successMessage');
	if (successMessage) {
		successMessage.remove();
	}

	// 移除錯誤訊息
	const errorMessage = document.getElementById('errorMessage');
	if (errorMessage) {
		errorMessage.remove();
	}

	// 移除任何錯誤警告
	const errorAlert = document.querySelector('.alert-danger');
	if (errorAlert) {
		errorAlert.remove();
	}

	// 聚焦到第一個輸入框
	document.getElementById('roomId').focus();
}
