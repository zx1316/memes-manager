document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById("form-signin");
    const usernameInput = document.getElementById("username-input");
    const passwordInput = document.getElementById("password-input");
    const submitBtn = document.querySelector("button[type='submit']");

    function showError(errStr) {
        document.querySelector('#err-modal-body').innerText = errStr;
        new bootstrap.Modal(document.querySelector('#err-modal')).show();
    }

    form.onsubmit = function(e) {
        e.preventDefault();
        if (usernameInput.value === '' || passwordInput.value === '') {
            showError('请输入用户名和密码！');    // 显示不能为空
            return;
        }

        submitBtn.disabled = true;
        fetch('/api/v1/auth', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({username: usernameInput.value, password: passwordInput.value})
        })
            .then(response => {
                if (response.ok) {
                    return response.text();
                }
                showError('用户名或密码错误！');
                submitBtn.disabled = false;
                throw new Error(response.status.toString());
            })
            .then(result => {
                sessionStorage.setItem('token', result);
                window.location.href = '/apps.html';
            })
            .catch(error => console.log(error));
    };
});
