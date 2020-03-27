<template>
  <div class="smeditor" id="smeditor">
    <div class="buttons" :class="buttonsBarFixed == true ? 'isFixed' :''">
      <button type="button" class='undo' @click='undo' v-on:mouseover.stop='mouseover($event)' title="撤销">
        <img :src="icons.undo"></img>
      </button>
      <button type="button" class='redo' @click='redo' v-on:mouseover.stop='mouseover($event)' title="重做">
        <img :src='icons.redo'></img>
      </button>
      <button type="button" class='remove-format'
              title="清除"
              @click='removeFormat'
              v-on:mouseover.stop='mouseover($event)'>
        <img :src='icons.removeFormat'></img>
      </button>
      <button type="button" class='set-font' @click.stop="titleButtonClick">
        <span>H</span>
         <title-picker v-bind:titlePickerClick="titlePickerClick" v-show="isTitlePickerShow"></title-picker>
      </button>
      <button type="button" class="font-size"
              title="字号"
              @click="isFontSizePickerShow = !isFontSizePickerShow"
              v-on:mouseover.stop='mouseover($event)'>
        <span> {{fontSize}} </span>
        <font-size-picker v-bind:FontSizePickerClick="fontSizePickerClick" v-show="isFontSizePickerShow"></font-size-picker>
      </button>
      <button type="button" v-for='(name, index) in basicIcons'
              @click='basicStyleClick(name)'
              v-bind:class="{buttonsActive: styles.indexOf(name) > -1}"
              v-on:mouseover.stop='mouseover($event)'
              v-bind:title='basicStyleNames[Object.keys(basicIcons).indexOf(name)]'>
        <img :src='icons[name]'></img>
      </button>
      <button type="button" v-on:mouseover.stop='mouseover($event)' title="文本颜色">
        <img :src='icons.color' @click="isColorPickerShow = !isColorPickerShow"></img>
        <color-picker :ColorPickerClick="colorPickerClick" v-show="isColorPickerShow"></color-picker>
      </button>
      <button type="button" class='indent' @click.stop='indent' v-on:mouseover.stop='mouseover($event)' title="增加缩进">
        <img :src='icons.indent'></img>
      </button>
      <button type="button" class='outdent' @click.stop='outdent' v-on:mouseover.stop='mouseover($event)' title="减少缩进">
        <img :src='icons.outdent'></img>
      </button>
      <button type="button" class='insert-ol' @click='insertList("OrderedList")' v-on:mouseover.stop='mouseover($event)' title="有序列表">
        <img :src='icons.listOrdered'></img>
      </button>
      <button type="button" class='insert-ul' @click='insertList("UnorderedList")' v-on:mouseover.stop='mouseover($event)' title="无序列表">
        <img :src='icons.listUnordered'></img>
      </button>
      <button type="button" class='align-left' @click='align("Left")' v-on:mouseover.stop='mouseover($event)' title="左对齐">
        <img :src='icons.alignLeft'></img>
      </button>
      <button type="button" class='align-center' @click='align("Center")' v-on:mouseover.stop='mouseover($event)' title="居中对齐">
        <img :src='icons.alignCenter'></img>
      </button>
      <button type="button" class='align-right' @click='align("Right")'  v-on:mouseover.stop='mouseover($event)' title='右对齐'>
        <img :src='icons.alignRight'></img>
      </button>
      <button type="button" class="insert-quote" @click='insertQuote'>
        <img :src="icons.insertQuote">
      </button>
      <button type="button" class='insert-link' @click='insertLinkClick'  v-on:mouseover.stop='mouseover($event)' title='插入链接'>
        <img :src='icons.insertLink'></img>
      </button>
      <button type="button" class='insert-options' @click="isInsertShow = !isInsertShow">
        <span class="insert-options-label"></span>
        <insert-options
         v-show="isInsertShow"
         :insertImage="insertImageClick"
         :insertLine="insertLine"
         :uploadImages='uploadImages'
         :insertVideo="insertVideoClick"
         :insertBlock="insertBlock"
         ></insert-options>
      </button>
      <button type="button" class="backup" @click='backupClick' v-on:mouseover.stop='mouseover($event)' title="Ctrl + S"></button>
      <!-- <button type="button" class="restore" @click='restoreClick'></button> -->
      <!-- <button type="button" class="preview" @click='previewClick' v-on:mouseover.stop='mouseover($event)' title="Ctrl + P"></button> -->
    </div>
    <div
      contenteditable="true"
      autocorrect="off"
      autocomplete="off"
      spellcheck="false"
      class="input-area"
      id="input-area"
      v-on:mouseup="mouseup"
      v-on:keyup.enter="kenter"
      v-on:keyup.ctrl.83="backupClick"
      v-on:keyup.ctrl.80="previewClick"
      >
    </div>
    <p class="select-words" v-show="selectWords">{{selectWords.length}}个字</p>
    <insert-link
      :insertLink='insertLink'
      :propText='insertLinkSection.text'
      :propLink='insertLinkSection.link'
      v-show='isInsertLinkShow'
      :cancel='insertLinkCancel'
    ></insert-link>
    <insert-video :insertVideo='insertVideo' v-show='isInsertVideoShow' :cancel='insertVideoCancel'></insert-video>
  </div>
