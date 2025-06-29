document.addEventListener('DOMContentLoaded', function () {
    // --- 日期驗證 ---
    const checkinInput = document.getElementById('check-in-date');
    const checkoutInput = document.getElementById('check-out-date');

    // 如果找不到日期欄位，直接 return，不執行後續日期邏輯
    if (!checkinInput || !checkoutInput) {
        return;
    }

    // 初始化：設定入住日期的最早可選日為今天
    function setInitialDate() {
        const today = new Date();
        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0');
        const day = String(today.getDate()).padStart(2, '0');
        const todayString = `${year}-${month}-${day}`;
        checkinInput.min = todayString;
    }

    // 當入住日期變更
    function handleCheckinChange() {
        if (!checkinInput.value) {
            checkoutInput.min = null;
            return;
        }
        const checkinDate = new Date(checkinInput.value);
        const nextDay = new Date(checkinDate.getTime()); 
        nextDay.setDate(nextDay.getDate() + 1);
        checkoutInput.min = nextDay.toISOString().split('T')[0];
        if (checkoutInput.value && new Date(checkoutInput.value) <= checkinDate) {
            checkoutInput.value = '';
        }
    }

    // 當退房日期變更
    function handleCheckoutChange() {
        if (!checkoutInput.value) {
            checkinInput.max = null;
            return;
        }
        const checkoutDate = new Date(checkoutInput.value);
        const prevDay = new Date(checkoutDate.getTime());
        prevDay.setDate(prevDay.getDate() - 1);
        if (new Date(checkinInput.min) > prevDay) {
            checkinInput.max = checkinInput.min;
        } else {
            checkinInput.max = prevDay.toISOString().split('T')[0];
        }
    }

    setInitialDate();
    checkinInput.addEventListener('change', handleCheckinChange);
    checkoutInput.addEventListener('change', handleCheckoutChange);

    // --- 動態新增/刪除房間 ---
    const roomsContainer = document.getElementById('rooms-container');
    const addRoomBtn = document.getElementById('add-room-btn');
    const roomCountInput = document.getElementById('room-count');
    let roomCounter = 1;

    // 更新房間數量
    function updateRoomCount() {
        const roomBlocks = document.querySelectorAll('.room-block');
        roomCountInput.value = roomBlocks.length;
    }

    // 重新編號所有房間
    function renumberRooms() {
        const roomBlocks = document.querySelectorAll('.room-block');
        roomBlocks.forEach((block, index) => {
            const roomNumber = index + 1;
            const title = block.querySelector('h5');
            title.textContent = `第 ${roomNumber} 間房`;
            
            // 更新 select 的 id 和 name
            const adultsSelect = block.querySelector('select[id^="adults-"]');
            const childrenSelect = block.querySelector('select[id^="children-"]');
            
            if (adultsSelect) {
                adultsSelect.id = `adults-${roomNumber}`;
                adultsSelect.name = `room${roomNumber}_adults`;
            }
            
            if (childrenSelect) {
                childrenSelect.id = `children-${roomNumber}`;
                childrenSelect.name = `room${roomNumber}_children`;
            }
        });
    }

    function addRoom() {
        const roomBlocks = document.querySelectorAll('.room-block');
        
        // 檢查是否已達到最大房間數（4間）
        if (roomBlocks.length >= 4) {
            alert('最多只能新增 4 間房');
            return;
        }
        
        roomCounter = roomBlocks.length + 1;
        const newRoomEl = document.createElement('div');
        newRoomEl.className = 'room-block';
        newRoomEl.innerHTML = `
            <h5 class="mb-3">第 ${roomCounter} 間房</h5>
            <button type="button" class="btn-close btn-remove-room" aria-label="Close"></button>
            <div class="row g-3">
                <div class="col-md-6">
                    <label for="adults-${roomCounter}" class="form-label">成人</label>
                    <select class="form-select" id="adults-${roomCounter}" name="room${roomCounter}_adults">
                        <option>1 位</option>
                        <option selected>2 位</option>
                        <option>3 位</option>
                        <option>4 位</option>
                        <option>5 位</option>
                        <option>6 位</option>
                    </select>
                </div>
                <div class="col-md-6">
                    <label for="children-${roomCounter}" class="form-label">孩童</label>
                    <select class="form-select" id="children-${roomCounter}" name="room${roomCounter}_children">
                        <option selected>0 位</option>
                        <option>1 位</option>
                        <option>2 位</option>
                    </select>
                </div>
            </div>
        `;
        roomsContainer.appendChild(newRoomEl);
        updateRoomCount();
    }

    function removeRoom(event) {
        // 使用事件委派，只在刪除按鈕上觸發
        if (event.target.classList.contains('btn-remove-room')) {
            event.target.closest('.room-block').remove();
            updateRoomCount();
            renumberRooms(); // 重新編號
        }
    }
    
    addRoomBtn.addEventListener('click', addRoom);
    roomsContainer.addEventListener('click', removeRoom);
    
    // 初始化房間數量
    updateRoomCount();

    // --- 表單提交處理 ---
    const bookingForm = document.querySelector('form[action*="booking/search"]');
    if (bookingForm) {
        bookingForm.addEventListener('submit', function(e) {
            // 收集每個房間的大人數
            const adultSelects = document.querySelectorAll('select[id^="adults-"]');
            const roomAdults = [];
            
            adultSelects.forEach(select => {
                const selectedOption = select.options[select.selectedIndex];
                const adultText = selectedOption.textContent;
                const adultCount = parseInt(adultText.match(/\d+/)[0]);
                roomAdults.push(adultCount);
            });
            
            // 創建隱藏的房間大人數欄位
            let roomAdultsInput = document.getElementById('room-adults');
            if (!roomAdultsInput) {
                roomAdultsInput = document.createElement('input');
                roomAdultsInput.type = 'hidden';
                roomAdultsInput.id = 'room-adults';
                roomAdultsInput.name = 'roomAdults';
                bookingForm.appendChild(roomAdultsInput);
            }
            roomAdultsInput.value = JSON.stringify(roomAdults);
            
            console.log('各房間大人數: ' + roomAdults);
            console.log('送出前 roomAdultsInput.value:', roomAdultsInput.value);
        });
    }
}); 