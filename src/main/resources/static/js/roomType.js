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
		});
	}

});