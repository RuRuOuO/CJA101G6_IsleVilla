document.addEventListener('DOMContentLoaded', () => {
	
	////監聽搜尋功能是否有改變，判斷是否送出表單
    const filterForm = document.getElementById('filterForm');
    if (filterForm) {
        const roomTypeSelect = document.getElementById('roomTypeId');
        const statusSelect = document.getElementById('roomTypeSaleStatus');
		
        //為這兩個下拉選單加上事件監聽
        [roomTypeSelect, statusSelect].forEach(selectElement => {
            // 確認下拉選單也真的存在
            if (selectElement) {
                selectElement.addEventListener('change', () => {
                    console.log('偵測到變更，送出表單');
                    filterForm.submit(); 
                });
            }
        });
    }
	
	//自動清理殘留 backdrop
	document.querySelectorAll('.modal-backdrop').forEach(e => e.remove());
	
	// 檢查頁面載入時是否有錯誤訊息，如果有就自動顯示對應的模態框
    // 如果有新增錯誤訊息，自動顯示新增模態框
	const addErrorMessage = document.getElementById('addErrorMessage');
	if (addErrorMessage) {
		console.log('發現新增錯誤訊息，顯示新增模態框');
   		const addModal = new bootstrap.Modal(document.getElementById('addRoomTypeModal'));
   		addModal.show();
   	}
   	// 如果有修改錯誤訊息，自動顯示修改模態框
	const updateErrorMessage = document.getElementById('updateErrorMessage');
	if (updateErrorMessage) {
		console.log('發現修改錯誤訊息，顯示修改模態框');
   		const updateModal = new bootstrap.Modal(document.getElementById('updateRoomTypeModal'));
   		updateModal.show();
   	}
	
	//處理修改表單，自動帶入該筆房型的原始資料
	const updateModal = document.getElementById('updateRoomTypeModal');
	if (updateModal) {
	   updateModal.addEventListener('show.bs.modal', function (event) {
	       const button = event.relatedTarget; // 觸發 modal 的按鈕
	       const modal = updateModal;         // modal DOM
	
		   // 清除之前的錯誤訊息
   			const errorMessageDiv = modal.querySelector('#updateErrorMessage');
   			if (errorMessageDiv) {
   				errorMessageDiv.remove();
   			}
			// 清除之前表單驗證狀態
			const form = modal.querySelector('#updateRoomTypeForm');
			if (form) {
				form.classList.remove('was-validated');
				// 清除所有input的驗證狀態
				const inputs = form.querySelectorAll('.form-control, .form-select');
				inputs.forEach(input => {
					input.classList.remove('is-invalid', 'is-valid');
				});
			}
			
			if (button) {		
		       // 從按鈕取值
		       const id = button.getAttribute('data-id');
		       const name = button.getAttribute('data-name');
		       const code = button.getAttribute('data-code');
		       const quantity = button.getAttribute('data-quantity');
		       const capacity = button.getAttribute('data-capacity');
		       const content = button.getAttribute('data-content');
		       const price = button.getAttribute('data-price');
		       const status = button.getAttribute('data-status');
	
		       // 填入表單欄位
		       modal.querySelector('input[name="roomTypeId"]').value = id;
		       modal.querySelector('#updateRoomTypeName').value = name;
		       modal.querySelector('#updateRoomTypeCode').value = code;
		       modal.querySelector('#updateRoomTypeQuantity').value = quantity;
		       modal.querySelector('#updateRoomTypeCapacity').value = capacity;
		       modal.querySelector('#updateRoomTypeContent').value = content;
		       modal.querySelector('#updateRoomTypePrice').value = price;
	
		       // 狀態選單要根據值選取
			   const saleStatusSelect = modal.querySelector('#updateRoomTypeSaleStatus');
			   if (saleStatusSelect) {
			       saleStatusSelect.value = status;  // 直接設定數值
			   }
			}
			
			// ===== 為修改表單設定即時驗證 =====
			setupRealTimeValidation(form);
	   })
	};
	   
	// 處理新增表單
	const addModal = document.getElementById('addRoomTypeModal');
	if (addModal) {
		addModal.addEventListener('show.bs.modal', function (event) {
			const modal = addModal;

			// 清除之前的錯誤訊息
			const errorMessageDiv = modal.querySelector('#addErrorMessage');
			if (errorMessageDiv) {
				errorMessageDiv.remove();
			}

			// 清除表單驗證狀態
			const form = modal.querySelector('#addRoomTypeForm');
			if (form) {
				form.classList.remove('was-validated');
				// 清除所有input的驗證狀態
				const inputs = form.querySelectorAll('.form-control, .form-select');
				inputs.forEach(input => {
					input.classList.remove('is-invalid', 'is-valid');
				});
				// 重置表單內容
				form.reset();
			}
			
			// ===== 為新增表單設定即時驗證 =====
			setupRealTimeValidation(form);
		});
	}

	
	// ===== 即時驗證功能函數 =====
	function setupRealTimeValidation(form) {
		if (!form) return;
		
		const inputs = form.querySelectorAll('.form-control, .form-select');
		
		inputs.forEach(input => {
			// 為每個輸入欄位添加事件監聽器
			input.addEventListener('blur', () => validateField(input));
			input.addEventListener('input', () => {
				// 如果欄位已經有驗證狀態，則即時更新
				if (input.classList.contains('is-invalid') || input.classList.contains('is-valid')) {
					validateField(input);
				}
			});
		});
	}
	
	function validateField(field) {
		const fieldId = field.id;
		const value = field.value.trim();
		let isValid = true;
		let errorMessage = '';
		
		// 清除之前的驗證狀態
		field.classList.remove('is-invalid', 'is-valid');
		
		// 根據欄位ID進行特定驗證
		switch (fieldId) {
			case 'addRoomTypeName':
			case 'updateRoomTypeName':
				if (!value) {
					isValid = false;
					errorMessage = '請輸入房型名稱';
				} else if (value.length > 50) {
					isValid = false;
					errorMessage = '房型名稱不能超過50個字元';
				}
				break;
				
			case 'addRoomTypeCode':
			case 'updateRoomTypeCode':
				const codePattern = /^[A-Za-z0-9]{3}$/;
				if (!value) {
					isValid = false;
					errorMessage = '請輸入房型代碼';
				} else if (!codePattern.test(value)) {
					isValid = false;
					errorMessage = '請輸入英文字母或數字，且長度為3';
				}
				break;
				
			case 'addRoomTypeContent':
			case 'updateRoomTypeContent':
				if (!value) {
					isValid = false;
					errorMessage = '請輸入房型說明';
				} else if (value.length > 1000) {
					isValid = false;
					errorMessage = '房型說明不能超過1000個字元';
				}
				break;
				
			case 'addRoomTypeQuantity':
			case 'updateRoomTypeQuantity':
				const quantity = parseInt(value);
				if (!value) {
					isValid = false;
					errorMessage = '請輸入房型數量';
				} else if (isNaN(quantity) || quantity < 0) {
					isValid = false;
					errorMessage = '房型數量必須大於等於0';
				}
				break;
				
			case 'addRoomTypeCapacity':
			case 'updateRoomTypeCapacity':
				const capacity = parseInt(value);
				if (!value) {
					isValid = false;
					errorMessage = '請輸入房型容納人數';
				} else if (isNaN(capacity) || capacity < 1) {
					isValid = false;
					errorMessage = '房型容納人數必須大於等於1';
				}
				break;
				
			case 'addRoomTypePrice':
			case 'updateRoomTypePrice':
				const price = parseInt(value);
				if (!value) {
					isValid = false;
					errorMessage = '請輸入房型價格';
				} else if (isNaN(price) || price < 1) {
					isValid = false;
					errorMessage = '房型價格必須大於0';
				}
				break;
				
			case 'addRoomTypeSaleStatus':
			case 'updateRoomTypeSaleStatus':
				if (!value) {
					isValid = false;
					errorMessage = '請選擇上下架狀態';
				}
				break;
		}
		
		// 套用驗證結果
		if (isValid) {
			field.classList.add('is-valid');
		} else {
			field.classList.add('is-invalid');
			// 更新錯誤訊息
			const feedback = field.nextElementSibling;
			if (feedback && feedback.classList.contains('invalid-feedback')) {
				feedback.textContent = errorMessage;
			}
		}
	}
	
	// ===== 表單提交前的完整驗證 =====
	const addForm = document.getElementById('addRoomTypeForm');
	const updateForm = document.getElementById('updateRoomTypeForm');
	
	if (addForm) {
		addForm.addEventListener('submit', function(event) {
			event.preventDefault();
			event.stopPropagation();
			
			if (validateForm(this)) {
				this.submit();
			}
		});
	}
	
	if (updateForm) {
		updateForm.addEventListener('submit', function(event) {
			event.preventDefault();
			event.stopPropagation();
			
			if (validateForm(this)) {
				this.submit();
			}
		});
	}
	
	function validateForm(form) {
		const inputs = form.querySelectorAll('.form-control, .form-select');
		let isFormValid = true;
		
		inputs.forEach(input => {
			validateField(input);
			if (input.classList.contains('is-invalid')) {
				isFormValid = false;
			}
		});
		
		form.classList.add('was-validated');
		return isFormValid;
	}
	
	// ===== 數字輸入欄位的額外處理 =====
	const numberInputs = document.querySelectorAll('input[type="number"]');
	numberInputs.forEach(input => {
		input.addEventListener('keypress', function(event) {
			// 只允許數字和一些控制鍵
			const allowedKeys = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
								'Backspace', 'Delete', 'Tab', 'Enter', 'ArrowLeft', 'ArrowRight'];
			
			if (!allowedKeys.includes(event.key) && !event.ctrlKey) {
				event.preventDefault();
			}
		});
		
		// 防止負數輸入（針對有min屬性的欄位）
		input.addEventListener('input', function() {
			const min = parseInt(this.getAttribute('min'));
			if (!isNaN(min) && parseInt(this.value) < min) {
				this.value = min;
			}
		});
	});
	
	// ===== 字元計數器（可選功能）=====
	const textareaFields = document.querySelectorAll('textarea');
	textareaFields.forEach(textarea => {
		const maxLength = 1000; // 根據你的需求調整
		
		// 創建字元計數器元素
		const counter = document.createElement('small');
		counter.className = 'text-muted mt-1 d-block';
		counter.textContent = `0 / ${maxLength}`;
		
		// 插入到textarea後面
		textarea.parentNode.insertBefore(counter, textarea.nextSibling);
		
		// 監聽輸入事件
		textarea.addEventListener('input', function() {
			const currentLength = this.value.length;
			counter.textContent = `${currentLength} / ${maxLength}`;
			
			if (currentLength > maxLength) {
				counter.className = 'text-danger mt-1 d-block';
				this.classList.add('is-invalid');
			} else {
				counter.className = 'text-muted mt-1 d-block';
			}
		});
	});
});