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
});