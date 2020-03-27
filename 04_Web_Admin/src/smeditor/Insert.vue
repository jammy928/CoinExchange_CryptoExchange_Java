<template>
  <div class="insert-options-items">
    <ul>
      <li v-for='(key, index) in names' @click='insertClick(key, index)'>
        <input
          v-if='index === 0'
          id="images_upload"
          ref="input"
          type="file"
          accept="image/gif,image/jpeg,image/jpg,image/png,image/svg"
          multiple="multiple"
          @change="handleFileChange"
        />
        <img :src='icons[key]' :class='key'></img>
        <span>{{labels[index]}}</span>
      </li>
    </ul>
  </div>
</template>
<script type="text/javascript">
import icons from './icons.js'
export default {
  name: 'Insert',
  data () {
    return {
      icons: icons,
      labels: ['图片', '分割线'],
      // labels: ['图片', '分割线', '视频', '代码块'],
      
      // names: ['insertImage', 'insertLine', 'insertVideo', 'insertBlock']
      names: ['insertImage', 'insertLine']
      
    }
  },
  props: ['insertImageClick', 'insertLink', 'insertLine', 'insertVideo', 'insertBlock', 'uploadImages'],
  methods: {
    handleFileChange () {
      let input = this.$refs.input[0]
      let files = input.files
      this.uploadImages(files)
    },
    insertClick (key, index) {
      if (this[key]) {
        this[key]()
      }
    }
  },
  watch: {
    textareaInner(newVal, oldVal) {
      console.log(newVal);
      console.log(oldVal);
    }
  }
}
</script>

<style type="text/css">

.insert-options-items {
  width: 120px;
  padding: 5px 0px;
  border-radius: 2px;
  position: absolute;
  box-shadow: 0 1px 2px #ccc;
  background-color: #fff;
  margin-top: 35px;
  text-align: left;
  z-index: 3;
}
.insert-options-items ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.insert-options-items ul li {
  padding: 5px 20px;
  color: #333;
  font-size: 12px;
  position: relative;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.insert-options-items ul li:hover, .insertActive {
  background-color: #E5E5E5;
}

.insert-options-items img {
  width: 20px;
  height: 20px;
  top: 1px;
  position: relative;
  margin-right: 15px;
}

.insert-options-items #images_upload {
  position: absolute;
  z-index: 2;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}
</style>
