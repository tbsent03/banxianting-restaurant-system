export function formatImg(url) {
    if (!url) return '';
    // 如果当前不是用 localhost 访问的（比如用手机 IP 访问），就把图片路径里的 localhost 替换成当前 IP
    return url.replace('localhost', window.location.hostname);
}