// 前端房型展示頁面 JavaScript
// 專門用於客戶端房型展示功能

// 頁面載入完成後初始化
document.addEventListener('DOMContentLoaded', function() {
    console.log('前端房型展示頁面已載入');
    
    // 初始化所有前端功能
    initializeRoomTypeCards();
    initializeImageErrorHandling();
    initializeAnimations();
    addKeyboardSupport();
    
    // 自動清理殘留的 backdrop
    document.querySelectorAll('.modal-backdrop').forEach(e => e.remove());
});

// 初始化房型卡片點擊事件
function initializeRoomTypeCards() {
    const roomTypeCards = document.querySelectorAll('.roomtype-card');
    
    roomTypeCards.forEach(card => {
        const btn = card.querySelector('.roomtype-btn');
        if (btn) {
            btn.addEventListener('click', function(e) {
                e.stopPropagation();
                const roomTypeId = this.getAttribute('data-roomtype-id');
                const roomTypeName = this.getAttribute('data-roomtype-name');
                const roomTypeCapacity = this.getAttribute('data-roomtype-capacity');
                const roomTypeContent = this.getAttribute('data-roomtype-content');
                const roomTypePrice = this.getAttribute('data-roomtype-price');
                
                showRoomTypeModal(roomTypeId, roomTypeName, roomTypeCapacity, roomTypeContent, roomTypePrice);
            });
        }
    });
    
    console.log(`已初始化 ${roomTypeCards.length} 個房型卡片`);
}

// 顯示房型詳細資訊的 Modal
function showRoomTypeModal(roomTypeId, roomTypeName, roomTypeCapacity, roomTypeContent, roomTypePrice) {
    console.log('顯示房型Modal:', roomTypeName);
    
    // 設定 modal 內容
    const modalTitle = document.getElementById('roomTypeModalTitle');
    const modalCapacity = document.getElementById('modalRoomTypeCapacity');
    const modalPrice = document.getElementById('modalRoomTypePrice');
    const modalContent = document.getElementById('modalRoomTypeContent');
    
    if (modalTitle) modalTitle.textContent = roomTypeName || '房型名稱';
    if (modalCapacity) modalCapacity.innerHTML = `<i class="bi bi-people"></i> 容納人數：${roomTypeCapacity || 0} 人`;
    if (modalPrice) modalPrice.textContent = formatPrice(roomTypePrice || 0);
    if (modalContent) modalContent.textContent = roomTypeContent || '暫無說明';
    
    // 載入房型圖片
    loadRoomTypeImages(roomTypeId);
    
    // 顯示 modal
    const modalElement = document.getElementById('roomTypeModal');
    if (modalElement) {
        const modal = new bootstrap.Modal(modalElement);
        modal.show();
    } else {
        console.error('找不到房型Modal元素');
    }
}

// 載入房型圖片
function loadRoomTypeImages(roomTypeId) {
    console.log('載入房型圖片:', roomTypeId);
    
    // 顯示載入狀態
    showCarouselLoading();
    
    // 發送請求獲取房型圖片
    fetch(`/roomtype/${roomTypeId}/images`)
        .then(response => {
            if (!response.ok) {
                throw new Error('載入圖片失敗');
            }
            return response.json();
        })
        .then(images => {
            console.log('獲取到圖片:', images);
            if (images && images.length > 0) {
                createImageCarousel(images);
            } else {
                showCarouselNoImages();
            }
        })
        .catch(error => {
            console.error('載入圖片錯誤:', error);
            showCarouselError();
        });
}

// 創建圖片輪播
function createImageCarousel(images) {
    const carouselInner = document.getElementById('modalCarouselInner');
    const carouselIndicators = document.getElementById('modalCarouselIndicators');
    
    if (!carouselInner || !carouselIndicators) {
        console.error('找不到輪播容器');
        return;
    }
    
    // 清空現有內容
    carouselInner.innerHTML = '';
    carouselIndicators.innerHTML = '';
    
    // 創建輪播圖片
    images.forEach((image, index) => {
        // 創建圖片項目
        const carouselItem = document.createElement('div');
        carouselItem.className = `carousel-item ${index === 0 ? 'active' : ''}`;
        
        const img = document.createElement('img');
        img.src = `/roomtype/image/${image.photoId}`;
        img.alt = `房型圖片 ${index + 1}`;
        img.className = 'img-fluid';
        
        // 圖片載入錯誤處理
        img.onerror = function() {
            this.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjQwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSIgZmlsbD0iI2Y4ZjlmYSIvPgo8dGV4dCB4PSI1MCUiIHk9IjUwJSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjE4IiBmaWxsPSIjNmM3NTdkIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+5ZyW54mH54Sh5rOV6Lyc5YWlPC90ZXh0Pgo8L3N2Zz4=';
        };
        
        carouselItem.appendChild(img);
        carouselInner.appendChild(carouselItem);
        
        // 創建指示器
        const indicator = document.createElement('button');
        indicator.type = 'button';
        indicator.setAttribute('data-bs-target', '#roomTypeCarousel');
        indicator.setAttribute('data-bs-slide-to', index.toString());
        indicator.className = index === 0 ? 'active' : '';
        indicator.setAttribute('aria-label', `圖片 ${index + 1}`);
        
        if (index === 0) {
            indicator.setAttribute('aria-current', 'true');
        }
        
        carouselIndicators.appendChild(indicator);
    });
    
    console.log(`已載入 ${images.length} 張圖片`);
}

