const qiniu = (name) => {
  return `https://bizzan.oss-cn-hangzhou.aliyuncs.com/smeditor_icons/${name}.svg`
}

const names = ['bold', 'italic', 'underline', 'strikethrough', 'color', 'listOrdered', 'listUnordered', 'listCheck', 'indent', 'outdent', 'alignLeft', 'alignCenter', 'alignRight', 'undo', 'redo', 'removeFormat', 'insertVideo', 'insertLink', 'insertImage', 'insertLine', 'insertQuote', 'insertBlock']
const icons = {}

names.forEach(name => {
  icons[name] = qiniu(name.toLowerCase())
})

export default icons
