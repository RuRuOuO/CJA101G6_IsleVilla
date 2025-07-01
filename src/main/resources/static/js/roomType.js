document.addEventListener('DOMContentLoaded', () => {
	
	////監聽搜尋功能是否有改變，判斷是否送出表單
    // 1. 先找到表單
    const filterForm = document.getElementById('filterForm');

    // 2. 確認表單真的存在
    if (filterForm) {
        // 3. 在表單底下，再去找下拉選單
        const roomTypeSelect = document.getElementById('roomTypeId');
        const statusSelect = document.getElementById('roomTypeSaleStatus');

        // 4. 為這兩個下拉選單加上事件監聽
        [roomTypeSelect, statusSelect].forEach(selectElement => {
            // 確認下拉選單也真的存在
            if (selectElement) {
                selectElement.addEventListener('change', () => {
                    console.log('偵測到變更，送出表單');
                    filterForm.submit(); // 使用正確的表單變數名稱提交
                });
            }
        });
    }
	
	//處理修改表單，自動帶入該筆房型的原始資料
	const updateModal = document.getElementById('updateRoomTypeModel');
	   updateModal.addEventListener('show.bs.modal', function (event) {
	       const button = event.relatedTarget; // 觸發 modal 的按鈕
	       const modal = updateModal;         // 你的 modal DOM

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
	   });
	   
//	  // 綁定「新增商品」與「修改商品」兩個 Modal 底下的 form submit
//      const updateForm = document.getElementById('updateRoomTypeForm');
//      if (updateForm) {
//          updateForm.onsubmit = submitUpdateForm;
//      }
//      const addForm = document.getElementById('addRoomTypeForm');
//      if (addForm) {
//          addForm.onsubmit = submitAddProductForm;
//      }
	   
//	   // 送出表單時組合正確順序與刪除資訊
//       async function submitUpdateForm(e) {
//           e.preventDefault();
//           const form = e.target;
//           // ...原有欄位驗證...
//           const formData = new FormData(form);
//           // 送出排序後的舊圖ID陣列
//           const oldPhotoOrder = productPhotoList.filter(p => !p.isNew && p.id).map(p => p.id);
//           formData.append('oldPhotoOrder', JSON.stringify(oldPhotoOrder));
//           // 送出要刪除的舊圖ID陣列
//           formData.append('deletedPhotoIds', JSON.stringify(deletedPhotoIds));
//           // 送出新圖檔案，依排序append
//           productPhotoList.filter(p => p.isNew).forEach(fileObj => {
//               formData.append('newPhotos', fileObj.file);
//           });
//           // ...其他欄位append...
//           // fetch送出
//           const resp = await fetch('/backend/product/edit', {
//               method: 'POST',
//               body: formData
//           });
//           if (resp.ok) {
//               alert('商品修改成功！');
//               window.location.href = '/backend/product/list';
//           } else {
//               alert('儲存失敗');
//           }
//       }
	   
//	   // 如果有新增錯誤訊息，自動顯示新增模態框
//	   if (document.getElementById('addErrorMessage')) {
//	       var addModal = new bootstrap.Modal(document.getElementById('addRoomTypeModel'));
//	       addModal.show();
//	   }
//
//	   // 如果有修改錯誤訊息，自動顯示修改模態框
//	   if (document.getElementById('updateErrorMessage')) {
//	       var updateModal = new bootstrap.Modal(document.getElementById('updateRoomTypeModel'));
//	       updateModal.show();
//	   }
});