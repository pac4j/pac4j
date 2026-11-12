// Adds a "Copy" button at the top right of every code block.
(function () {
    if (!navigator.clipboard && !document.queryCommandSupported) return;

    var COPY = '<svg viewBox="0 0 16 16" aria-hidden="true" focusable="false"><path fill="none" stroke="currentColor" stroke-width="1.3" d="M5.5 4.5h7a1 1 0 0 1 1 1v8a1 1 0 0 1-1 1h-7a1 1 0 0 1-1-1v-8a1 1 0 0 1 1-1z"/><path fill="none" stroke="currentColor" stroke-width="1.3" d="M3.5 11.5h-1a1 1 0 0 1-1-1v-8a1 1 0 0 1 1-1h7a1 1 0 0 1 1 1v1"/></svg>';
    var CHECK = '<svg viewBox="0 0 16 16" aria-hidden="true" focusable="false"><path fill="none" stroke="currentColor" stroke-width="1.6" d="M2.5 8.5l3.5 3.5 7.5-8"/></svg>';

    var blocks = document.querySelectorAll('main pre');
    for (var i = 0; i < blocks.length; i++) {
        addButton(blocks[i]);
    }

    function addButton(pre) {
        // the button must sit outside the scrollable <pre>, otherwise it scrolls with long lines
        var wrap = pre.parentNode;
        if (!wrap.classList.contains('highlight')) {
            wrap = document.createElement('div');
            pre.parentNode.insertBefore(wrap, pre);
            wrap.appendChild(pre);
        }
        wrap.classList.add('code-copy-wrap');

        var button = document.createElement('button');
        button.type = 'button';
        button.className = 'code-copy';
        button.title = 'Copy to clipboard';
        button.setAttribute('aria-label', 'Copy to clipboard');
        button.innerHTML = COPY + '<span>Copy</span>';
        button.addEventListener('click', function () {
            var text = pre.innerText.replace(/\n$/, '');
            if (navigator.clipboard) {
                navigator.clipboard.writeText(text).then(done, function () { if (select(pre)) done(); });
            } else if (select(pre)) {
                done();
            }
        });
        wrap.appendChild(button);

        function done() {
            button.innerHTML = CHECK + '<span>Copied</span>';
            button.classList.add('copied');
            setTimeout(function () {
                button.innerHTML = COPY + '<span>Copy</span>';
                button.classList.remove('copied');
            }, 1500);
        }
    }

    // fallback for browsers that refuse the Clipboard API: select the code and run the old copy command
    function select(pre) {
        var selection = window.getSelection();
        var range = document.createRange();
        range.selectNodeContents(pre);
        selection.removeAllRanges();
        selection.addRange(range);
        var copied = false;
        try { copied = document.execCommand('copy'); } catch (e) { /* not supported either */ }
        selection.removeAllRanges();
        return copied;
    }
})();