// 顯示輪播載入狀態
function showCarouselLoading() {
    const carouselInner = document.getElementById('modalCarouselInner');
    const carouselIndicators = document.getElementById('modalCarouselIndicators');
    
    if (carouselInner) {
        carouselInner.innerHTML = `
            <div class="carousel-item active">
                <div class="carousel-placeholder">
                    <i class="bi bi-hourglass-split"></i>
                    <span>載入中...</span>
                </div>
            </div>
        `;
    }
    
    if (carouselIndicators) {
        carouselIndicators.innerHTML = '';
    }
}

// 顯示無圖片狀態
function showCarouselNoImages() {
    const carouselInner = document.getElementById('modalCarouselInner');
    const carouselIndicators = document.getElementById('modalCarouselIndicators');
    
    if (carouselInner) {
        carouselInner.innerHTML = `
            <div class="carousel-item active">
                <div class="carousel-placeholder">
                    <i class="bi bi-image"></i>
                    <span>暫無圖片</span>
                </div>
            </div>
        `;
    }
    
    if (carouselIndicators) {
        carouselIndicators.innerHTML = '';
    }
}

// 顯示載入錯誤狀態
function showCarouselError() {
    const carouselInner = document.getElementById('modalCarouselInner');
    const carouselIndicators = document.getElementById('modalCarouselIndicators');
    
    if (carouselInner) {
        carouselInner.innerHTML = `
            <div class="carousel-item active">
                <div class="carousel-placeholder">
                    <i class="bi bi-exclamation-triangle"></i>
                    <span>載入失敗</span>
                </div>
            </div>
        `;
    }
    
    if (carouselIndicators) {
        carouselIndicators.innerHTML = '';
    }
}

// 初始化圖片載入錯誤處理
function initializeImageErrorHandling() {
    const images = document.querySelectorAll('.roomtype-image');
    
    images.forEach(img => {
        // 圖片載入失敗處理
        img.addEventListener('error', function() {
            console.log('圖片載入失敗:', this.src);
            
            // 建立無圖片占位符
            const container = this.parentElement;
            const placeholder = document.createElement('div');
            placeholder.className = 'roomtype-no-image';
            placeholder.innerHTML = `
                <i class="bi bi-image"></i>
                <span>暫無圖片</span>
            `;
            
            // 替換圖片
            container.removeChild(this);
            container.appendChild(placeholder);
        });
        
        // 圖片載入成功處理
        img.addEventListener('load', function() {
            console.log('圖片載入成功:', this.src);
            this.style.opacity = '1';
        });
        
        // 設定初始透明度
        img.style.opacity = '0';
        img.style.transition = 'opacity 0.3s ease';
    });
    
    console.log(`已初始化 ${images.length} 張圖片載入處理`);
}

// 初始化動畫效果
function initializeAnimations() {
    // 檢查瀏覽器是否支援 Intersection Observer
    if (!window.IntersectionObserver) {
        console.log('瀏覽器不支援 Intersection Observer，跳過動畫效果');
        return;
    }
    
    // 監聽滾動事件，實現卡片進入動畫
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
                console.log('卡片進入視野:', entry.target);
            }
        });
    }, {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    });
    
    // 為每個卡片添加初始動畫樣式
    const cards = document.querySelectorAll('.roomtype-card');
    cards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(30px)';
        card.style.transition = `opacity 0.6s ease ${index * 0.1}s, transform 0.6s ease ${index * 0.1}s`;
        
        observer.observe(card);
    });
    
    console.log(`已初始化 ${cards.length} 個卡片動畫效果`);
}

// 為房型卡片添加鍵盤支援（無障礙設計）
function addKeyboardSupport() {
    const buttons = document.querySelectorAll('.roomtype-btn');
    
    buttons.forEach(btn => {
        btn.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                this.click();
            }
        });
        
        // 為按鈕添加 tabindex 確保可以被鍵盤焦點
        if (!btn.hasAttribute('tabindex')) {
            btn.setAttribute('tabindex', '0');
        }
    });
    
    console.log(`已初始化 ${buttons.length} 個按鈕的鍵盤支援`);
}

// 格式化價格顯示
function formatPrice(price) {
    const numPrice = parseInt(price) || 0;
    return `NT$ ${numPrice.toLocaleString()}`;
}

// 處理 Modal 關閉事件
document.addEventListener('hidden.bs.modal', function (event) {
    if (event.target.id === 'roomTypeModal') {
        console.log('房型 Modal 已關閉');
    }
});

// 工具函數：顯示載入狀態
function showLoading(container) {
    if (container) {
        container.innerHTML = `
            <div class="loading">
                <i class="bi bi-hourglass-split"></i>
                <span>載入中...</span>
            </div>
        `;
    }
}

// 工具函數：顯示空狀態
function showEmptyState(container, message = '暫無房型資料') {
    if (container) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="bi bi-house"></i>
                <h5>暫無資料</h5>
                <p>${message}</p>
            </div>
        `;
    }
}

// 全域錯誤處理
window.addEventListener('error', function(e) {
    console.error('頁面錯誤:', e.error);
});

console.log('前端房型展示 JavaScript 模組已載入'); 