{
    stateHelper.createState('loading', true);
    stateHelper.createState('picking', false);
    stateHelper.createState('selectedNum', 0);
    stateHelper.createState('fuzzy', false);
    stateHelper.createState('canUpload', false);

    const imgShower = document.querySelector('#img-shower');
    const fileUploadInput = document.querySelector('#file-input');
    const tagsUploadInput = document.querySelector('#tags-input1');
    const checkedId = new Set();
    let totalImgNum = 0;
    let isFilterSet = false;

    function initTooltip() {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(tooltipTrigger => new bootstrap.Tooltip(tooltipTrigger));
    }

    function showInfo(title, body) {
        document.querySelector('#info-modal-title').innerText = title;
        document.querySelector('#info-modal-body').innerText = body;
        new bootstrap.Modal(document.querySelector('#info-modal')).show();
    }

    function initStorage() {
        if (sessionStorage.getItem('fuzzy') === null) {
            sessionStorage.setItem('fuzzy', 'false');
        }
        if (sessionStorage.getItem('tags') === null) {
            sessionStorage.setItem('tags', '');
        }
    }

    function resetFilter() {
        stateHelper.setState('fuzzy', sessionStorage.getItem('fuzzy') !== 'false');
        document.querySelector('#tags-input').value = sessionStorage.getItem('tags');
    }

    function addImgs(result) {
        totalImgNum = result.total;
        result.ids.forEach(id => {
            const a = document.createElement('a'); // 创建一个a元素
            a.href = 'javascript:void(0);';
            a.setAttribute('data-img-id', id);
            a.className = "position-relative";
            a.innerHTML = `<img class="auto-thumbnail" src="/api/v1/memes/${id}/thumbnail" loading="lazy">
                            <svg class="img-check-i"></svg>
                            <svg class="img-check-bg-i"><use href="#checkbox-bg"></use></svg>`;
            stateHelper.createState(`checked-${id}`, false);
            stateHelper.addObserver(a, (ele, checked, picking) => {
                const svg = ele.querySelector('.img-check-i');
                const svgbg = ele.querySelector('.img-check-bg-i');
                if (picking) {
                    svg.classList.remove('visually-hidden');
                    svgbg.classList.remove('visually-hidden');
                    if (checked) {
                        svg.innerHTML = '<use href="#checkbox-marked"></use>';
                        svg.classList.add('img-check-i-marked');
                        svgbg.classList.add('img-check-bg-i-marked');
                    } else {
                        svg.innerHTML = '<use href="#checkbox"></use>';
                        svg.classList.remove('img-check-i-marked');
                        svgbg.classList.remove('img-check-bg-i-marked');
                    }
                } else {
                    svg.classList.add('visually-hidden');
                    svgbg.classList.add('visually-hidden');
                }
            }, `checked-${id}`, 'picking');
            imgShower.appendChild(a); // 将a添加到body中
        });
        stateHelper.setState('loading', false);
    }

    function queryImgsError(error) {
        stateHelper.setState('loading', false);
        if (error.message === '401' || error.message === '403') {
            new bootstrap.Modal(document.querySelector('#auth-modal')).show();
        } else {
            showInfo('❌错误', `查询失败，出现错误：${error.message}`);
        }
    }

    function queryImgs() {
        stateHelper.setState('loading', true);
        stateHelper.setState('selectedNum', 0);
        imgShower.querySelectorAll('a').forEach(a => stateHelper.deleteState(`checked-${a.getAttribute('data-img-id')}`));
        imgShower.innerHTML = '';
        checkedId.clear();

        const tagsInput = document.querySelector('#tags-input');
        if (!stateHelper.getState('fuzzy')) {
            const arr = tagsInput.value.split(',')
                .map(tag => tag.trim())
                .filter(tag => tag.length > 0);
            fetch('/api/v1/memes/filter', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                },
                body: JSON.stringify({tags: arr})
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    }
                    throw new Error(response.status.toString());
                })
                .then(result => addImgs(result))
                .catch(error => queryImgsError(error));
        } else {
            fetch('/api/v1/memes/fuzzy-filter', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                },
                body: JSON.stringify({keyword: tagsInput.value.trim()})
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    }
                    throw new Error(response.status.toString());
                })
                .then(result => addImgs(result))
                .catch(error => queryImgsError(error));
        }
    }

    function addAndFilterCommon(ele, picking, loading) {
        if (picking) {
            ele.classList.add('visually-hidden');
        } else {
            ele.classList.remove('visually-hidden');
            ele.disabled = loading;
        }
    }

    const imgShowerAndEndCommon = (ele, loading) => {loading ? ele.classList.add('visually-hidden') : ele.classList.remove('visually-hidden')};
    stateHelper.addObserver(document.querySelector('#upload-confirm-btn'), (ele, canUpload) => {ele.disabled = !canUpload}, 'canUpload');
    stateHelper.addObserver(document.querySelector('#add-btn'), addAndFilterCommon, 'picking', 'loading');
    stateHelper.addObserver(document.querySelector('#filter-btn'), addAndFilterCommon, 'picking', 'loading');
    stateHelper.addObserver(document.querySelector('#tag-query-radio'), (ele, fuzzy) => {ele.checked = !fuzzy}, 'fuzzy');
    stateHelper.addObserver(document.querySelector('#fuzzy-query-radio'), (ele, fuzzy) => {ele.checked = fuzzy}, 'fuzzy');
    stateHelper.addObserver(document.querySelector('#delete-modal-body'), (ele, selectedNum) => {ele.innerHTML = `确定要删除这${selectedNum}个Meme吗？`}, 'selectedNum');
    stateHelper.addObserver(document.querySelector('#header-heading'), (ele, picking, selectedNum) => {ele.innerText = picking ? `已选择${selectedNum}项` : 'Meme管理器'}, 'picking', 'selectedNum');
    stateHelper.addObserver(imgShower, imgShowerAndEndCommon, 'loading');
    stateHelper.addObserver(document.querySelector('#end-text'), imgShowerAndEndCommon, 'loading');
    stateHelper.addObserver(document.querySelector('#waiting-div'), (ele, val) => {val ? ele.classList.remove('visually-hidden') : ele.classList.add('visually-hidden')}, 'loading');
    stateHelper.addObserver(document.querySelector('#delete-btn'), (ele, picking, loading, selectedNum) => {
        if (picking) {
            ele.classList.remove('visually-hidden');
            ele.disabled = selectedNum <= 0 || loading;
        } else {
            ele.classList.add('visually-hidden');
        }
    }, 'picking', 'loading', 'selectedNum');

    stateHelper.addObserver(document.querySelector('#check-all-btn'), (ele, picking, loading, selectedNum) => {
        if (picking) {
            ele.classList.remove('visually-hidden');
            ele.disabled = loading;
            if (selectedNum === totalImgNum && totalImgNum !== 0) {
                ele.classList.remove('btn-outline-primary');
                ele.classList.add('btn-primary');
            } else {
                ele.classList.add('btn-outline-primary');
                ele.classList.remove('btn-primary');
            }
        } else {
            ele.classList.add('visually-hidden');
        }
    }, 'picking', 'loading', 'selectedNum');

    stateHelper.addObserver(document.querySelector('#add-check-btn'), (ele, picking) => {
        if (picking) {
            ele.classList.remove('btn-outline-primary');
            ele.classList.add('btn-primary');
        } else {
            ele.classList.add('btn-outline-primary');
            ele.classList.remove('btn-primary');
        }
    }, 'picking');

    stateHelper.addObserver(document.querySelector('#filter-label'), (ele, fuzzy) => {
        ele.innerHTML = fuzzy ? `关键词
                <svg id="tags-input-help" class="help-i" data-bs-toggle="tooltip" data-bs-placement="right" data-bs-original-title="不支持多关键词，忽略关键词首尾的空白符">
                <use href="#help"></use>
                </svg>` : `标签
                <svg id="tags-input-help" class="help-i" data-bs-toggle="tooltip" data-bs-placement="right" data-bs-original-title="多个标签间用英文逗号隔开，忽略标签首尾的空白符">
                <use href="#help"></use>
                </svg>`;
        initTooltip();
    }, 'fuzzy');

    tagsUploadInput.addEventListener('input', () => stateHelper.setState('canUpload', tagsUploadInput.value.length > 0 && fileUploadInput.files.length > 0));
    fileUploadInput.addEventListener('change', () => stateHelper.setState('canUpload', tagsUploadInput.value.length > 0 && fileUploadInput.files.length > 0));
    document.querySelector('#filter-modal').addEventListener('hidden.bs.modal', () => {isFilterSet ? isFilterSet = false : resetFilter()});
    document.querySelector('#tag-query-radio').addEventListener('change', () => stateHelper.setState('fuzzy', false));
    document.querySelector('#fuzzy-query-radio').addEventListener('change', () => stateHelper.setState('fuzzy', true));
    document.querySelector('#add-check-btn').addEventListener('click', () => stateHelper.setState('picking', !stateHelper.getState('picking')));
    document.querySelector('#apply-filter-btn').addEventListener('click', () => {
        isFilterSet = true;
        sessionStorage.setItem('fuzzy', stateHelper.getState('fuzzy'));
        sessionStorage.setItem('tags', document.querySelector('#tags-input').value);
        queryImgs();
    });

    document.querySelector('#confirm-delete-btn').addEventListener('click', () => {
        stateHelper.setState('loading', true);
        fetch('/api/v1/memes', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`
            },
            body: JSON.stringify(Array.from(checkedId)),
        })
            .then(response => {
                stateHelper.setState('loading', false);
                if (response.ok) {
                    checkedId.forEach(id => {
                        stateHelper.getObservers(`checked-${id}`)[0].remove();  // remove the thumbnail from dom
                        stateHelper.deleteState(`checked-${id}`);   // remove the state
                    });
                    totalImgNum -= checkedId.size;
                    checkedId.clear();
                    stateHelper.setState('selectedNum', 0);
                    return;
                }
                throw new Error(response.status.toString());
            })
            .catch(error => {
                if (error.message === '401' || error.message === '403') {
                    new bootstrap.Modal(document.querySelector('#auth-modal')).show();
                } else {
                    showInfo('❌错误', `删除失败，出现错误：${error.message}`);
                }
            });
    });

    document.querySelector('#check-all-btn').addEventListener('click', () => {
        if (stateHelper.getState('selectedNum') !== totalImgNum) {
            imgShower.querySelectorAll('a').forEach(a => {
                const id = a.getAttribute('data-img-id');
                stateHelper.setState(`checked-${id}`, true);
                checkedId.add(Number(id));
            });
            stateHelper.setState('selectedNum', totalImgNum);
        } else {
            imgShower.querySelectorAll('a').forEach(a => stateHelper.setState(`checked-${a.getAttribute('data-img-id')}`, false));
            stateHelper.setState('selectedNum', 0);
            checkedId.clear();
        }
    });

    document.querySelector('#upload-confirm-btn').addEventListener('click', () => {
        stateHelper.setState('loading', true);
        const formData = new FormData();
        formData.append('file', fileUploadInput.files[0]);
        formData.append('tags', tagsUploadInput.value);
        fetch('/api/v1/memes', {
            method: 'POST',
            headers: {'Authorization': `Bearer ${sessionStorage.getItem('token')}`},
            body: formData
        })
            .then(response => {
                stateHelper.setState('loading', false);
                if (response.ok) {
                    fileUploadInput.value = '';
                    tagsUploadInput.value = '';
                    stateHelper.setState('canUpload', false);
                    showInfo('ℹ️信息', '上传成功，请刷新或查询以更新显示。');
                    return;
                }
                throw new Error(response.status.toString());
            })
            .catch(error => {
                if (error.message === '401' || error.message === '403') {
                    new bootstrap.Modal(document.querySelector('#auth-modal')).show();
                } else if (error.message === '400') {
                    showInfo('❌错误', '上传失败，请检查标签格式。');
                } else if (error.message === '409') {
                    showInfo('❌错误', '上传失败，这个Meme已经在库中了。');
                } else if (error.message === '413') {
                    showInfo('❌错误', '上传失败，图片大小太大了。');
                } else if (error.message === '415') {
                    showInfo('❌错误', '上传失败，请检查图片格式。');
                } else {
                    showInfo('❌错误', `上传失败，出现错误：${error.message}`);
                }
            });
    });

    imgShower.addEventListener('click', e => {
        let targetA = e.target.closest('.position-relative');
        if (targetA) {
            const id = targetA.getAttribute('data-img-id');
            if (stateHelper.getState('picking')) {
                // 选择
                const state = 'checked-' + id;
                if (stateHelper.getState(state)) {
                    stateHelper.setState(state, false);
                    stateHelper.setState('selectedNum', stateHelper.getState('selectedNum') - 1);
                    checkedId.delete(Number(id));
                } else {
                    stateHelper.setState(state, true);
                    stateHelper.setState('selectedNum', stateHelper.getState('selectedNum') + 1);
                    checkedId.add(Number(id));
                }
            } else {
                location.href = `detail.html?id=${id}`;     // 详情页面
            }
        }
    });

    new ResizeObserver(entries => {
        const container = entries[0];
        const width = container.contentRect.width;
        const mqList = window.matchMedia('(min-width: 768px)');
        const num = Math.max(1, mqList.matches ? Math.round(width / 192) : Math.floor(width / 112));
        const finalWidth = width / num;
        document.querySelector('#auto-thumbnail-style').innerHTML = `.auto-thumbnail {
            width: ${finalWidth}px;
            height: ${finalWidth}px;
            object-fit: cover;
            padding: 0.075rem;
        }`;
    }).observe(imgShower);

    initTooltip();
    initStorage();
    resetFilter();
    queryImgs();
}
