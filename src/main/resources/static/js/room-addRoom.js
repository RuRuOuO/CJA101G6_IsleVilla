// DOM載入完成後執行
document.addEventListener('DOMContentLoaded', function() {
	const form = document.getElementById('roomForm');
	const roomIdInput = document.getElementById('roomId');
	const roomTypeIdInput = document.getElementById('roomTypeId');
	const roomStatusSelect = document.getElementById('roomStatus');
	const successMessage = document.getElementById('successMessage');

	// 表單提交處理
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

		// 顯示Bootstrap驗證樣式
		form.classList.add('was-validated');
	});

	// 即時驗證 - 房間ID
	roomIdInput.addEventListener('input', function() {
		validateNumberInput(this);
	});

	// 即時驗證 - 房型ID
	roomTypeIdInput.addEventListener('input', function() {
		validateNumberInput(this);
	});

	// 即時驗證 - 房間狀態
	roomStatusSelect.addEventListener('change', function() {
		validateSelectInput(this);
	});

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

	// 自定義資料驗證
	function validateRoomData(data) {
		// 檢查是否為正整數
		if (!Number.isInteger(data.roomId) || data.roomId <= 0) {
			console.log(!Number.isInteger(data.roomId));
			console.log(data.roomId);
			showErrorAlert('房間ID必須是大於0的整數！');
			return false;
		}

		if (!Number.isInteger(data.roomTypeId) || data.roomTypeId <= 0) {
			showErrorAlert('房型ID必須是大於0的整數！');
			return false;
		}

		if (![0, 1, 2, 3].includes(data.roomStatus)) {
			showErrorAlert('請選擇正確的房間狀態！');
			return false;
		}

		return true;
	}


	// 顯示載入狀態
	function showLoadingState(isLoading) {
		const submitBtn = form.querySelector('button[type="submit"]');
		const resetBtn = form.querySelector('button[type="button"]');

		if (isLoading) {
			submitBtn.disabled = true;
			resetBtn.disabled = true;
			submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>新增中...';
		} else {
			submitBtn.disabled = false;
			resetBtn.disabled = false;
			submitBtn.innerHTML = '<i class="bi bi-plus-circle me-1"></i>新增房間';
		}
	}

	// 顯示成功訊息
	function showSuccessMessage() {
		successMessage.classList.remove('d-none');
		successMessage.scrollIntoView({ behavior: 'smooth', block: 'nearest' });

		// 3秒後隱藏訊息
		setTimeout(() => {
			successMessage.classList.add('d-none');
		}, 5000);
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

		// 3秒後移除警告
		setTimeout(() => {
			alertDiv.remove();
		}, 5000);
	}
});

// 返回頁面函數（全域函數，供HTML onclick使用）
function goBack() {
    window.location.href = '/selectPage.html'
    }

// 重置表單函數（全域函數，供HTML onclick使用）
function resetForm() {
	const form = document.getElementById('roomForm');
	console.log("重製表單");

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
