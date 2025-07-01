// DOM載入完成後執行
document.addEventListener('DOMContentLoaded', function() {
	const form = document.getElementById('roomTypePhotoForm');
	const roomTypePhotoIdInput = document.getElementById('roomTypePhotoId');
	const roomTypeSelect = document.getElementById('roomTypeId');


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
					roomTypePhotoId: parseInt(formData.get('roomTypePhotoId')),
					roomTypeId: parseInt(formData.get('roomTypeId')),
				};
	
				// 自定義驗證
				if (validateRoomData(roomData)) {
					form.submit(); //驗證後沒問題手動送出
				}
			}
	
			// 顯示Bootstrap驗證樣式 會讓已經存在的 .invalid-feedback 區塊顯示出來。加上 .is-invalid 類別(CSS)
			form.classList.add('was-validated');
		});
		
		
		// 即時驗證 - 房型照片ID
		if(roomTypePhotoIdInput) {
			roomTypePhotoIdInput.addEventListener('input', function() {
				validateNumberInput(this);
			});
		}

		// 即時驗證 - 房型ID
		if(roomTypeSelect) {
			roomTypeSelect.addEventListener('change', function() {
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
			const { roomTypePhotoId, roomTypeId } = roomData; //, roomStatus

			// 驗證條件不同的情況下：
			if (validateMode === 'strict') {
			    if (!roomTypePhotoId || isNaN(roomTypePhotoId) || roomTypePhotoId <= 0) {
			        showErrorAlert('房型圖片ID必須是大於0的整數！');
			        return false;
			    }
			    if (isNaN(roomTypeId) || roomTypeId < 0) {
			        showErrorAlert('請選擇房型！');
			        return false;
			    }
			    return true;
			} else {
			    // loose 模式：只需填一個條件
			    const hasRoomTypePhotoId = roomTypePhotoId && !isNaN(roomTypePhotoId) && roomTypePhotoId > 0;
			    const hasRoomTypeId = !isNaN(roomTypeId) && roomTypeId > 0;
				
			    if (!hasRoomTypePhotoId && !hasRoomTypeId) {
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

// 照片預覽功能
function previewImage(input) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const preview = document.getElementById('newImagePreview');
            const previewImg = document.getElementById('previewImg');
            if (preview && previewImg) {
                previewImg.src = e.target.result;
                preview.style.display = 'block';
            }
        };
        reader.readAsDataURL(input.files[0]);
    }
}

// 原有的照片預覽功能（用於模態框）
function previewImageModal(photoId) {
    var img = document.querySelector('img[data-photo-id="' + photoId + '"]');
    if (img) {
        var modal = new bootstrap.Modal(document.getElementById('imageModal'));
        document.getElementById('modalImage').src = img.src;
        modal.show();
    }
}

function validatePhoto(input) {
            const file = input.files[0];
            const errorDiv = input.parentElement.querySelector('.invalid-feedback'); // 找到 invalid-feedback div
            
            if (file) {
                // 檢查檔案類型
                const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
                if (!allowedTypes.includes(file.type)) {
                    input.setCustomValidity('只支援 JPG、PNG、GIF 格式的圖片');
                    input.classList.add('is-invalid');
                    input.classList.remove('is-valid');
                    if (errorDiv) errorDiv.textContent = '只支援 JPG、PNG、GIF 格式的圖片';
                    return false;
                }
                
                // 檢查檔案大小 (5MB)
                if (file.size > 5 * 1024 * 1024) {
                    const fileSize = (file.size / (1024 * 1024)).toFixed(1);
                    input.setCustomValidity(`圖片檔案大小不能超過 5MB，目前檔案大小：${fileSize}MB`);
                    input.classList.add('is-invalid');
                    input.classList.remove('is-valid');
                    if (errorDiv) errorDiv.textContent = `圖片檔案大小不能超過 5MB，目前檔案大小：${fileSize}MB`;
                    return false;
                }
                
                // 檔案驗證通過
                input.setCustomValidity('');
                input.classList.remove('is-invalid');
                input.classList.add('is-valid');
                return true;
            } else {
                // 沒有選擇檔案
                input.setCustomValidity('請選擇要上傳的照片');
                input.classList.remove('is-valid', 'is-invalid');
                errorDiv.textContent = '請選擇要上傳的照片';
            }
        }

// 重置表單函數（全域函數，供HTML onclick使用）
function resetForm() {
	const form = document.getElementById('roomTypePhotoForm');
	console.log("重置表單");

	// 直接清空欄位值
	const roomTypePhotoIdInput = document.getElementById('roomTypePhotoId');
	const roomTypeInput = document.getElementById('roomTypeId');
	
	if (roomTypePhotoIdInput) roomTypePhotoIdInput.value = '';
	if (roomTypeInput) roomTypeInput.value = '';

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
	if (roomTypePhotoIdInput) roomTypePhotoIdInput.focus();
}
