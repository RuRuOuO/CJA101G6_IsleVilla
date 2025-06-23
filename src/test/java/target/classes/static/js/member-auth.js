// member-auth.js - 會員登入、註冊、忘記密碼頁面的JavaScript功能

document.addEventListener('DOMContentLoaded', function() {
    // 初始化所有功能
    initPasswordToggle();
    initFormValidation();
    initPhotoPreview();
    initPasswordStrengthIndicator();
    initFormSubmitHandler();
});

// 密碼顯示/隱藏功能
function initPasswordToggle() {
    // 處理所有密碼切換按鈕
    const toggleButtons = ['togglePassword', 'togglePassword1', 'togglePassword2'];
    
    toggleButtons.forEach(buttonId => {
        const toggleBtn = document.getElementById(buttonId);
        if (toggleBtn) {
            toggleBtn.addEventListener('click', function() {
                const passwordInput = this.parentElement.querySelector('input[type="password"], input[type="text"]');
                const icon = this.querySelector('i');
                
                if (passwordInput.type === 'password') {
                    passwordInput.type = 'text';
                    icon.classList.remove('fa-eye');
                    icon.classList.add('fa-eye-slash');
                } else {
                    passwordInput.type = 'password';
                    icon.classList.remove('fa-eye-slash');
                    icon.classList.add('fa-eye');
                }
            });
        }
    });
}

// 表單驗證功能
function initFormValidation() {
    // 登入表單驗證
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            if (!validateLoginForm()) {
                e.preventDefault();
            }
        });
    }

    // 註冊表單驗證
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            if (!validateRegisterForm()) {
                e.preventDefault();
            }
        });
        
        // 即時驗證 - 只在註冊頁面啟用
        addRealTimeValidation();
    }

    // 忘記密碼表單驗證
    const forgotPasswordForm = document.getElementById('forgotPasswordForm');
    if (forgotPasswordForm) {
        forgotPasswordForm.addEventListener('submit', function(e) {
            if (!validateForgotPasswordForm()) {
                e.preventDefault();
            }
        });
    }
}

// 登入表單驗證
function validateLoginForm() {
    let isValid = true;
    
    const email = document.getElementById('memberEmail');
    const password = document.getElementById('memberPassword');
    
    // 清除之前的錯誤狀態
    clearFieldError(email);
    clearFieldError(password);
    
    // 驗證信箱
    if (!email.value.trim()) {
        showFieldError(email, '請輸入電子信箱');
        isValid = false;
    } else if (!isValidEmail(email.value)) {
        showFieldError(email, '請輸入有效的電子信箱格式');
        isValid = false;
    }
    
    // 驗證密碼
    if (!password.value.trim()) {
        showFieldError(password, '請輸入密碼');
        isValid = false;
    }
    
    return isValid;
}

// 註冊表單驗證
function validateRegisterForm() {
    let isValid = true;
    
    const fields = [
        'memberEmail', 'memberName', 'memberPassword', 'confirmPassword',
        'memberBirthdate', 'memberGender', 'memberPhone', 'memberAddress'
    ];
    
    // 清除所有錯誤狀態
    fields.forEach(fieldId => {
        const field = document.getElementById(fieldId);
        if (field) clearFieldError(field);
    });
    
    // 驗證各個欄位
    isValid &= validateEmail('memberEmail');
    isValid &= validateName('memberName');
    isValid &= validatePassword('memberPassword');
    isValid &= validateConfirmPassword('confirmPassword');
    isValid &= validateBirthdate('memberBirthdate');
    isValid &= validateGender('memberGender');
    isValid &= validatePhone('memberPhone');
    isValid &= validateAddress('memberAddress');
    isValid &= validatePhoto('memberPhoto');
    isValid &= validateTerms('agreeTerms');
    
    return isValid;
}

// 忘記密碼表單驗證
function validateForgotPasswordForm() {
    const email = document.getElementById('memberEmail');
    clearFieldError(email);
    
    if (!email.value.trim()) {
        showFieldError(email, '請輸入電子信箱');
        return false;
    }
    
    if (!isValidEmail(email.value)) {
        showFieldError(email, '請輸入有效的電子信箱格式');
        return false;
    }
    
    return true;
}

// 各欄位驗證函數
function validateEmail(fieldId) {
    const field = document.getElementById(fieldId);
    const value = field.value.trim();
    
    if (!value) {
        showFieldError(field, '請輸入電子信箱');
        return false;
    }
    
    if (!isValidEmail(value)) {
        showFieldError(field, '請輸入有效的電子信箱格式');
        return false;
    }
    
    showFieldSuccess(field);
    return true;
}