</template>

<script>
import icons from './icons.js'
import ColorPicker from './ColorPicker.vue'
import TitlePicker from './TitlePicker.vue'
import InsertLink from './InsertLink.vue'
import InsertVideo from './InsertVideo.vue'
import Insert from './Insert.vue'
import FontSizePicker from './FontSizePicker'
import { getStore, removeStore, setStore } from '@/config/storage';
import { helpManageDetail, announceDetail, activityDetail, envelopeDetail } from '@/service/getData';

const remove = function (arr, val) {
  let index = arr.indexOf(val)
  if (index > -1) {
    arr.splice(index, 1)
  }
}
const editorElement = function () {
  return document.querySelector('.smeditor .input-area')
}

export default {
  name: 'smeditor',
  components: {
    'color-picker': ColorPicker,
    'title-picker': TitlePicker,
    'insert-options': Insert,
    'insert-link': InsertLink,
    'insert-video': InsertVideo,
    FontSizePicker
  },
  props: ['config'],
  data () {
    return {
      // 图标
      icons: icons,
      // 样式
      styles: [],
      // 基本样式名称
      basicIcons: ['bold', 'underline', 'italic', 'strikethrough'],
      basicStyleNames: ['粗体', '斜体', '下划线', '中划线'],
      // 调色盘是否显示
      isColorPickerShow: false,
      // 标题选择是否显示
      isTitlePickerShow: false,
      // 字号选项是否显示
      isFontSizePickerShow: false,
      // 插入选项是否显示
      isInsertShow: false,
      // 插入链接是否显示
      isInsertLinkShow: false,
      // 插入视频是否显示
      isInsertVideoShow: false,
      // 选中文字内容
      selectWords: '',
      // 字号
      fontSize: 16,
      // 光标
      cursor: {},
      // 鼠标选中节点
      selectNode: {},
      buttonsBarFixed: false,
      insertLinkSection: {
        node: '',
        start: 0,
        end: 0,
        text: '',
        link: ''
      }
    }
  },
  methods: {
    // 回车事件
    kenter (e) {
      e.stopPropagation()
      if (this.styles.length === 0) {
        return false
      }
    },
    // 鼠标事件
    mouseup () {
      this.selectNode = getSelectedNode()
      const str = window.getSelection().toString()
      if (str.length < 1) {
        return false
      }
      this.selectWords = str
      setTimeout(() => {
        this.selectWords = ''
      }, 1500)
    },
    // 鼠标事件
    mouseover (event) {
      let target = ''
      event.path.forEach(el => {
        if (el.localName === 'button' && target === '') {
          target = el
        }
      })
      // tippy(target, {
      //   placement: 'bottom',
      //   animation: 'shift-away',
      //   duration: 100,
      //   arrow: true
      // })
    },
    // 重做
    redo () {
      document.execCommand('redo')
    },
    // 撤销
    undo () {
      document.execCommand('undo')
    },
    // 移除格式
    removeFormat () {
      document.execCommand('removeFormat', false, '')
      document.execCommand('insertHTML', false, `<p></p>`)
      this.styles = []
      this.FontSize = 16
    },
    // 字号选项点击
    fontSizePickerClick (size, index) {
      document.execCommand('FontSize', false, index + 1)
      this.fontSize = size
      this.closeAlert()
    },
    // 标题按钮点击
    titleButtonClick () {
      getCursor(this)
      this.isTitlePickerShow = !this.isTitlePickerShow
    },
    // 标题选项点击
    titlePickerClick (size, index) {
      this.closeAlert()
      let html = ''
      restoreCursor(this)
      let node = getSelectedNode()
      console.log(node, node.localName)
      // 一共分六种情况
      // 1. empty <h>
      // 2. empty <p>
      if (node.className === editorElement().className ||
          node.className.startsWith('smeditor')) {
        if (size === '正文') {
          document.execCommand('insertHTML', false, `<p><span><br></span></p>`)
        } else {
          document.execCommand('insertHTML', false, `<${size}><span><br></span></${size}>`)
        }
        return false
      }
      // 3 h -> p
      // 4 h -> h+
      // 5 p -> h
      // 6 p -> p
      if (node.localName.startsWith('h') && size === '正文') {
        html = `<p>${node.innerHTML}</p>`
      } else if (node.localName.startsWith('h') && size.startsWith('H')) {
        html = `<${size}>${node.innerHTML}</${size}>`
      } else if (node.innerHTML.length > 0 && node.localName.startsWith('h') === false && size !== '正文') {
        html = `<${size}>${node.innerHTML}</${size}>`
      } else {
        html = `<p>${node.innerHTML}</p>`
      }
      restoreCursor(this)
      node.outerHTML = ''
      document.execCommand('insertHTML', false, html)
      // const range = document.createRange()
      // range.selectNodeContents(node)
      // range.collapse(false)
      // const selection = window.getSelection()
      // selection.removeAllRanges()
      // selection.addRange(range)
    },
    // 基本样式点击
    basicStyleClick (name) {
      execCmd(this, () => {
        document.execCommand(name, false, '')
        if (this.styles.indexOf(name) === -1) {
          this.styles.push(name)
        } else {
          remove(this.styles, name)
        }
      })
    },
    // 调色盘点击
    colorPickerClick (color) {
      // document.querySelector('.ql-color-label').style.fill = color
      execCmd(this, () => {
        document.execCommand('forecolor', false, color)
        this.closeAlert()
      })
    },
    // 点击插入图片
    insertImageClick (size, index) {
      this.closeAlert()
    },
    // 上传图片
    uploadImages (files) {

      this.$emit('isUploading', true)

      Array.from(files).forEach(file => {
        this.upload(file, (url) => {
          this.insertImageHtml(url)
        })
      })
    },
    upload (file, success) {
      let url = this.config.uploadUrl
      let xhr = new XMLHttpRequest()
      let form = new FormData()
      let self = this
      form.append(this.config.uploadName, file)
      xhr.open('POST', url, true)
      xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
          if (xhr.status === 200) {
            const json = JSON.parse(xhr.responseText)
            const imgUrl = self.config.uploadCallback(json)
            success(imgUrl)
          } else {
            if (self.config.uploadFailed) {
              self.config.uploadFailed(xhr.responseText)
            }
            // 测试网站, 模拟上传
            if (location.href.indexOf('ericjj.com/smeditor.github.io') > 0) {
              const imgUrl = self.config.uploadCallback('')
              success(imgUrl)
            }
          }
        }
      }
      xhr.send(form)
    },
    insertImageHtml (url) {
      document.execCommand('insertHTML', false, `
              <br><div class="image-desc" style="text-align: center; color: #333;">
                <img class="uploaded-img" src=${url} max-width="100%" width="auto" height="auto">
              </div>`)
    },
    // 点击插入链接
    insertLinkClick () {
      this.closeAlert()
      this.insertLinkSection.text = window.getSelection().toString()
      getCursor(this)
      this.isInsertLinkShow = true
    },
    // 插入链接
    insertLink (url, title) {
      restoreCursor(this)
      const node = getSelectedNode()
      if (node.localName === 'a') {
        node.outerHTML = `<a href=${url} target="_blank">${title}</>`
        return false
      }
      document.execCommand('insertHTML', false, `<a href=${url} target="_blank">${title}</a>`)
    },
    // 取消插入链接
    insertLinkCancel () {
      this.closeAlert()
      this.isInsertLinkShow = false
    },
    // 点击插入链接
    insertVideoClick () {
      this.closeAlert()
      setTimeout(() => {
        this.isInsertVideoShow = true
      }, 200)
      getCursor(this)
    },
    // 插入链接
    insertVideo (text) {
      restoreCursor(this)
      document.execCommand('insertHTML', false, text)
      this.closeAlert()
    },
    // 取消插入链接
    insertVideoCancel () {
      this.closeAlert()
    },
    // 插入一条线
    insertLine () {
      this.closeAlert()
      document.execCommand('insertHTML', false, `<p><hr></p>`)
    },
    // 插入代码块
    insertBlock () {
      this.closeAlert()
      document.execCommand('insertHTML', false, `<pre><code><span><br><span></code></pre>`)
    },
    // 插入引用
    insertQuote () {
      let node = getSelectedNode()
      // console.log(node)
      if (node.className === 'input-area') {
        return false
      }
      if (node.localName === 'blockquote' && node.parentNode.className !== 'input-area') {
        let str = node.innerHTML
        console.log(node.parentNode)
        node.parentNode.outerHTML = ''
        document.execCommand('insertHTML', false, `<br><p>${str}</p>`)
        this.insertEmptyP()
      } else if (node.className === 'blockquote') {
        let str = node.innerHTML
        console.log(str)
        document.execCommand('insertHTML', false, `<p>${str}</p>`)
        this.insertEmptyP()
        node.outerHTML = ''
      } else if (node.innerHTML.length > 0 &&
        node.className !== 'smeditor' &&
        node.className !== editorElement().className &&
        node.className !== 'blockquote') {
        document.execCommand('insertHTML', false, `<div class="blockquote"><blockquote style="color: #B2B2B2; padding-left: 15px; border-left: 5px solid #B2B2B2; margin-top: 0px; margin-bottom: 0px;">${node.innerHTML}</blockquote></div>`)
        node.outerHTML = ''
      } else {
        document.execCommand('insertHTML', false, `<div class="blockquote"><blockquote style="color: #B2B2B2; padding-left: 15px; border-left: 5px solid #B2B2B2; margin-top: 0px; margin-bottom: 0px;"><span><br></span></blockquote></div>`)
      }
    },
    // 插入 有序/无序 列表
    insertList (name) {
      this.closeAlert()
      document.execCommand(`insert${name}`, false, '')
    },
    // 插入 todo , 暂时不做
    insertCheck () {
      this.closeAlert()
      document.execCommand('insertHTML', false, `
        <ul class="unchecked-list"><li class="unchecked">&nbsp</li></ul>
      `)
      document.querySelectorAll('.unchecked-list').forEach(ul => {
        ul.childNodes.forEach(li => {
          li.onclick = function (event) {
            const name = event.target.className === 'unchecked' ? 'checked' : 'unchecked'
            event.target.className = name
            console.log(event.target.className)
          }
        })
      })
    },
    // 缩进+
    indent () {
      document.execCommand('indent', false, null)
    },
    // 缩进-
    outdent () {
      document.execCommand('outdent', false, null)
    },
    // 对齐
    align (name) {
      execCmd(this, () => {
        document.execCommand(`Justify${name}`)
      })
    },
    // 备份
    backupClick () {
      window.localStorage.setItem('smeditor', editorElement().innerHTML)
    },
    // 恢复
    restoreClick () {
      editorElement().innerHTML = window.localStorage.getItem('smeditor') || ''
    },
    // 预览预览
    previewClick () {
      window.localStorage.setItem('smeditorPreview', editorElement().innerHTML)
      const {href} = this.$router.resolve({
        name: 'Preview'
      })
      window.open(href, '_blank')
    },
    // 关闭弹窗
    closeAlert () {
      setTimeout(() => {
        this.isFontSizePickerShow = false
        this.isInsertShow = false
        this.isColorPickerShow = false
        this.isInsertVideoShow = false
        this.isTitlePickerShow = false
      }, 200)
    },
    insertEmptyP () {
      document.execCommand('insertHTML', false, '<p><span></br></span></p>')
    },
  },
  created() {
    this.$on('saveInner', () => {
      this.backupClick();
    })
  },
  mounted () {
    this.$nextTick(() => {
      removeStore('smeditor');
      let queryDetailId = getStore('manageID');

      if (!!queryDetailId) {

        let parentComp = this.config.parentName;

        if(parentComp==='helpManage') {

          helpManageDetail({ id: queryDetailId })
          .then(res => {
            setStore('smeditor', res.data.content);
            document.getElementById('input-area').innerHTML = getStore('smeditor') || '';
          })

        }else if (parentComp==='announce') {

          announceDetail(queryDetailId )
          .then(res => {
              setStore('smeditor', res.data.content);
              document.getElementById('input-area').innerHTML = getStore('smeditor') || '';
          })

        }else if(parentComp == 'activity') {
          activityDetail(queryDetailId )
          .then(res => {
              setStore('smeditor', res.data.content);
              document.getElementById('input-area').innerHTML = getStore('smeditor') || '';
          })
        }else if(parentComp == 'envelope') {
          envelopeDetail(queryDetailId )
          .then(res => {
              setStore('smeditor', res.data.detail);
              document.getElementById('input-area').innerHTML = getStore('smeditor') || '';
          })
        }
      }
    })
    setTimeout(() => {
      editorElement().focus()

      this.insertEmptyP()
      window.addEventListener('scroll', () => {
        if (this.config.onScroll) {
          this.config.onScroll()
        }
        var scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop
        var offsetTop = document.querySelector('.smeditor').offsetTop
        if (scrollTop > offsetTop) {
          this.buttonsBarFixed = true
        } else {
          this.buttonsBarFixed = false
        }
      })
    }, 100)
    addEvents(this)
  }
}

