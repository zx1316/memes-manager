{
    new ResizeObserver(entries => {
        const container = entries[0];
        const height = container.borderBoxSize[0].blockSize;
        document.querySelector('#main-container').style.marginTop = height + 'px';
    }).observe(document.querySelector('#header'));
}
