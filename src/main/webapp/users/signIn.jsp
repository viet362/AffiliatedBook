<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - Affiliated Book Demo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            min-height: 100vh;
        }
        .login-card {
            border: none;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            background: #ffffff;
            overflow: hidden;
        }
        .demo-box {
            background: #f0f7ff;
            border: 1px solid #cce3fd;
            border-radius: 12px;
            padding: 14px;
        }
        .btn-quick-admin {
            background: #fff0f0;
            color: #dc3545;
            border: 1px solid #f5c2c7;
            transition: all 0.2s ease;
        }
        .btn-quick-admin:hover {
            background: #dc3545;
            color: #ffffff;
            transform: translateY(-2px);
        }
        .btn-quick-user {
            background: #eef6ff;
            color: #0d6efd;
            border: 1px solid #b6d4fe;
            transition: all 0.2s ease;
        }
        .btn-quick-user:hover {
            background: #0d6efd;
            color: #ffffff;
            transform: translateY(-2px);
        }
    </style>
</head>

<body class="d-flex justify-content-center align-items-center vh-100 p-3">

<div class="card login-card p-4" style="width: 100%; max-width: 420px;">

    <!-- Logo / Tiêu đề -->
    <div class="text-center mb-3">
        <a href="${pageContext.request.contextPath}/products?page=home" class="text-decoration-none">
            <i class="fas fa-book-open text-primary fs-1 mb-2"></i>
            <h4 class="fw-bold text-dark m-0">Nhà Sách Trực Tuyến</h4>
        </a>
        <p class="text-muted small mt-1">Đăng nhập để trải nghiệm đầy đủ tính năng</p>
    </div>

    <!-- KHUNG ĐĂNG NHẬP NHANH CHO DEMO / NHÀ TUYỂN DỤNG -->
    <div class="demo-box mb-4">
        <div class="d-flex align-items-center justify-content-between mb-2">
            <span class="fw-bold text-primary" style="font-size: 0.88rem;">
                <i class="fas fa-sparkles me-1 text-warning"></i> Dành Cho Nhà Tuyển Dụng
            </span>
            <span class="badge bg-primary-subtle text-primary" style="font-size: 0.72rem;">1-Click Demo</span>
        </div>
        <p class="text-muted small mb-2" style="font-size: 0.8rem; line-height: 1.3;">
            Bấm nút bên dưới để tự động điền tài khoản và trải nghiệm phân quyền:
        </p>
        <div class="d-flex gap-2">
            <button type="button" class="btn btn-sm btn-quick-admin flex-fill fw-semibold py-2" onclick="quickLogin('admin', 'admin123', this)">
                <i class="fas fa-user-shield me-1"></i> 👑 Admin
            </button>
            <button type="button" class="btn btn-sm btn-quick-user flex-fill fw-semibold py-2" onclick="quickLogin('demo_user', '123456', this)">
                <i class="fas fa-user me-1"></i> 👤 User
            </button>
        </div>
    </div>

    <!-- Thông báo lỗi (nếu có) -->
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger py-2 px-3 mb-3 text-center" style="font-size: 0.88rem;">
            <i class="fas fa-exclamation-circle me-1"></i> ${errorMessage}
        </div>
    </c:if>

    <!-- FORM ĐĂNG NHẬP -->
    <form id="loginForm" method="POST" action="${pageContext.request.contextPath}/auth?action=signIn">
        <div class="mb-3">
            <label class="form-label small fw-semibold text-secondary">Tên đăng nhập</label>
            <div class="input-group">
                <span class="input-group-text bg-light text-muted"><i class="fas fa-user"></i></span>
                <input type="text" id="usernameInput" class="form-control" name="username" placeholder="Nhập username" required>
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label small fw-semibold text-secondary">Mật khẩu</label>
            <div class="input-group">
                <span class="input-group-text bg-light text-muted"><i class="fas fa-lock"></i></span>
                <input type="password" id="passwordInput" class="form-control" name="password" placeholder="Nhập mật khẩu" required>
            </div>
        </div>

        <button type="submit" id="submitBtn" class="btn btn-primary w-100 py-2 fw-semibold">
            <i class="fas fa-sign-in-alt me-1"></i> Đăng nhập
        </button>
    </form>

    <!-- Các lựa chọn khác -->
    <div class="d-flex align-items-center my-3">
        <hr class="flex-grow-1 my-0 text-muted">
        <span class="px-2 text-muted small" style="font-size: 0.8rem;">HOẶC</span>
        <hr class="flex-grow-1 my-0 text-muted">
    </div>

    <a href="${pageContext.request.contextPath}/products?page=home" class="btn btn-outline-secondary w-100 py-2" style="font-size: 0.9rem;">
        <i class="fas fa-eye me-1"></i> Xem tiếp với tư cách Khách
    </a>

    <div class="text-center mt-3" style="font-size: 0.88rem;">
        <span class="text-muted">Chưa có tài khoản?</span>
        <a href="${pageContext.request.contextPath}/auth?page=signUp" class="text-decoration-none fw-semibold">Đăng ký ngay</a>
    </div>

</div>

<script>
    function quickLogin(username, password, buttonElement) {
        // Điền dữ liệu vào input
        const uInput = document.getElementById('usernameInput');
        const pInput = document.getElementById('passwordInput');
        const submitBtn = document.getElementById('submitBtn');
        const form = document.getElementById('loginForm');

        uInput.value = username;
        pInput.value = password;

        // Đổi trạng thái nút bấm
        if (buttonElement) {
            buttonElement.innerHTML = '<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span> Đang vào...';
        }
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span> Đang đăng nhập...';
        submitBtn.disabled = true;

        // Tự động submit sau 250ms để tạo hiệu ứng mượt mà
        setTimeout(function() {
            form.submit();
        }, 250);
    }
</script>

</body>
</html>