function addEvents (self) {
  editorElement().onfocus = function (event) {
    self.closeAlert()
  }
  // 回车事件
  editorElement().onkeypress = function (event) {
    const el = getSelectedNode()
    if (event.keyCode === 13 && isImageCaption(el)) {
      document.execCommand('removeFormat', false, '')
      this.innerHTML = this.innerHTML + '<p><br></p>'
      document.getSelection().collapse(this, this.childNodes.length - 1)
      return false
    }
    if (event.keyCode === 13 && el.className === 'blockquote' && el.lastChild.innerHTML === '<br>') {
      el.lastChild.innerHTML = ''
      document.execCommand('removeFormat', false, '')
      this.innerHTML = this.innerHTML + '<p></p>'
      document.getSelection().collapse(this, this.childNodes.length - 1)
      return false
    }

    if (event.keyCode === 13 && el.localName === 'pre' && el.lastChild.innerHTML === '<br>') {
      el.lastChild.innerHTML = ''
      document.execCommand('removeFormat', false, '')
      this.innerHTML = this.innerHTML + '<p><span><br></span></p>'
      document.getSelection().collapse(this, this.childNodes.length - 1)
      return false
    }
  }
  // 删除事件
  editorElement().onkeydown = function (event) {
    const el = getSelectedNode()
    if (event.keyCode === 8 && isImageDesc(el)) {
      el.innerHTML = '<p></p>'
      return false
    }
    if (el.innerHTML.length <= 1 &&
        event.keyCode === 8 &&
        isImageCaption(el)) {
      el.innerHTML = ''
      return false
    }
    if (getSelectedNode().innerHTML.length === 0) {
      document.execCommand('insertHTML', false, '<p><span></br></span></p>')
    }
  }
  editorElement().addEventListener('paste', function (event) {
    let items = (event.clipboardData || event.originalEvent.clipboardData).items
    for (let index in items) {
      let item = items[index]
      if (item.kind === 'file') {
        event.preventDefault()
        let blob = item.getAsFile()
        self.upload(blob, (url) => {
          self.insertImageHtml(url)
        })
      }
    }
  }, false)
}