function validateName(fieldId) {
    const field = document.getElementById(fieldId);
    const value = field.value.trim();
    
    if (!value) {
        showFieldError(field, '請輸入姓名');
        return false;
    }
    
    if (value.length < 2) {
        showFieldError(field, '姓名至少需要2個字元');
        return false;
    }
    
    if (value.length > 30) {
        showFieldError(field, '姓名不能超過30個字元');
        return false;
    }
    
    showFieldSuccess(field);
    return true;
}

function validatePassword(fieldId) {
    const field = document.getElementById(fieldId);
    const value = field.value;
    
    if (!value) {
        showFieldError(field, '請輸入密碼');
        return false;
    }
    
    if (value.length < 8) {
        showFieldError(field, '密碼至少需要8個字元');
        return false;
    }
    
    if (!/(?=.*[a-zA-Z])(?=.*\d)/.test(value)) {
        showFieldError(field, '密碼必須包含英文字母和數字');
        return false;
    }
    
    showFieldSuccess(field);
    return true;
}

function validateConfirmPassword(fieldId) {
    const field = document.getElementById(fieldId);
    const password = document.getElementById('memberPassword').value;
    const confirmPassword = field.value;
    
    if (!confirmPassword) {
        showFieldError(field, '請確認密碼');
        return false;
    }
    
    if (password !== confirmPassword) {
        showFieldError(field, '密碼確認不一致');
        return false;
    }
    
    showFieldSuccess(field);
    return true;
}

function validateBirthdate(fieldId) {
    const field = document.getElementById(fieldId);
    const value = field.value;
    
    if (!value) {
        showFieldError(field, '請選擇生日');
        return false;
    }
    
    const birthDate = new Date(value);
    const today = new Date();
    const age = today.getFullYear() - birthDate.getFullYear();
    
    if (age < 13) {
        showFieldError(field, '年齡必須滿13歲');
        return false;
    }
    
    if (age > 120) {
        showFieldError(field, '請輸入有效的生日');
        return false;
    }
    
    showFieldSuccess(field);
    return true;
}

function validateGender(fieldId) {
    const field = document.getElementById(fieldId);
    const value = field.value;
    
    if (!value) {
        showFieldError(field, '請選擇性別');
        return false;
    }
    
    showFieldSuccess(field);
    return true;
}

function validatePhone(fieldId) {
    const field = document.getElementById(fieldId);
    const value = field.value.trim();
    
    if (!value) {
        showFieldError(field, '請輸入電話號碼');
        return false;
    }
    
    // 台灣電話號碼格式驗證
    const phoneRegex = /^(\+886|0)?[2-9]\d{7,8}$|^(\+886|0)?9\d{8}$/;
    if (!phoneRegex.test(value.replace(/[-\s]/g, ''))) {
        showFieldError(field, '請輸入有效的台灣電話號碼');
        return false;
    }
    
    showFieldSuccess(field);
    return true;
}

function validateAddress(fieldId) {
    const field = document.getElementById(fieldId);
    const value = field.value.trim();
    
    if (!value) {
        showFieldError(field, '請輸入地址');
        return false;
    }
    
    if (value.length < 10) {
        showFieldError(field, '請輸入完整地址');
        return false;
    }
    
    if (value.length > 200) {
        showFieldError(field, '地址不能超過200个字元');
        return false;
    }
    
    showFieldSuccess(field);
    return true;
}

function validatePhoto(fieldId) {
    const field = document.getElementById(fieldId);
    const file = field.files[0];
    
    if (file) {
        // 檢查檔案類型
        const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];
        if (!allowedTypes.includes(file.type)) {
            showFieldError(field, '只支援 JPG、PNG、GIF 格式的圖片');
            return false;
        }
        
        // 檢查檔案大小 (5MB)
        if (file.size > 5 * 1024 * 1024) {
            showFieldError(field, '圖片檔案大小不能超過 5MB');
            return false;
        }
    }
    
    clearFieldError(field);
    return true;
}

function validateTerms(fieldId) {
    const field = document.getElementById(fieldId);
    
    if (!field.checked) {
        showFieldError(field, '請同意服務條款和隱私政策');
        return false;
    }
    
    clearFieldError(field);
    return true;
}

// 即時驗證 - 只在註冊頁面啟用
function addRealTimeValidation() {
    // 檢查是否在註冊頁面
    const registerForm = document.getElementById('registerForm');
    if (!registerForm) {
        return; // 如果不是註冊頁面，直接返回
    }
    
    // 信箱即時驗證
    const email = document.getElementById('memberEmail');
    if (email) {
        email.addEventListener('blur', () => validateEmail('memberEmail'));
    }
    
    // 密碼即時驗證 - 只在註冊頁面啟用
    const password = document.getElementById('memberPassword');
    if (password) {
        password.addEventListener('input', () => {
            validatePassword('memberPassword');
            updatePasswordStrength(password.value);
        });
    }
    
    // 確認密碼即時驗證
    const confirmPassword = document.getElementById('confirmPassword');
    if (confirmPassword) {
        confirmPassword.addEventListener('input', () => validateConfirmPassword('confirmPassword'));
    }
    
    // 電話即時驗證
    const phone = document.getElementById('memberPhone');
    if (phone) {
        phone.addEventListener('blur', () => validatePhone('memberPhone'));
    }
}

