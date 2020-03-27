<template>
  <div class="insert-video">
    <div class="insert-video-box">
      <span class="cancel" @click='cancelClick'>X</span>
      <p>插入视频</p>
      <input type="text" name="text" placeholder="复制通用代码 <iframe src='...'></iframe>" v-model='text' v-on:keyup.enter='conformClick'>
      <button @click='conformClick'> 确认</button>
    </div>
  </div>
</template>
<script type="text/javascript">
export default {
  name: 'InsertVideo',
  data () {
    return {
      text: ''
    }
  },
  props: ['insertVideo', 'cancel'],
  methods: {
    conformClick () {
      this.insertVideo(this.text)
    },
    cancelClick () {
      this.cancel()
    }
  },
  mounted () {
    document.querySelector('.insert-video-box input').focus()
  }
}
</script>

<style type="text/css">
.insert-video {
  position: fixed;
  top: 0;
  left: 0;
  background-color: rgba(255, 255, 255, 1);
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.insert-video-box {
  position: relative;
  padding: 30px 20px;
  border-radius: 6px;
  margin: 0 auto 24px;
  background-clip: padding-box;
  box-shadow: 0 2px 8px rgba(0,0,0,.2);
}
.insert-video p {
  text-align: center;
  padding: 20px 0px;
  font-size: 25px;
}
.insert-video input {
  text-align: left;
  margin: 5px 0px;
  border: 1px solid rgb(200,200,200);
  font-size: 14px;
  padding: 10px 5px;
  width: calc(100% - 10px);
}

.insert-video button {
  outline: none;
  cursor: pointer;
  margin-left: calc(50% - 40px);
  margin-top: 10px;
  width: 80px;
  height: 40px;
  border: none;
  text-align: center;
  color: #898989;
  background-color: #fff;
  font-size: 15px;
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
  box-shadow: 1px 2px 8px hsla(0,0%,70%,.8);
  transition-property: right;
  transition: all 0.3s;
}

.insert-video .cancel {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
  font-size: 20px;
  width: 40px;
  height: 40px;
  text-align: center;
  line-height: 40px;
  cursor: pointer;
}
</style>