function execCmd (self, callback) {
  getCursor(self)
  restoreCursor(self)
  callback()
}

function getCursor (self) {
  self.cursor = window.getSelection().getRangeAt(0)
}

// function delHtmlTag (str) {
//   return str.replace(/<[^>]+>/g, '')
// }

function isImageCaption (el) {
  return el.className === 'image-caption'
}

function isImageDesc (el) {
  return el.className === 'image-desc'
}

function getSelectedNode () {
  if (document.selection) {
    return document.selection.createRange().parentElement()
  } else {
    let selection = window.getSelection()
    if (selection.rangeCount > 0) {
      return selection.getRangeAt(0).startContainer.parentNode
    }
  }
}

function restoreCursor (self) {
  self.closeAlert()
  self.isInsertLinkShow = false
  editorElement().focus()
  const savedRange = self.cursor
  if (window.getSelection) {
    var s = window.getSelection()
    if (s.rangeCount > 0) {
      s.removeAllRanges()
    }
    s.addRange(savedRange)
  } else if (document.createRange) {
    window.getSelection().addRange(savedRange)
  } else if (document.selection) {
    savedRange.select()
  }
}
</script>
<style>
.smeditor {
  width: 70%;
  margin: 0 auto;
  position: relative;
  z-index: 2;
}
.smeditor .input-area {
  outline: none;
	overflow: auto;
  height: 400px;
  /* width: calc(100% - 20px); */
  padding: 10px;
  text-align: left;
  box-shadow: 0 1px 6px #ccc;
  background-color: #ffffff;
  border-color: transparent;
  letter-spacing: 1.5px;
  color: rgb(44, 62, 80);
  font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
}

