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
		       		       modal.querySelector('#updateRoomTypeCapacity').value = capacity;
		       modal.querySelector('#updateRoomTypeContent').value = content;
		       modal.querySelector('#updateRoomTypePrice').value = price;
	
		       // 狀態選單要根據值選取
			   const saleStatusSelect = modal.querySelector('#updateRoomTypeSaleStatus');
			   if (saleStatusSelect) {
			       saleStatusSelect.value = status;  // 直接設定數值
			   }
			   
			   // 重設圖片狀態並載入
			   roomTypePhotoList = [];
			   deletedPhotoIds = [];
			   loadRoomTypePhotos(id); // 載入該房型的圖片
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
			
			// 清空新增圖片
			// 將圖片管理功能無縫整合到現有的Modal事件中
			// 確保每次開啟Modal時都有正確的初始狀態
			// 避免不同操作之間的資料干擾
			addRoomTypePhotoList = [];
			renderAddRoomTypePhotoPreview();
			
			// ===== 為新增表單設定即時驗證 =====
			setupRealTimeValidation(form);
		});
		
		// 清空新增表單時也清空圖片
		addModal.addEventListener('hidden.bs.modal', function () {
			addRoomTypePhotoList = [];
			renderAddRoomTypePhotoPreview();
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
				// 處理新增房型的圖片資料
				handleAddRoomTypePhotoSubmission(this);
				this.submit();
			}
		});
	}
	
	//攔截表單提交事件
	//在提交前呼叫 handleRoomTypePhotoSubmission() 處理圖片資料
	//這是解決圖片順序問題的關鍵步驟！
	if (updateForm) {
		updateForm.addEventListener('submit', function(event) {
			event.preventDefault();
			event.stopPropagation();
			
			if (validateForm(this)) {
				// 處理房型圖片排序資料
				handleRoomTypePhotoSubmission(this);
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

// ===== 房型圖片管理功能 =====
// 房型圖片管理相關變數
let roomTypePhotoList = [];      // 存放修改時的圖片資料
let deletedPhotoIds = [];        // 記錄被刪除的圖片ID
let sortablePhotoArea;           // 拖曳排序實例

// 新增房型圖片相關變數
let addRoomTypePhotoList = [];   // 存放新增時的圖片資料
let sortableAddPhotoArea;        // 新增頁面的拖曳排序實例

// 載入房型資料進行更新
//當使用者點擊修改按鈕時，自動載入該房型的基本資料和圖片
function loadRoomTypeForUpdate(button) {
    // 取得房型資料
    const roomTypeId = button.getAttribute('data-id');
    const roomTypeName = button.getAttribute('data-name');
    const roomTypeCode = button.getAttribute('data-code');
    const roomTypeQuantity = button.getAttribute('data-quantity');
    const roomTypeCapacity = button.getAttribute('data-capacity');
    const roomTypeContent = button.getAttribute('data-content');
    const roomTypePrice = button.getAttribute('data-price');
    const roomTypeSaleStatus = button.getAttribute('data-status');
    
    // 填入表單資料
    document.getElementById('updateRoomTypeForm').querySelector('input[name="roomTypeId"]').value = roomTypeId;
    document.getElementById('updateRoomTypeName').value = roomTypeName;
    document.getElementById('updateRoomTypeCode').value = roomTypeCode;
    document.getElementById('updateRoomTypeQuantity').value = roomTypeQuantity;
    document.getElementById('updateRoomTypeCapacity').value = roomTypeCapacity;
    document.getElementById('updateRoomTypeContent').value = roomTypeContent;
    document.getElementById('updateRoomTypePrice').value = roomTypePrice;
    document.getElementById('updateRoomTypeSaleStatus').value = roomTypeSaleStatus;
    
    // 載入房型圖片
    loadRoomTypePhotos(roomTypeId);
}

// 載入房型圖片
// 從後端API載入房型的現有圖片
// 將圖片資料格式化為前端可用的格式
// isNew: false 標記這些是資料庫中的既有圖片
function loadRoomTypePhotos(roomTypeId) {
    fetch(`/backend/roomTypePhoto/photos/${roomTypeId}`) // 呼叫API
        .then(res => res.json())
        .then(photos => {
            roomTypePhotoList = photos.map(photo => ({
                id: photo.roomTypePhotoId,
                url: `/backend/roomTypePhoto/image/${photo.roomTypePhotoId}`,
                displayOrder: photo.displayOrder,
                isNew: false
            }));
			// ...渲染圖片區域...
            deletedPhotoIds = [];
            renderRoomTypePhotoSortArea();
        })
        .catch(error => {
            console.error('載入圖片失敗:', error);
            roomTypePhotoList = [];
            renderRoomTypePhotoSortArea();
        });
}

// 新增新圖片（修改時）
// 檔案格式驗證（只允許圖片）
// 檔案大小限制（最大5MB）
// 使用 URL.createObjectURL() 建立本地預覽URL
// isNew: true 標記這些是新上傳的圖片
function addNewRoomTypePhotos(input) {
    if (input.files) {
        Array.from(input.files).forEach(file => {
            if (!file.type.startsWith('image/')) {
                alert('請選擇圖片檔案！');
                return;
            }
            if (file.size > 5 * 1024 * 1024) {
                alert('圖片大小不可超過 5MB！');
                return;
            }
            const url = URL.createObjectURL(file);
            roomTypePhotoList.push({ 
                id: null,           // 新圖片還沒有ID
                file,              // 保存檔案物件
                url,               // 用於預覽的URL
                displayOrder: null,
                isNew: true        // 標記為新圖片
            });
        });
        renderRoomTypePhotoSortArea();
        input.value = '';
    }
}

// 刪除圖片（修改時）
// 如果刪除的是既有圖片，將ID記錄到 deletedPhotoIds
// 如果刪除的是新上傳圖片，直接從陣列移除即可
// 重新渲染圖片區域
function removeRoomTypePhoto(idx) {
    const photo = roomTypePhotoList[idx];
    if (!photo.isNew && photo.id) {
        deletedPhotoIds.push(photo.id);  // 記錄要刪除的既有圖片ID
    }
    roomTypePhotoList.splice(idx, 1);    // 從陣列中移除
    renderRoomTypePhotoSortArea();
}

// 渲染圖片排序區域（修改時）
function renderRoomTypePhotoSortArea() {
    const area = document.getElementById('roomTypePhotoSortArea');
    if (!area) return;
    
    if (roomTypePhotoList.length === 0) {
        area.innerHTML = `<div class="d-flex align-items-center justify-content-center text-muted">暫無圖片</div>`;
        return;
    }

    area.innerHTML = '';
    roomTypePhotoList.forEach((photo, idx) => {
        const wrapper = document.createElement('div');
        wrapper.className = 'position-relative';
        wrapper.style.width = '100px';
        wrapper.style.height = '100px';
        wrapper.setAttribute('data-idx', idx);
        wrapper.innerHTML = `
            <img src="${photo.url}" class="img-thumbnail w-100 h-100" style="object-fit:cover;"/>
            <button type="button" class="btn btn-sm btn-danger position-absolute top-0 end-0" onclick="removeRoomTypePhoto(${idx})">&times;</button>
        `;
        area.appendChild(wrapper);
    });

    // 初始化拖曳排序
    if (sortablePhotoArea) sortablePhotoArea.destroy();
    sortablePhotoArea = new Sortable(area, {
        animation: 150,
        onEnd: function() {
            const newList = [];
            area.querySelectorAll('[data-idx]').forEach(el => {
                newList.push(roomTypePhotoList[parseInt(el.getAttribute('data-idx'))]);
            });
            roomTypePhotoList = newList;
            renderRoomTypePhotoSortArea();
        }
    });
}

// 新增房型圖片相關功能
function addNewRoomTypePhotos_add(input) {
    if (input.files) {
        Array.from(input.files).forEach(file => {
            if (!file.type.startsWith('image/')) {
                alert('請選擇圖片檔案！');
                return;
            }
            if (file.size > 5 * 1024 * 1024) {
                alert('圖片大小不可超過 5MB！');
                return;
            }
            const url = URL.createObjectURL(file);
            addRoomTypePhotoList.push({ file, url });
        });
        renderAddRoomTypePhotoPreview();
        input.value = '';
    }
}

function removeAddRoomTypePhoto(idx) {
    addRoomTypePhotoList.splice(idx, 1);
    renderAddRoomTypePhotoPreview();
}
//圖片區域渲染
// 動態生成圖片預覽區域的HTML
// 每張圖片都有刪除按鈕
// 使用 object-fit:cover 確保圖片比例正確
function renderAddRoomTypePhotoPreview() {
    const area = document.getElementById('addRoomTypePhotoPreview');
    if (!area) return;
    
    if (addRoomTypePhotoList.length === 0) {
        area.innerHTML = `
            <div class="d-flex align-items-center justify-content-center text-muted" style="width: 100%; height: 100px;" id="addNoPhotosPlaceholder">
                <div class="text-center">
                    <i class="bi bi-images fs-1"></i>
                    <div>暫無圖片</div>
                </div>
            </div>
        `;
        return;
    }

    area.innerHTML = '';
    addRoomTypePhotoList.forEach((photo, idx) => {
        const wrapper = document.createElement('div');
        wrapper.className = 'position-relative';
        wrapper.style.width = '100px';
        wrapper.style.height = '100px';
        wrapper.setAttribute('data-idx', idx);
        wrapper.innerHTML = `
            <img src="${photo.url}" class="img-thumbnail w-100 h-100" style="object-fit:cover;" onerror="this.src='https://via.placeholder.com/100x100?text=No+Image';"/>
            <button type="button" class="btn btn-sm btn-danger position-absolute top-0 end-0" style="z-index:2;" onclick="removeAddRoomTypePhoto(${idx})">&times;</button>
        `;
        area.appendChild(wrapper);
    });

    // 初始化拖曳排序
    // 使用 SortableJS 實現拖曳排序
    // 排序完成後重新整理陣列順序
    if (sortableAddPhotoArea) sortableAddPhotoArea.destroy();
    sortableAddPhotoArea = new Sortable(area, {
        animation: 150,
        onEnd: function() {
            const newList = [];
            area.querySelectorAll('[data-idx]').forEach(el => {
                newList.push(addRoomTypePhotoList[parseInt(el.getAttribute('data-idx'))]);
            });
            addRoomTypePhotoList = newList;
            renderAddRoomTypePhotoPreview();
        }
    });
}

// 處理房型圖片提交資料
//將前端的圖片管理狀態轉換為後端可處理的資料格式
//displayOrder: index + 1：依照拖曳後的陣列順序設定顯示順序
//動態建立隱藏的表單欄位，讓後端接收完整的圖片操作資料
function handleRoomTypePhotoSubmission(form) {
    // 移除舊的隱藏欄位
    const oldFields = form.querySelectorAll('input[name^="roomTypePhoto"], input[name="photoSortOrder"], input[name="deletedPhotoIds"]');
    oldFields.forEach(field => field.remove());
    
    // 建立一個 DataTransfer 物件來收集所有新圖片
    const dt = new DataTransfer();
    roomTypePhotoList.forEach((photo, index) => {
        if (photo.isNew && photo.file) {
            dt.items.add(photo.file);
        }
    });
    
    // 如果有新圖片，建立檔案輸入欄位
    if (dt.files.length > 0) {
        const fileInput = document.createElement('input');
        fileInput.type = 'file';
        fileInput.name = 'newPhotos';
        fileInput.style.display = 'none';
        fileInput.multiple = true;
        fileInput.files = dt.files;
        form.appendChild(fileInput);
    }
    
    // 加入排序資料
    const sortOrderInput = document.createElement('input');
    sortOrderInput.type = 'hidden';
    sortOrderInput.name = 'photoSortOrder';
    sortOrderInput.value = JSON.stringify(roomTypePhotoList.map((photo, index) => ({
        id: photo.id,
        displayOrder: index + 1,
        isNew: photo.isNew
    })));
    form.appendChild(sortOrderInput);
    
    // 加入刪除的圖片ID
    if (deletedPhotoIds.length > 0) {
        const deletedInput = document.createElement('input');
        deletedInput.type = 'hidden';
        deletedInput.name = 'deletedPhotoIds';
        deletedInput.value = JSON.stringify(deletedPhotoIds);
        form.appendChild(deletedInput);
    }
}

// 處理新增房型圖片提交資料
//只處理新圖片，不需要排序和刪除邏輯
function handleAddRoomTypePhotoSubmission(form) {
    // 移除舊的隱藏欄位
    const oldFields = form.querySelectorAll('input[name="newPhotos"]');
    oldFields.forEach(field => field.remove());
    
    // 建立一個 DataTransfer 物件來收集所有圖片
    const dt = new DataTransfer();
    addRoomTypePhotoList.forEach((photo, index) => {
        if (photo.file) {
            dt.items.add(photo.file);
        }
    });
    
    // 如果有圖片，建立檔案輸入欄位
    if (dt.files.length > 0) {
        const fileInput = document.createElement('input');
        fileInput.type = 'file';
        fileInput.name = 'newPhotos';
        fileInput.style.display = 'none';
        fileInput.multiple = true;
        fileInput.files = dt.files;
        form.appendChild(fileInput);
    }
}