// 照片預覽功能
function initPhotoPreview() {
    const photoInput = document.getElementById('memberPhoto');
    if (photoInput) {
        photoInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    // 移除舊的預覽圖片
                    const oldPreview = document.querySelector('.photo-preview');
                    if (oldPreview) {
                        oldPreview.remove();
                    }
                    
                    // 創建新的預覽圖片
                    const img = document.createElement('img');
                    img.src = e.target.result;
                    img.className = 'photo-preview img-fluid';
                    img.alt = '照片預覽';
                    
                    // 插入預覽圖片
                    photoInput.parentNode.appendChild(img);
                };
                reader.readAsDataURL(file);
            }
        });
    }
}

// 密碼強度指示器 - 只在註冊頁面啟用
function initPasswordStrengthIndicator() {
    // 檢查是否在註冊頁面
    const registerForm = document.getElementById('registerForm');
    if (!registerForm) {
        return; // 如果不是註冊頁面，直接返回
    }
    
    const passwordInput = document.getElementById('memberPassword');
    if (passwordInput) {
        // 創建密碼強度指示器容器
        const strengthContainer = document.createElement('div');
        strengthContainer.className = 'mt-2';
        
        // 創建密碼強度指示器
        const strengthIndicator = document.createElement('div');
        strengthIndicator.className = 'password-strength';
        
        // 創建強度文字說明
        const strengthText = document.createElement('small');
        strengthText.className = 'text-muted mt-1 d-block';
        strengthText.textContent = '密碼強度：';
        
        // 組裝元素
        strengthContainer.appendChild(strengthIndicator);
        strengthContainer.appendChild(strengthText);
        
        // 插入到密碼輸入框的父容器後面
        passwordInput.parentNode.insertAdjacentElement('afterend', strengthContainer);
        
        passwordInput.addEventListener('input', function() {
            updatePasswordStrength(this.value);
        });
    }
}

function updatePasswordStrength(password) {
    const strengthIndicator = document.querySelector('.password-strength');
    if (!strengthIndicator) return;
    
    let strength = 0;
    
    // 檢查長度
    if (password.length >= 8) strength++;
    
    // 檢查是否包含小寫字母
    if (/[a-z]/.test(password)) strength++;
    
    // 檢查是否包含大寫字母
    if (/[A-Z]/.test(password)) strength++;
    
    // 檢查是否包含數字
    if (/\d/.test(password)) strength++;
    
    // 檢查是否包含特殊字符
    if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) strength++;
    
    // 更新強度指示器
    strengthIndicator.className = 'password-strength';
    if (strength <= 2) {
        strengthIndicator.classList.add('weak');
    } else if (strength <= 4) {
        strengthIndicator.classList.add('medium');
    } else {
        strengthIndicator.classList.add('strong');
    }
}

// 表單提交處理
function initFormSubmitHandler() {
    // 忘記密碼表單提交處理
    const forgotPasswordForm = document.getElementById('forgotPasswordForm');
    if (forgotPasswordForm) {
        forgotPasswordForm.addEventListener('submit', function() {
            const submitBtn = document.getElementById('submitBtn');
            const spinner = document.getElementById('loadingSpinner');
            
            if (submitBtn && spinner) {
                submitBtn.disabled = true;
                spinner.classList.remove('d-none');
                submitBtn.textContent = ' 發送中...';
                
                // 5秒後重新啟用按鈕
                setTimeout(() => {
                    submitBtn.disabled = false;
                    spinner.classList.add('d-none');
                    submitBtn.textContent = '發送重設連結';
                }, 5000);
            }
        });
    }
}

// 工具函數
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function showFieldError(field, message) {
    field.classList.add('is-invalid');
    field.classList.remove('is-valid');
    
    const errorElement = document.getElementById(field.id + 'Error');
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.style.display = 'block';
    }
}

function showFieldSuccess(field) {
    field.classList.add('is-valid');
    field.classList.remove('is-invalid');
    
    const errorElement = document.getElementById(field.id + 'Error');
    if (errorElement) {
        errorElement.style.display = 'none';
    }
}

function clearFieldError(field) {
    field.classList.remove('is-invalid', 'is-valid');
    
    const errorElement = document.getElementById(field.id + 'Error');
    if (errorElement) {
        errorElement.style.display = 'none';
    }
}

// 頁面載入動畫
function addPageAnimation() {
    const card = document.querySelector('.card');
    if (card) {
        card.classList.add('fade-in');
    }
}

// 當頁面載入完成時添加動畫
window.addEventListener('load', addPageAnimation);