.smeditor .buttons {
  position: -webkit-sticky;
  display: flex;
  justify-content: baseline;
  align-items: center;
  width: 100%;
  padding: 10px 0;
  background-color: rgba(240,240,240, 1);
  transition: position 0.3s;
}

.smeditor .isFixed {
  position: fixed;
  top: 0px;
  width: 70%;
}

.smeditor .buttons button {
  border: none;
  color: #000000;
  height: 27px;
  width: 30px;
  padding: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  outline: none;
  cursor: pointer;
  background-color: transparent;
  border: 1px solid transparent;
  position: relative;
}

.smeditor .buttons button:hover {
  border-color: #BFBFBF;
}

.smeditor .buttonsActive {
  border: 1px solid #BFBFBF !important;
}

.smeditor svg {
  fill: #555;
  height: 100%;
  width: 100%;
}

.smeditor input {
  border: none;
  color: #333;
  font-size: 16px;
  text-align: center;
  width: 100%;
}

.smeditor img {
  max-width: 100%;
  width: auto;
  height: auto;
  vertical-align: middle;
  border: 0;
}

.smeditor p {
  padding: 2px 0;
  margin: 0px;
}

.smeditor svg {
  cursor: pointer;
}

.smeditor a {
  color: #87AA99;
  margin-right: 3px;
}

