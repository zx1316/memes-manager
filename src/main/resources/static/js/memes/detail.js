{
    stateHelper.createState('loading', false);

    function getQueryVariable(variable) {
        const query = window.location.search.substring(1);
        const vars = query.split("&");
        for (let i = 0; i < vars.length; i++) {
            const pair = vars[i].split("=");
            if (pair[0] === variable) {
                return pair[1];
            }
        }
        return null;
    }

    function formatDate(timestamp){
        const date =new Date(timestamp);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        return`${year}-${month}-${day}T${hours}:${minutes}:${seconds}+08:00`;
    }

    function showInfo(title, body) {
        document.querySelector('#info-modal-title').innerText = title;
        document.querySelector('#info-modal-body').innerText = body;
        new bootstrap.Modal(document.querySelector('#info-modal')).show();
    }

    stateHelper.addObserver(document.querySelector('#save-btn'), (ele, loading) => {ele.disabled = loading}, 'loading');
    stateHelper.addObserver(document.querySelector('#delete-btn'), (ele, loading) => {ele.disabled = loading}, 'loading');

    document.querySelector('#save-btn').addEventListener('click', () => {
        stateHelper.setState('loading', true);
        const arr = document.querySelector('#tags-textarea').value.split(',')
            .map(tag => tag.trim())
            .filter(tag => tag.length > 0);

        fetch(`/api/v1/memes/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`
            },
            body: JSON.stringify({tags: arr, favorite: false})
        })
            .then(res => {
                stateHelper.setState('loading', false);
                if (res.ok) {
                    showInfo('ℹ️信息', '标签修改成功。');
                    return;
                }
                throw new Error(res.status.toString());
            })
            .catch(err => {
                if (err.message === '401' || err.message === '403') {
                    new bootstrap.Modal(document.querySelector('#auth-modal')).show();
                } else if (err.message === '400') {
                    showInfo('❌错误', '标签修改失败，请检查标签格式。');
                } else {
                    showInfo('❌错误', `标签修改失败，出现错误：${err.message}`);
                }
            });
    });

    document.querySelector('#confirm-delete-btn').addEventListener('click', () => {
        stateHelper.setState('loading', true);
        fetch(`/api/v1/memes/${id}`, {
            method: 'DELETE',
            headers: {'Authorization': `Bearer ${sessionStorage.getItem('token')}`}
        })
            .then(res => {
                stateHelper.setState('loading', false);
                if (res.ok) {
                    location.href = 'manager.html';
                    return;
                }
                throw new Error(res.status.toString());
            })
            .catch(err => {
                if (err.message === '401' || err.message === '403') {
                    new bootstrap.Modal(document.querySelector('#auth-modal')).show();
                } else {
                    showInfo('❌错误', `删除失败，出现错误：${err.message}`);
                }
            });
    });

    const id = getQueryVariable('id');
    const originalImg = document.querySelector('#original-img');
    originalImg.src = '/api/v1/memes/' + id + '/raw';
    originalImg.addEventListener('click', () => {
        location.href = '/api/v1/memes/' + id + '/raw';
    });
    fetch(`/api/v1/memes/${id}`)
        .then(res => res.json())
        .then(result => {
            document.querySelector('#tags-textarea').value = result.memeTags.join();
            document.querySelector('#upload-time-text').innerText = formatDate(result.uploadTime);
        })
        .catch(err => console.log(err));
}
