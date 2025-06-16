// 判斷當前頁面類型
const isLogoutPage = document.body.classList.contains('logout-page');
const isSuccessPage = document.body.classList.contains('success-page');

// ====== 登出確認頁面功能 ======
if (isLogoutPage) {
    // 倒數計時自動返回 (可選功能)
    let countdown = 300; // 5分鐘後自動返回

    function startCountdown() {
        const interval = setInterval(() => {
            countdown--;
            if (countdown <= 0) {
                clearInterval(interval);
                window.location.href = '/';
            }
        }, 1000);
    }

    // 頁面載入時開始計時 (可選)
    // document.addEventListener('DOMContentLoaded', startCountdown);

    // 鍵盤快捷鍵
    document.addEventListener('keydown', function (event) {
        // ESC 鍵取消
        if (event.key === 'Escape') {
            history.back();
        }
        // Enter 鍵確認登出
        if (event.key === 'Enter') {
            const form = document.querySelector('form');
            if (form) {
                form.submit();
            }
        }
    });

    // 防止重複提交
    const logoutForm = document.querySelector('form');
    if (logoutForm) {
        logoutForm.addEventListener('submit', function (event) {
            const submitBtn = this.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>登出中...';
            }
        });
    }
}

// ====== 登出成功頁面功能 ======
if (isSuccessPage) {
    // 倒數計時功能
    let countdownTime = 10;
    const countdownElement = document.getElementById('countdown');

    function updateCountdown() {
        if (countdownElement) {
            countdownElement.textContent = countdownTime;
        }

        if (countdownTime <= 0) {
            window.location.href = '/';
            return;
        }

        countdownTime--;
        setTimeout(updateCountdown, 1000);
    }

    // 開始倒數計時
    updateCountdown();

    // 鍵盤快捷鍵
    document.addEventListener('keydown', function (event) {
        // Enter 鍵回首頁
        if (event.key === 'Enter') {
            window.location.href = '/';
        }
        // L 鍵重新登入
        if (event.key.toLowerCase() === 'l') {
            window.location.href = '/member-login';
        }
    });

    // 頁面載入動畫
    document.addEventListener('DOMContentLoaded', function () {
        // 延遲顯示按鈕，增加動畫效果
        setTimeout(() => {
            const actionButtons = document.querySelector('.action-buttons');
            if (actionButtons) {
                actionButtons.style.opacity = '1';
                actionButtons.style.transform = 'translateY(0)';
            }
        }, 500);
    });

    // 滑鼠移動視差效果
    document.addEventListener('mousemove', function (e) {
        const shapes = document.querySelectorAll('.floating-shape');
        const mouseX = e.clientX / window.innerWidth;
        const mouseY = e.clientY / window.innerHeight;

        shapes.forEach((shape, index) => {
            const speed = (index + 1) * 0.5;
            const x = (mouseX - 0.5) * speed * 20;
            const y = (mouseY - 0.5) * speed * 20;

            shape.style.transform = `translate(${x}px, ${y}px)`;
        });
    });
}

// ====== 共用功能 ======

// 頁面載入完成後的通用初始化
document.addEventListener('DOMContentLoaded', function () {
    // 為所有按鈕添加點擊音效 (可選)
    const buttons = document.querySelectorAll('button, .btn');
    buttons.forEach(button => {
        button.addEventListener('click', function () {
            // 可以在這裡添加音效或其他通用效果
            this.style.transform = 'scale(0.95)';
            setTimeout(() => {
                this.style.transform = '';
            }, 100);
        });
    });

    // 添加載入完成的動畫效果
    document.body.style.opacity = '0';
    document.body.style.transition = 'opacity 0.3s ease-in-out';

    setTimeout(() => {
        document.body.style.opacity = '1';
    }, 100);
});

// 錯誤處理
window.addEventListener('error', function (event) {
    console.error('頁面發生錯誤:', event.error);
});

// 防止右鍵選單 (可選安全功能)
// document.addEventListener('contextmenu', function(e) {
//     e.preventDefault();
// });

// 防止選取文字 (可選)
// document.addEventListener('selectstart', function(e) {
//     e.preventDefault();
// });