.smeditor pre {
  display: block;
  padding: 9.5px;
  margin: 0 0 10px;
  font-size: 13px;
  line-height: 1.42857143;
  color: #333;
  word-break: break-all;
  word-wrap: break-word;
  background-color: #f5f5f5;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.smeditor pre code  {
  display: block;
  background-color: #f1f1f1;
  border-radius: 3px;
  padding: 3px 5px;
  margin: 0 3px;
}

.smeditor .blockquote {
  margin: 15px 0px;
}


.smeditor .backup,
.smeditor .restore,
.smeditor .preview{
  min-width: 40px !important;
}

.smeditor .backup:before,
.smeditor .restore:before,
.smeditor .preview:before {
  color: rgb(51, 51, 51);
  font-family: Helvetica, Tahoma, Arial, "Hiragino Sans GB", "Microsoft YaHei", SimSun, sans-serif;
  line-height: 28px;
  font-size: 12px;
  float: left;
  margin-left: 8px;
}

.smeditor .backup:before {
  content: "保存";
}

.smeditor .restore:before {
  content: "恢复";
}

.smeditor .preview:before {
  content: "预览";
}

.smeditor .select-words {
  position: fixed;
  right: calc(50% - 0px);
  margin-right: -100px;
  bottom: 60px;
  width: 200px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  color: #898989;
  opacity: 1;
  z-index: 1;
  background-color: #fff;
  border-radius: 1px;
  -webkit-border-radius: 1px;
  -moz-border-radius: 1px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-transition: all .2s ease-in;
  -moz-transition: all .2s ease-in;
  transition: all .2s ease-in;
  -webkit-box-shadow: 0 2px 8px hsla(0,0%,70%,.8);
  -moz-box-shadow: 0 2px 8px hsla(0,0%,70%,.8);
  -ms-box-shadow: 0 2px 8px hsla(0,0%,70%,.8);
  -o-box-shadow: 0 2px 8px hsla(0,0%,70%,.8);
  box-shadow: 0 2px 8px hsla(0,0%,70%,.8);
  transition-property: right;
  transition: all 0.3s;
  font-size: 14px;
}

.smeditor .font-size, .smeditor .insert-options {
  min-width: 40px !important;
}

.smeditor .font-size,
.smeditor .set-font {
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.smeditor .font-size span,
.smeditor .set-font span {
  font-size: 14px;
  color: #333;
  bottom: -0.5px;
  font-family: 'Helvetica,Tahoma,Arial,Hiragino Sans GB,Microsoft YaHei,SimSun,sans-serif';
  position: relative;
}

.smeditor .insert-options:before {
  content: "\63D2\5165";
  color: #333;
  font-family: 'Helvetica,Tahoma,Arial,Hiragino Sans GB,Microsoft YaHei,SimSun,sans-serif';
  line-height: 28px;
  font-size: 12px;
  float: left;
  margin-left: 8px;
}

.smeditor .insert-quote img {
  width: 20px;
  margin-bottom: 1px;
}

.unchecked-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.unchecked:before {
  content: "\F402";
  padding-left: 3px;
  margin-right: 6px;
  cursor: pointer;
  box-sizing: border-box;
}

.checked:before {
  content: "\F402";
}

</style>
